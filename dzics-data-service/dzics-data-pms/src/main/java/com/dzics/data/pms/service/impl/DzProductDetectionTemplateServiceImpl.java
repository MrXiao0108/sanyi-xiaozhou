package com.dzics.data.pms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.db.dao.DzProductDetectionTemplateDao;
import com.dzics.data.pms.model.dto.DbProDuctileEditer;
import com.dzics.data.pms.model.dto.DzProductDetectionTemplateParms;
import com.dzics.data.pms.model.dto.EditProDuctTemp;
import com.dzics.data.pms.model.dto.TableColumnDto;
import com.dzics.data.pms.model.entity.DzProductDetectionTemplate;
import com.dzics.data.pms.model.vo.DBDetectTempVo;
import com.dzics.data.pms.model.vo.DzDetectTempVo;
import com.dzics.data.pms.service.DzProductDetectionTemplateService;
import com.dzics.data.redis.util.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品检测设置默认模板 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@SuppressWarnings("ALL")
@Service
public class DzProductDetectionTemplateServiceImpl extends ServiceImpl<DzProductDetectionTemplateDao, DzProductDetectionTemplate> implements DzProductDetectionTemplateService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzProductDetectionTemplateDao detectionTemplateMapper;
    @Autowired
    private DzProductDetectionTemplateService productDetectionTemplateService;

    @Override
    public Result selProductTemplate(String orderId, String lineId, String productNo, String sub) {
        List<DzDetectTempVo> tempVos = selProductTemplateProductId(orderId,lineId,productNo);
        return Result.ok(tempVos, tempVos != null ? Long.valueOf(tempVos.size()) : 0L);
    }
    @Override
    public Result getByProductId(String productNo) {
        QueryWrapper<DzProductDetectionTemplate> eq = new QueryWrapper<DzProductDetectionTemplate>().eq("product_no", productNo).eq("is_show", 0);
        eq.select("detection_id", "table_col_con", "table_col_val");
        List<DzProductDetectionTemplate> dzProductDetectionTemplates = detectionTemplateMapper.selectList(eq);
        return new Result(CustomExceptionType.OK, dzProductDetectionTemplates, dzProductDetectionTemplates.size());
    }

    @Override
    public List<DzProductDetectionTemplateParms> listGroupBy(String field, String type, String productName, String departId, String orderId, String lineId) {
        List<DzProductDetectionTemplateParms> templates = detectionTemplateMapper.listGroupBy(field, type, productName, departId, orderId, lineId);
        return templates;
    }

    @Override
    public List<TableColumnDto> listProductNo(String productNo, String orderNo, String lineNo) {
        return detectionTemplateMapper.listMap(productNo, orderNo, lineNo);
    }

    @Override
    public List<DzDetectTempVo> selProductTemplateProductId(String orderId, String lineId, String productNo) {
        List<DzDetectTempVo> vos = detectionTemplateMapper.productId(orderId, lineId, productNo);
        return vos;
    }

    @Override
    public DzProductDetectionTemplate getProductNoItem(String productNo, String item) {
        return detectionTemplateMapper.getProductNoItem(productNo, item);

    }

    @Override
    public List<Map<String, Object>> listProductId(String productNo) {
        return detectionTemplateMapper.listProductIdMap(productNo);
    }

    @Override
    public List<TableColumnDto> getDefoutDetectionTemp() {
        return detectionTemplateMapper.getDefoutDetectionTemp();
    }

    @Override
    public Integer getProductNo(String productNo, Long departId, String orderId, String lineId) {
        QueryWrapper<DzProductDetectionTemplate> wp = new QueryWrapper<>();
        wp.eq("depart_id", departId);
        wp.eq("product_no", productNo);
        wp.eq("order_id", orderId);
        wp.eq("line_id", lineId);
        return productDetectionTemplateService.count(wp);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean save(List<DzProductDetectionTemplate> addtempLs) {
        return productDetectionTemplateService.saveBatch(addtempLs);
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
        productDetectionTemplateService.updateBatchById(update);
        if (CollectionUtils.isNotEmpty(dzDetectTempVos)) {
            DzProductDetectionTemplate byId = productDetectionTemplateService.getById(dzDetectTempVos.get(0).getDetectionId());
            if (byId != null) {
                redisUtil.del(RedisKey.TEST_ITEM + byId.getOrderNo() + byId.getLineNo() + templateParm.getProductNo());
            }
        }
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public boolean delGroupupId(String groupId) {
        QueryWrapper<DzProductDetectionTemplate> wp = new QueryWrapper<>();
        wp.eq("group_Id", groupId);
        boolean remove = remove(wp);
        return remove;
    }

    @Override
    public List<DBDetectTempVo> getEditDBDetectorItem(String groupId) {
        List<DBDetectTempVo> vos = detectionTemplateMapper.geteditdbdetectoritem(groupId);
        return vos;
    }


    @Override
    public List<DzDetectTempVo> getEditDetectorItem(String groupId) {
        List<DzDetectTempVo> vos = detectionTemplateMapper.groupById(groupId);
        return vos;
    }

    @Override
    public List<DzProductDetectionTemplate> getByOrderNoProNo(Long departId, String productNo, String orderId1, String lineId1) {
        QueryWrapper<DzProductDetectionTemplate> wp = new QueryWrapper<>();
        wp.eq("product_no", productNo);
        wp.eq("depart_id", departId);
        wp.eq("order_id", orderId1);
        wp.eq("line_id", lineId1);
        wp.select("table_col_con", "table_col_val", "compensation_value", "detection_id", "group_Id");
        List<DzProductDetectionTemplate> templates = detectionTemplateMapper.selectList(wp);
        return templates;
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
        updateBatchById(update);
        return new Result(CustomExceptionType.OK);
    }

}
