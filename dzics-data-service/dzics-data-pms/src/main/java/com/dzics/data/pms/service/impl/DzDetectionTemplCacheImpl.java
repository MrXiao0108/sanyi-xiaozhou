package com.dzics.data.pms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.db.dao.DzDetectionTemplateDao;
import com.dzics.data.pms.db.dao.DzDetectorDataDao;
import com.dzics.data.pms.db.dao.DzProductDao;
import com.dzics.data.pms.db.dao.DzProductDetectionTemplateDao;
import com.dzics.data.pms.model.dto.DbProDuctileEditer;
import com.dzics.data.pms.model.dto.DetectionData;
import com.dzics.data.pms.model.dto.EditProDuctTemp;
import com.dzics.data.pms.model.dto.ProductParm;
import com.dzics.data.pms.model.entity.DzProduct;
import com.dzics.data.pms.model.entity.DzProductDetectionTemplate;
import com.dzics.data.pms.model.vo.DBDetectTempVo;
import com.dzics.data.pms.model.vo.DzDetectTempVo;
import com.dzics.data.pms.service.DzDetectionTemplCache;
import com.dzics.data.pms.service.DzProductDetectionTemplateService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@SuppressWarnings("ALL")
@Service
@Slf4j
public class DzDetectionTemplCacheImpl implements DzDetectionTemplCache {

    @Autowired
    private DzDetectionTemplateDao dzDetectionTemplateMapper;
    @Autowired
    private DzProductDetectionTemplateDao productDetectionTemplateMapper;
    @Autowired
    private DzProductDetectionTemplateService detectionTemplateService;
    @Autowired
    private DzDetectorDataDao dzDetectorDataMapper;
    @Autowired
    DzProductDao dzProductMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzDetectionTemplCache dzDetectionTemplCache;



    @Override
    public List<DzDetectTempVo> list() {
        return dzDetectionTemplateMapper.listDzDetectTempVo();
    }

    @Override
    public List<DzProductDetectionTemplate> getByOrderNoProNo(Long departId, String productNo, String orderId1, String lineId1) {
        QueryWrapper<DzProductDetectionTemplate> wp = new QueryWrapper<>();
        wp.eq("product_no", productNo);
        wp.eq("depart_id", departId);
        wp.eq("order_id", orderId1);
        wp.eq("line_id", lineId1);
        wp.select("table_col_con", "table_col_val", "compensation_value", "detection_id", "group_Id");
        List<DzProductDetectionTemplate> templates = productDetectionTemplateMapper.selectList(wp);
        return templates;
    }


    @Override
    public List<Map<String, Object>> getGroupKey(List<String> groupKey) {
        return dzDetectorDataMapper.getGroupKey(groupKey);
    }

    @Override
    public List<DetectionData> getDataValue(String groupKey) {
        return dzDetectorDataMapper.getDataValue(groupKey);
    }

    @Override
    public List<DzDetectTempVo> getEditDetectorItem(String groupId) {
        List<DzDetectTempVo> vos = productDetectionTemplateMapper.groupById(groupId);
        return vos;
    }

    @Override
    public List<ProductParm> getByDepartId(Long departId) {
        return dzProductMapper.getByDepartId(departId);
    }

    @Override
    public String getByProeuctNoDepartId(String productNo) {
        return dzProductMapper.getByProeuctNoDepartId(productNo);
    }

    @Override
    public Result editProDetectorItem(EditProDuctTemp templateParm) {
        List<DbProDuctileEditer> dzDetectTempVos = templateParm.getDzDetectTempVos();
        List<DzProductDetectionTemplate> update = new ArrayList<>();
        for (DbProDuctileEditer dzDetectTempVo : dzDetectTempVos) {
            DzProductDetectionTemplate up = new DzProductDetectionTemplate();
            BeanUtils.copyProperties(dzDetectTempVo, up);
            up.setDepartId(templateParm.getDepartId());
            up.setProductNo(templateParm.getProductNo());
            update.add(up);
        }
        detectionTemplateService.updateBatchById(update);
        if (CollectionUtils.isNotEmpty(dzDetectTempVos)) {
            DzProductDetectionTemplate byId = detectionTemplateService.getById(dzDetectTempVos.get(0).getDetectionId());
            if (byId != null) {
                redisUtil.del(RedisKey.TEST_ITEM + byId.getOrderNo() + byId.getLineNo() + templateParm.getProductNo());
            }
        }

        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result dbProDetectorItem(EditProDuctTemp editProDuctTemp) {
        List<DbProDuctileEditer> dzDetectTempVos = editProDuctTemp.getDbDetectTempVos();
        List<DzProductDetectionTemplate> update = new ArrayList<>();
        for (DbProDuctileEditer dzDetectTempVo : dzDetectTempVos) {
            DzProductDetectionTemplate up = new DzProductDetectionTemplate();
            BeanUtils.copyProperties(dzDetectTempVo, up);
            up.setDepartId(editProDuctTemp.getDepartId());
            up.setProductNo(editProDuctTemp.getProductNo());
            update.add(up);
        }
        detectionTemplateService.updateBatchById(update);
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public List<DBDetectTempVo> getEditDBDetectorItem(String groupId) {
        List<DBDetectTempVo> vos = productDetectionTemplateMapper.geteditdbdetectoritem(groupId);
        return vos;
    }

    @Override
    public boolean delGroupupId(Long groupId) {
        QueryWrapper<DzProductDetectionTemplate> wp = new QueryWrapper<>();
        wp.eq("group_Id", groupId);
        int delete = productDetectionTemplateMapper.delete(wp);
        return delete > 1 ? true : false;
    }

    @Override
    public List<DzProductDetectionTemplate> getByOrderNoProNoIsShow(long departId, String productNo) {
        QueryWrapper<DzProductDetectionTemplate> wp = new QueryWrapper<>();
        wp.eq("product_no", productNo);
        wp.eq("depart_id", departId);
        wp.eq("is_show", 0);
        wp.select("table_col_con", "table_col_val");
        List<DzProductDetectionTemplate> templates = productDetectionTemplateMapper.selectList(wp);
        return templates;
    }

    @Override
    public void deleteLineIdByOrderNoLineNo() {}

    @Override
    public BigDecimal getProductNameFrequency(String lineType, String productAlias) {
        QueryWrapper<DzProduct> dz = new QueryWrapper<>();
        dz.eq("line_type", lineType);
        dz.eq("product_name", productAlias);
        DzProduct dzProduct = dzProductMapper.selectOne(dz);
        if (dzProduct != null) {
            BigDecimal frequency = dzProduct.getFrequency();
            if (frequency != null) {
                return frequency;
            }
        }
        return new BigDecimal(0);
    }


    @Override
    public Result delProDetectorItem(Long groupId, String sub) {
        boolean bb = dzDetectionTemplCache.delGroupupId(groupId);
        if (bb) {
            return new Result(CustomExceptionType.OK);
        }
        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR0);
    }

}
