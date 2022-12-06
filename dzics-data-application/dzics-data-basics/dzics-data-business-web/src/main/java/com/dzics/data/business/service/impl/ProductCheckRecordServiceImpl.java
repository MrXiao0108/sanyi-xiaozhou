package com.dzics.data.business.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.business.service.ProductCheckRecordService;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.write.CustomCellWriteHandler;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.pdm.db.dao.DzWorkpieceDataDao;
import com.dzics.data.pdm.db.model.dto.DetectorDataQuery;
import com.dzics.data.pms.db.dao.DzProductDao;
import com.dzics.data.pms.db.dao.DzProductDetectionTemplateDao;
import com.dzics.data.pms.model.entity.DzProduct;
import com.dzics.data.pms.model.entity.DzProductDetectionTemplate;
import com.dzics.data.pms.model.vo.HeaderClom;
import com.dzics.data.pms.model.vo.ProDetection;
import com.dzics.data.pub.db.dao.DzProductionLineDao;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Classname ProductCheckRecordServiceImpl
 * @Description 产品检测记录实现
 * @Date 2022/3/4 15:46
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class ProductCheckRecordServiceImpl implements ProductCheckRecordService {
    @Autowired
    private DzProductionLineDao lineDao;
    @Autowired
    private DzProductDetectionTemplateDao detectionTemplateDao;
    @Autowired
    private DzProductDao productDao;
    @Autowired
    private DzWorkpieceDataDao workpieceDataDao;

    @Override
    public Result selDetectorData(DetectorDataQuery detectorDataQuery, String sub) {
        String lineId = detectorDataQuery.getLineId();
        DzProductionLine dzProductionLine = lineDao.selectById(lineId);
        if (dzProductionLine == null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR592);
        }
        String orderId = dzProductionLine.getOrderId();
        String orderNo = dzProductionLine.getOrderNo();
        String lineNo = dzProductionLine.getLineNo();
        detectorDataQuery.setOrderNo(orderNo);
        detectorDataQuery.setLineNo(lineNo);
        String productNo = detectorDataQuery.getProductNo();
        //查询表头
        List<DzProductDetectionTemplate> templates = detectionTemplateDao.selOrderLineProductNo(productNo, 0, lineId, orderId);
        if (CollectionUtils.isEmpty(templates)) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR341);
        }
        ProDetection proDetection = new ProDetection();
        List<HeaderClom> tableColumn = proDetection.getTableColumn();
        for (DzProductDetectionTemplate dzProductDetectionTemplate : templates) {
            HeaderClom headerClom = new HeaderClom();
            headerClom.setColData(dzProductDetectionTemplate.getTableColVal());
            headerClom.setColName(dzProductDetectionTemplate.getTableColCon());
            tableColumn.add(headerClom);
        }
//        添加表头数据
        proDetection.setTableColumn(tableColumn);
        //1.查询产品相关信息
        DzProduct dzProduct = productDao.selectOne(new QueryWrapper<DzProduct>().eq("product_no", productNo));
        if (StringUtils.isEmpty(detectorDataQuery.getType())) {
            detectorDataQuery.setField("detector_time");
            detectorDataQuery.setType("DESC");
        }
        if(detectorDataQuery.getPage() != -1){
            PageHelper.startPage(detectorDataQuery.getPage(), detectorDataQuery.getLimit());
        }
        List<Map<String, Object>> resp = workpieceDataDao.selDetectorData(detectorDataQuery);
        PageInfo<Map<String, Object>> info = new PageInfo(resp);
        List<Map<String, Object>> list = info.getList();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> objectMap = list.get(i);
            objectMap.put("productName", dzProduct != null ? dzProduct.getProductName() : "默认产品");
            String res = "1".equals(objectMap.get("out_ok").toString()) ? "正常" : "异常";
            objectMap.put("detectionResult", res);
            String outOkName = "out_ok" + (i < 10 ? ("0" + i) : i);
            String detectName = "detect" + (i < 10 ? ("0" + i) : i);
            Object val = objectMap.get(outOkName);
            if (val != null && "0".equals(val.toString())) {
                objectMap.put(detectName, objectMap.get(detectName) + "::");
            }
        }
        proDetection.setTableData(list);
        proDetection.setTableColumn(tableColumn);
        return new Result(CustomExceptionType.OK, proDetection, info.getTotal());
    }

    @Override
    public void getDetectorExcel1(HttpServletResponse response, DetectorDataQuery detectorDataQuery, String sub) {
        try {
            String userIdentity = detectorDataQuery.getUserIdentity();
            if (userIdentity == null) {
                log.error("导出检测数据excel异常，userIdentity为空:{}", userIdentity);
                return;
            }
            detectorDataQuery.setPage(1);
            detectorDataQuery.setLimit(10000);
            Result result = selDetectorData(detectorDataQuery, sub);
            ProDetection proDetection = (ProDetection) result.getData();
            List<HeaderClom> tableColumn = new ArrayList<>();
            List<List<String>> header = new ArrayList<>();//头
            for (HeaderClom headerClom : proDetection.getTableColumn()) {
                List<String> head = new ArrayList<>();
                head.add(headerClom.getColName());
                if (userIdentity.equals("1")) {
                    tableColumn.add(headerClom);
                    header.add(head);
                } else if (!headerClom.getColData().equals("departName")) {
                    tableColumn.add(headerClom);
                    header.add(head);
                }
            }
            List<Map<String, Object>> tableData = proDetection.getTableData();//数据
            if (tableData.size() == 0) {
                log.warn("导出检测数据excel，没有查询到检测数据，搜索条件：{}", detectorDataQuery);
                return;
            }
            //在表中存放查询到的数据放入对应的列
            List<List<String>> data = new ArrayList<>();//数据
            for (int i = 0; i < tableData.size(); i++) {
                List<String> dataList = new ArrayList<>();
                Map<String, Object> map = tableData.get(i);
                for (int j = 0; j < tableColumn.size(); j++) {
                    String colData = tableColumn.get(j).getColData();
                    Object o1 = map.get(colData);
                    String o = o1 != null ? o1.toString() : "";

                    if (o != null && o.indexOf(":") > 0) {
                        o = o.substring(0, o.length() - 2);
                    }
                    dataList.add(o);
                }
                data.add(dataList);
            }
            //导出excel
            downloadExcel(response, "检测数据", "检测记录", header, data);
        } catch (Exception e) {
            log.error("导出检测数据Excel表格失败:{}", e.getMessage(), e);
        }

    }

    public void downloadExcel(HttpServletResponse response, String fileName, String sheet, List<List<String>> header, List<List<String>> data) throws IOException {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
        fileName = URLEncoder.encode(fileName + DateUtil.dateFormatToStingYmdHms(new Date()) + ".xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        EasyExcel.write(response.getOutputStream()).registerWriteHandler(new CustomCellWriteHandler()).head(header).sheet(sheet).doWrite(data);
    }
}
