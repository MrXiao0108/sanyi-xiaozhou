package com.dzics.data.kanban.changsha.changpaoguang.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.kanban.changsha.changpaoguang.vo.ProDetectionPaoGuang;
import com.dzics.data.pdm.model.dao.DzWorkpieceDataDo;
import com.dzics.data.pdm.service.DzWorkpieceDataService;
import com.dzics.data.pms.model.dto.TableColumnDto;
import com.dzics.data.pms.service.DzProductDetectionTemplateService;
import com.dzics.data.pub.service.kanban.DetectorDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Classname ZhanHuiDetectorDataServiceImpl
 * @Description 展会检测记录实现
 * @Date 2022/3/15 9:32
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class PaoGuangDetectorDataServiceImpl implements DetectorDataService<ProDetectionPaoGuang<List<DzWorkpieceDataDo>>> {

    @Autowired
    private DzWorkpieceDataService workpieceDataService;
    @Autowired
    private DzProductDetectionTemplateService dzProductDetectionTemplateService;

    /**
     * 产品检测数据
     *
     * @param orderNo
     * @param lineNo
     * @param modelNumber
     * @return
     */
    @Override
    public ProDetectionPaoGuang<List<DzWorkpieceDataDo>> getDetectorData(String orderNo, String lineNo, String modelNumber) {
        List<Map<String, String>> list = workpieceDataService.getNewestThreeDataId(orderNo, lineNo, modelNumber, 4);
        if (CollectionUtils.isNotEmpty(list)) {
            if (!list.isEmpty()) {
                List<DzWorkpieceDataDo> dataDos = new ArrayList<>();
                for (Map<String, String> objectMap : list) {
                    for (Map.Entry<String, String> stringStringEntry : objectMap.entrySet()) {
                        stringStringEntry.setValue(String.valueOf(stringStringEntry.getValue()));
                    }
                    String detectorTime = String.valueOf(objectMap.get("detectorTime"));
                    objectMap.put("detectorTime", detectorTime);
                    for (int i = 1; i < 29; i++) {
                        String key = i < 10 ? "0" + i : i + "";
                        String val = String.valueOf(objectMap.get("out_ok" + key));
                        if (val != null && val.toString().equals("0")) {
                            objectMap.put("detect" + key, String.valueOf(objectMap.get("detect" + key)) + "::");
                        }
                    }
                    try {
                        DzWorkpieceDataDo dataDo = new DzWorkpieceDataDo();
                        BeanUtils.populate(dataDo, objectMap);
                        dataDos.add(dataDo);
                    } catch (Throwable throwable) {
                        log.error("类型转换错误:{}", throwable.getMessage(), throwable);
                    }
                }
                String productNo = list.get(0).get("productNo").toString();
                List<TableColumnDto> templates = dzProductDetectionTemplateService.listProductNo(productNo, orderNo, lineNo);
                if (CollectionUtils.isEmpty(templates)) {
                    templates = dzProductDetectionTemplateService.getDefoutDetectionTemp();
                }
                ProDetectionPaoGuang<List<DzWorkpieceDataDo>> proDetection = new ProDetectionPaoGuang();
                proDetection.setTableColumn(templates);
                proDetection.setTableData(dataDos);
                return proDetection;
            }
        }
        return null;
    }
}
