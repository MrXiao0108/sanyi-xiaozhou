package com.dzics.data.pdm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.pdm.db.dao.DzEquipmentProNumSignalDao;
import com.dzics.data.pdm.model.entity.DzEquipmentProNumSignal;
import com.dzics.data.pdm.model.entity.DzLineShiftDay;
import com.dzics.data.pdm.service.DzEquipmentProNumSignalService;
import com.dzics.data.pdm.service.DzLineShiftDayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 班次生产记录表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-23
 */
@Service
@Slf4j
public class DzEquipmentProNumSignalServiceImpl extends ServiceImpl<DzEquipmentProNumSignalDao, DzEquipmentProNumSignal> implements DzEquipmentProNumSignalService {

    @Autowired
    private DzLineShiftDayService dzLineShiftDayService;

    @Override
    public void updateDzEqProNum(DzEquipmentProNumSignal signal) {
        signal.setCreateTime(null);
        signal.setDelFlag(null);
        signal.setOrgCode(null);
        signal.setUpdateTime(new Date());
        QueryWrapper<DzEquipmentProNumSignal> wp = new QueryWrapper<>();
        wp.eq("order_no",signal.getOrderNo());
        wp.eq("equiment_id",signal.getEquimentId());
        wp.eq("day_id", signal.getDayId());
        wp.eq("product_type", signal.getProductType());
        wp.eq("batch_number", signal.getBatchNumber());
        wp.eq("model_number", signal.getModelNumber());
        wp.eq("work_hour", signal.getWorkHour());
        this.update(signal, wp);
    }

    @Override
    public DzEquipmentProNumSignal saveDzEqProNum(DzEquipmentProNumSignal dzEquipmentProNumSignal) {
        dzEquipmentProNumSignal.setCreateTime(new Date());
        save(dzEquipmentProNumSignal);
        return dzEquipmentProNumSignal;
    }

    @Override
    public Long getEquimentIdDayProNum(Long id, LocalDate nowDay, String tableKey) {
        Long sumDay = baseMapper.getEquimentIdDayProNum(id, nowDay, tableKey);
        return sumDay == null ? 0 : sumDay;
    }


    @Override
    public DzEquipmentProNumSignal getDzDayEqProNumSig(String orderNumber, String deviceId, String id, String productType, String batchNumber, String modelNumber, int hour) {
        QueryWrapper<DzEquipmentProNumSignal> wp = new QueryWrapper<>();
        wp.eq("order_no",orderNumber);
        wp.eq("equiment_id",deviceId);
        wp.eq("day_id", id);
        wp.eq("product_type", productType);
        wp.eq("batch_number", batchNumber);
        wp.eq("model_number", modelNumber);
        wp.eq("work_hour", hour);
        wp.orderByDesc("create_time");
        List<DzEquipmentProNumSignal> lists = this.list(wp);
        if (CollectionUtils.isNotEmpty(lists)) {
            if (lists.size() > 1) {
                log.warn("一天同一个班次在班次生产记录表dz_equipment_pro_num存在多条记录：size:{}", lists.size());
            }
            return lists.get(0);
        }
        return null;
    }

    @Override
    public DzEquipmentProNumSignal updateDzEqProNumSignal(DzEquipmentProNumSignal dzEquipmentProNumSignal) {
        updateDzEqProNum(dzEquipmentProNumSignal);
        return dzEquipmentProNumSignal;
    }

    @Override
    public DzEquipmentProNumSignal saveDzEqProNumSignal(DzEquipmentProNumSignal dzEquipmentProNumSignal) {
        DzEquipmentProNumSignal numSignal = saveDzEqProNum(dzEquipmentProNumSignal);
        return numSignal;
    }

    @Override
    public BigDecimal getSumQuaNum(GetOrderNoLineNo getOrderNoLineNo,String equipmentId, String orderNo) {
        //获取当前班次
        LocalDate now = LocalDate.now();
        LocalTime time = LocalTime.now();
        DzLineShiftDay lingShiftDay = dzLineShiftDayService.getLingShifuDay(equipmentId, getOrderNoLineNo.getLineNo(), "01", String.valueOf(EquiTypeEnum.JQR.getCode()), orderNo, now, time);
        if (lingShiftDay == null) {
            log.error("获取生产节拍时 当前班次为空，设备id：{}，产线号：{},订单号：{},设备编号：{},设备类型:{},日期：{},时间：{}", equipmentId, getOrderNoLineNo.getLineNo(), orderNo, "01", EquiTypeEnum.JQR.getCode(), now, time);
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(baseMapper.getSumQuaNum(lingShiftDay.getId(), equipmentId, orderNo));
    }
}
