package com.dzics.data.business.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.ums.model.dao.SwitchSiteDo;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import com.dzics.data.common.base.enums.UserIdentityEnum;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.write.CustomCellWriteHandler;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.SnowflakeUtil;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.business.service.PmsService;
import com.dzics.data.pms.db.dao.DzProductDao;
import com.dzics.data.pms.model.dto.*;
import com.dzics.data.pms.model.entity.DzProductDetectionTemplate;
import com.dzics.data.pms.model.vo.*;
import com.dzics.data.pms.service.DzDetectionTemplateService;
import com.dzics.data.pms.service.DzProductDetectionTemplateService;
import com.dzics.data.ums.db.dao.SysDepartDao;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author xnb
 * @date 2022年02月10日 9:04
 */
@Service
@Slf4j
public class PmsServiceImpl implements PmsService {
    @Autowired
    private SnowflakeUtil snowflakeUtil;
    @Autowired
    private DzProductDao dzProductMapper;
    @Autowired
    private DzicsUserService userService;
    @Autowired
    private DzProductDetectionTemplateService templateService;
    @Autowired
    private DzDetectionTemplateService detectionTemplateService;
    @Autowired
    private SysDepartDao sysDepartDao;

    @Override
    public Result selDetectorItem(GroupId groupId, String sub) {
        DetectionTemplateParm parm = new DetectionTemplateParm();
        if (!groupId.getCheckModel()) {
            if (groupId != null && groupId.getDepartId() != null) {
                List<ProductParm> productParms = dzProductMapper.getByDepartId(groupId.getDepartId());
                if (CollectionUtils.isNotEmpty(productParms)) {
                    for (ProductParm productParm : productParms) {
                        productParm.setProductName(productParm.getProductName() + "-" + productParm.getLineType());
                    }
                }
                parm.setProducts(productParms);
                return new Result(CustomExceptionType.OK, parm);
            } else {
                return new Result(CustomExceptionType.OK, parm);
            }
        }
//        获取站点信息
        SysUser byUserName = userService.getByUserName(sub);
        Result result = userService.querySwitchSite(byUserName, true);
        parm.setDeparts(result.getData());
        if (groupId != null && groupId.getDepartId() != null) {
            List<ProductParm> productParms = dzProductMapper.getByDepartId(groupId.getDepartId());
            if (CollectionUtils.isNotEmpty(productParms)) {
                for (ProductParm productParm : productParms) {
                    productParm.setProductName(productParm.getProductName() + "-" + productParm.getLineType());
                }
            }
            parm.setProducts(productParms);
        }
        if (groupId != null && !StringUtils.isEmpty(groupId.getProductNo())) {
            parm.setProductNo(groupId.getProductNo());
            if (groupId.getEditingMode()) {
                List<DBDetectTempVo> tempVos = templateService.getEditDBDetectorItem(groupId.getGroupId());
                if (CollectionUtils.isNotEmpty(tempVos)) {
                    DBDetectTempVo dbDetectTempVo = tempVos.get(0);
                    String lineType = dbDetectTempVo.getLineType();
                    String orderId = dbDetectTempVo.getOrderId();
                    parm.setLineType(lineType);
                    parm.setOrderId(orderId);
                }
                parm.setDbDetectTempVos(tempVos);
            } else {
                List<DzDetectTempVo> tempVos = templateService.getEditDetectorItem(groupId.getGroupId());
                if (CollectionUtils.isNotEmpty(tempVos)) {
                    DzDetectTempVo dbDetectTempVo = tempVos.get(0);
                    String lineType = dbDetectTempVo.getLineType();
                    String orderId = dbDetectTempVo.getOrderId();
                    parm.setLineType(lineType);
                    parm.setOrderId(orderId);
                }
                parm.setDzDetectTempVos(tempVos);
            }

        }
        if (groupId != null && groupId.getGroupId() != null) {
            if (!StringUtils.isEmpty(groupId.getProductNo())) {
                String departId = getByProeuctNoDepartId(groupId.getProductNo());
                parm.setDepartId(departId);
            }
            return new Result(CustomExceptionType.OK, parm);
        } else {
            if (!groupId.getEditingMode()) {
                List<DzDetectTempVo> tempVos = detectionTemplateService.listDzDetectTempVo();
                parm.setDzDetectTempVos(tempVos);
            }
            return new Result(CustomExceptionType.OK, parm);
        }
    }

    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    @Override
    public Result addDetectorItem(AddDetectorPro detectorPro, String sub, String useDepartId) {
        SysUser byUserName = userService.getByUserName(sub);
        long groupId = snowflakeUtil.nextId();
        Integer count = templateService.getProductNo(detectorPro.getProductNo(),
                Long.valueOf(useDepartId), detectorPro.getOrderId(), detectorPro.getLineId());
        if (count == null || count > 0) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR34);
        }
        String productNo = detectorPro.getProductNo();
        List<DzDetectTempVo> detectionTemplates = detectorPro.getDetectionTemplates();
        List<DzProductDetectionTemplate> addtempLs = new ArrayList<>();
        for (DzDetectTempVo detectionTemplate : detectionTemplates) {
            DzProductDetectionTemplate addtemp = new DzProductDetectionTemplate();
            BeanUtils.copyProperties(detectionTemplate, addtemp);
            addtemp.setOrderId(detectorPro.getOrderId());
            addtemp.setLineId(detectorPro.getLineId());
            addtemp.setLineNo(detectorPro.getLineNo());
            addtemp.setOrderNo(detectorPro.getOrderNo());
            addtemp.setGroupId(String.valueOf(groupId));
            addtemp.setProductNo(productNo);
            addtemp.setDepartId(useDepartId);
            addtemp.setOrgCode(byUserName.getUseOrgCode());
            addtemp.setCreateBy(byUserName.getUsername());
            addtemp.setDetectionId(null);
            addtempLs.add(addtemp);

        }
        boolean save = templateService.save(addtempLs);
        if (save) {
            return new Result(CustomExceptionType.OK);
        }
        throw new CustomException(CustomExceptionType.SYSTEM_ERROR);
    }

    @Override
    public Result<List<LinkedHashMap<String, Object>>> queryProDetectorItem(ProDuctCheck proDuctCheck, String sub) {
        String orderId = proDuctCheck.getOrderId();
        String lineId = proDuctCheck.getLineId();
        Result result = new Result(CustomExceptionType.OK);
        SysUser byUserName = userService.getByUserName(sub);
//        归属站点id
        String affiliationDepartId = byUserName.getAffiliationDepartId();
        List<DzProductDetectionTemplateParms> templates = new ArrayList<>();
        SwitchSiteDo byOrgCode = sysDepartDao.getByOrgCode(byUserName.getUseOrgCode());
        PageHelper.startPage(proDuctCheck.getPage(), proDuctCheck.getLimit());
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode()) {
            if (byUserName.getUseOrgCode().equals(byUserName.getOrgCode())) {
                List<DzProductDetectionTemplateParms> listx = templateService.listGroupBy(proDuctCheck.getField(), proDuctCheck.getType(), proDuctCheck.getProductName(), null, orderId, lineId);
                PageInfo<DzProductDetectionTemplateParms> dzProductDetectionTemplatePageInfo = new PageInfo<>(listx);
                templates = dzProductDetectionTemplatePageInfo.getList();
                result.setCount(dzProductDetectionTemplatePageInfo.getTotal());
            } else {
                List<DzProductDetectionTemplateParms> listx = templateService.listGroupBy(proDuctCheck.getField(), proDuctCheck.getType(), proDuctCheck.getProductName(), byOrgCode.getId(), orderId, lineId);
                PageInfo<DzProductDetectionTemplateParms> dzProductDetectionTemplatePageInfo = new PageInfo<>(listx);
                templates = dzProductDetectionTemplatePageInfo.getList();
                result.setCount(dzProductDetectionTemplatePageInfo.getTotal());
            }
        } else {
            List<DzProductDetectionTemplateParms> listx = templateService.listGroupBy(proDuctCheck.getField(), proDuctCheck.getType(), proDuctCheck.getProductName(), affiliationDepartId, orderId, lineId);
            PageInfo<DzProductDetectionTemplateParms> dzProductDetectionTemplatePageInfo = new PageInfo<>(listx);
            templates = dzProductDetectionTemplatePageInfo.getList();
            result.setCount(dzProductDetectionTemplatePageInfo.getTotal());
        }
        List<LinkedHashMap<String, Object>> resp = new ArrayList<>();
        for (int ix = 0; ix < templates.size(); ix++) {
            DzProductDetectionTemplateParms detectionTemplateGr = templates.get(ix);
            String orderId1 = detectionTemplateGr.getOrderId();
            String lineId1 = detectionTemplateGr.getLineId();
            List<DzProductDetectionTemplate> onedzproTem = templateService.getByOrderNoProNo(Long.valueOf(detectionTemplateGr.getDepartId()),
                    detectionTemplateGr.getProductNo(),orderId1,lineId1);
            LinkedHashMap<String, Object> taberHeader = new LinkedHashMap<>();
            taberHeader.put("productName", detectionTemplateGr.getProductName());
            taberHeader.put("departId", detectionTemplateGr.getDepartId().toString());
            taberHeader.put("productNo", detectionTemplateGr.getProductNo());
            taberHeader.put("orderId", detectionTemplateGr.getOrderId());
            taberHeader.put("orderNo", detectionTemplateGr.getOrderNo());
            taberHeader.put("lineId", detectionTemplateGr.getLineId());
            taberHeader.put("lineNo", detectionTemplateGr.getLineNo());
            taberHeader.put("lineName", detectionTemplateGr.getLineName());
            for (int i1 = 0; i1 < onedzproTem.size(); i1++) {
                DzProductDetectionTemplate detectionTemplate = onedzproTem.get(i1);
                if (i1 == 0) {
                    taberHeader.put("groupId", detectionTemplate.getGroupId().toString());
                }
                taberHeader.put(detectionTemplate.getTableColVal(), detectionTemplate.getTableColCon());
                if ((i1 + 1) < 10) {
                    taberHeader.put("compensationValue0" + (i1 + 1), detectionTemplate.getCompensationValue());
                } else {
                    taberHeader.put("compensationValue" + (i1 + 1), detectionTemplate.getCompensationValue());
                }
            }
            resp.add(taberHeader);
        }
        result.setData(resp);
        return result;
    }


    @Override
    public Result delProDetectorItem(String groupId, String sub) {
        boolean bb = templateService.delGroupupId(groupId);
        if (bb) {
            return new Result(CustomExceptionType.OK);
        }
        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR0);
    }


    @Override
    public Result editProDetectorItem(EditProDuctTemp templateParm, String sub) {
        return templateService.editProDetectorItem(templateParm);
    }

    @Override
    public Result dbProDetectorItem(EditProDuctTemp editProDuctTemp, String sub) {
        return templateService.dbProDetectorItem(editProDuctTemp);
    }

    @Override
    public String getByProeuctNoDepartId(String productNo) {
        return dzProductMapper.getByProeuctNoDepartId(productNo);
    }

    public void downloadExcel(HttpServletResponse response, String fileName, String sheet, List<List<String>> header, List<List<String>> data) throws IOException {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
        fileName = URLEncoder.encode(fileName + DateUtil.dateFormatToStingYmdHms(new Date()) + ".xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        EasyExcel.write(response.getOutputStream()).registerWriteHandler(new CustomCellWriteHandler()).head(header).sheet(sheet).doWrite(data);
    }


}
