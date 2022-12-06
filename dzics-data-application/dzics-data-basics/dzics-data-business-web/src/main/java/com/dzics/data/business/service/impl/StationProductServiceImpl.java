package com.dzics.data.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.business.model.dto.locationartifacts.AddLocationArtifactsVo;
import com.dzics.data.business.model.dto.locationartifacts.PutLocationArtifactsVo;
import com.dzics.data.business.model.vo.locationartifacts.GetLocationArtifactsByIdDo;
import com.dzics.data.business.service.StationProductService;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.db.dao.DzProductDao;
import com.dzics.data.pms.db.dao.DzProductDetectionTemplateDao;
import com.dzics.data.pms.model.dto.Products;
import com.dzics.data.pms.model.entity.DzProduct;
import com.dzics.data.pms.model.vo.DzDetectTempVo;
import com.dzics.data.pub.db.dao.DzWorkingStationProductDao;
import com.dzics.data.pub.model.entity.DzWorkStationTemplate;
import com.dzics.data.pub.model.entity.DzWorkingStationProduct;
import com.dzics.data.pub.service.DzWorkStationTemplateService;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname StationProductServiceImpl
 * @Description 描述
 * @Date 2022/3/8 10:21
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class StationProductServiceImpl implements StationProductService {

    @Autowired
    private DzicsUserService userService;
    @Autowired
    private DzWorkingStationProductDao stationProductDao;
    @Autowired
    private DzProductDao productDao;
    @Autowired
    private DzWorkStationTemplateService stationTemplateService;
    @Autowired
    private DzProductDetectionTemplateDao detectionTemplateDao;
    @Override
    public Result add(AddLocationArtifactsVo addLocationArtifactsVo, String sub) {
        SysUser byUserName = userService.getByUserName(sub);
//        工件id
        String productId = addLocationArtifactsVo.getProductId();
//        工位id
        String workingProcedureId = addLocationArtifactsVo.getWorkingStationId();
        QueryWrapper<DzWorkingStationProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("working_station_id", workingProcedureId);
        wrapper.eq("product_id", productId);
        List<DzWorkingStationProduct> dzWorkingStationProducts = stationProductDao.selectList(wrapper);
        if (dzWorkingStationProducts.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_118);
        }
//        工位ID-产品ID 关系
        List<DzDetectTempVo> dzDetectTempVos = addLocationArtifactsVo.getDzDetectTempVos();
        DzWorkingStationProduct workingProcedureProduct = new DzWorkingStationProduct();
        workingProcedureProduct.setProductId(Long.valueOf(productId));
        workingProcedureProduct.setWorkingStationId(workingProcedureId);
//      保存工位产品关系
        stationProductDao.insert(workingProcedureProduct);
//      保存检测项 和产品关系 ，  （工位ID 和 产品ID） 关系表 ID
        List<DzWorkStationTemplate> workDetectionTemplates = new ArrayList<>();
        dzDetectTempVos.stream().forEach(dzDetectTempVo -> {
            if (dzDetectTempVo.getIsShow() != null && dzDetectTempVo.getIsShow().intValue() == 0) {
                DzWorkStationTemplate workDetectionTemplate = new DzWorkStationTemplate();
                workDetectionTemplate.setWorkStationProductId(workingProcedureProduct.getWorkStationProductId());
                workDetectionTemplate.setDetectionId(Long.valueOf(dzDetectTempVo.getDetectionId()));
                workDetectionTemplate.setDetectionGroupId(dzDetectTempVo.getGroupId());
                workDetectionTemplate.setOrgCode(byUserName.getUseOrgCode());
                workDetectionTemplate.setDelFlag(false);
                workDetectionTemplate.setCreateBy(byUserName.getUsername());
                workDetectionTemplates.add(workDetectionTemplate);
            }
        });
        stationTemplateService.saveBatch(workDetectionTemplates);
        return Result.ok();
    }


    @Override
    public Result<GetLocationArtifactsByIdDo> selEditProcedureProduct(String orderId, String lineId, String workStationProductId, String sub) {
        DzWorkingStationProduct procedureProduct = stationProductDao.selectById(workStationProductId);
        DzProduct byId = productDao.selectById(procedureProduct.getProductId());
//        获取该产品所有检测配置项
        List<DzDetectTempVo> tempVos = detectionTemplateDao.productId(orderId, lineId, byId.getProductNo());
//        获取已经配置的展示的检测项
        QueryWrapper<DzWorkStationTemplate> wp = new QueryWrapper<>();
        wp.eq("work_station_product_id", workStationProductId);
        List<DzWorkStationTemplate> list = stationTemplateService.list(wp);
        if (CollectionUtils.isNotEmpty(tempVos)) {
            for (DzDetectTempVo tempVo : tempVos) {
                tempVo.setIsShow(1);
                if (CollectionUtils.isNotEmpty(list)) {
                    String detectionId = tempVo.getDetectionId();
                    for (DzWorkStationTemplate workDetectionTemplate : list) {
                        String detectionIdX = workDetectionTemplate.getDetectionId().toString();
                        if (detectionId.equals(detectionIdX)) {
                            tempVo.setIsShow(0);
                            break;
                        }

                    }
                }
            }
        }
        GetLocationArtifactsByIdDo proceduct = new GetLocationArtifactsByIdDo();
        proceduct.setDzDetectTempVos(tempVos);
        Products products = new Products();
        products.setProductId(byId.getProductId() != null ? byId.getProductId().toString() : "");
        products.setOrderId(byId.getOrderId() != null ? byId.getOrderId().toString() : "");
        products.setOrderNo(byId.getOrderNo());
        products.setDepartId(byId.getDepartId() != null ? byId.getDepartId().toString() : "");
        products.setProductName(byId.getProductName());
        products.setProductNo(byId.getProductNo());
        products.setPicture(byId.getPicture());
        proceduct.setProduct(products);
        proceduct.setWorkStationProductId(workStationProductId);
        return Result.ok(proceduct);
    }


    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result updateProcedureProduct(PutLocationArtifactsVo putLocationArtifactsVo, String sub) {
        SysUser byUserName = userService.getByUserName(sub);
        String productId = putLocationArtifactsVo.getProductId();
        String workStationProductId = putLocationArtifactsVo.getWorkStationProductId();

        QueryWrapper<DzWorkStationTemplate> wp = new QueryWrapper<>();
        wp.eq("work_station_product_id", workStationProductId);
        stationTemplateService.remove(wp);

        List<DzDetectTempVo> dzDetectTempVos = putLocationArtifactsVo.getDzDetectTempVos();
        DzWorkingStationProduct workingStationProduct = new DzWorkingStationProduct();
        workingStationProduct.setWorkStationProductId(workStationProductId);
        workingStationProduct.setProductId(Long.valueOf(productId));
        stationProductDao.updateById(workingStationProduct);
        //      保存检测项 和产品关系
        List<DzWorkStationTemplate> workDetectionTemplates = new ArrayList<>();
        dzDetectTempVos.stream().forEach(dzDetectTempVo -> {
            if (dzDetectTempVo.getIsShow().intValue() == 0) {
                DzWorkStationTemplate workDetectionTemplate = new DzWorkStationTemplate();
                workDetectionTemplate.setWorkStationProductId(workStationProductId);
                workDetectionTemplate.setDetectionId(Long.valueOf(dzDetectTempVo.getDetectionId()));
                workDetectionTemplate.setDetectionGroupId(dzDetectTempVo.getGroupId());
                workDetectionTemplate.setOrgCode(byUserName.getUseOrgCode());
                workDetectionTemplate.setDelFlag(false);
                workDetectionTemplate.setCreateBy(byUserName.getUsername());
                workDetectionTemplates.add(workDetectionTemplate);
            }
        });
        stationTemplateService.saveBatch(workDetectionTemplates);
        return Result.ok();
    }


    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result delWorkStationProductId(String workStationProductId, String sub) {
        stationProductDao.deleteById(workStationProductId);
        QueryWrapper<DzWorkStationTemplate> wp = new QueryWrapper<>();
        wp.eq("work_station_product_id", workStationProductId);
        stationTemplateService.remove(wp);
        return Result.ok();
    }
}
