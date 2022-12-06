package com.dzics.data.pdm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.model.custom.MachiningNumTotal;
import com.dzics.data.common.base.model.vo.MachiningJC;
import com.dzics.data.common.base.model.vo.MachiningMessageStatus;
import com.dzics.data.pdm.db.dao.DzEquipmentProNumDao;
import com.dzics.data.pdm.db.dao.DzEquipmentWorkShiftDao;
import com.dzics.data.pdm.db.dao.DzProductionPlanDao;
import com.dzics.data.pdm.model.entity.DzEquipmentProNum;
import com.dzics.data.pdm.service.DzEquipmentProNumService;
import com.dzics.data.pdm.service.kanban.DeviceProNumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 设备生产数量表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Service
@Slf4j
@SuppressWarnings(value = "ALL")
public class DzEquipmentProNumServiceImpl extends ServiceImpl<DzEquipmentProNumDao, DzEquipmentProNum> implements DzEquipmentProNumService , DeviceProNumService<List<MachiningNumTotal>> {

    @Autowired
    private DzProductionPlanDao dzProductionLineService;

    @Autowired
    protected DzEquipmentProNumDao proNumMapper;

    @Autowired
    private DzEquipmentWorkShiftDao dzLineShiftDayService;


    @Override
    public void arrange() {
        dzProductionLineService.selectList(Wrappers.emptyWrapper());
    }

    @Override
    public DzEquipmentProNum getDzDayEqProNum(String orderNumber, String deviceId, String id, String productType, String batchNumber, String modelNumber, int hour) {
        QueryWrapper<DzEquipmentProNum> wp = new QueryWrapper<>();
        wp.eq("order_no", orderNumber);
        wp.eq("equiment_id", deviceId);
        wp.eq("day_id", id);
        wp.eq("product_type", productType);
        wp.eq("batch_number", batchNumber);
        wp.eq("model_number", modelNumber);
        wp.eq("work_hour", hour);
        wp.orderByDesc("create_time");
        List<DzEquipmentProNum> list = this.list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() > 1) {
                log.warn("一天同一个班次在班次生产记录表dz_equipment_pro_num存在多条记录：size:{}", list.size());
            }
            return list.get(0);
        }
        return null;

    }

    @Override
    public DzEquipmentProNum saveDzEqProNum(DzEquipmentProNum dzEqProNum) {
        dzEqProNum.setCreateTime(new Date());
        save(dzEqProNum);
        return dzEqProNum;
    }

    @Override
    public DzEquipmentProNum updateDzEqProNum(DzEquipmentProNum proNum) {
        proNum.setCreateTime(null);
        proNum.setDelFlag(null);
        proNum.setOrgCode(null);
        proNum.setUpdateTime(new Date());
        QueryWrapper<DzEquipmentProNum> wp = new QueryWrapper<>();
        wp.eq("order_no", proNum.getOrderNo());
        wp.eq("equiment_id", proNum.getEquimentId());
        wp.eq("day_id", proNum.getDayId());
        wp.eq("product_type", proNum.getProductType());
        wp.eq("batch_number", proNum.getBatchNumber());
        wp.eq("model_number", proNum.getModelNumber());
        wp.eq("work_hour", proNum.getWorkHour());
        update(proNum, wp);
        return proNum;
    }


    @Override
    public DzEquipmentProNum getDzEquipmentProNum(Long id) {
        QueryWrapper<DzEquipmentProNum> wp = new QueryWrapper<>();
        wp.eq("day_id", id);
        wp.orderByDesc("create_time");
        List<DzEquipmentProNum> lists = this.list(wp);
        if (CollectionUtils.isNotEmpty(lists)) {
            if (lists.size() == 1) {
                return lists.get(0);
            } else if (lists.size() > 1) {
                long roughNum = lists.stream().mapToLong(DzEquipmentProNum::getRoughNum).sum();
                long badnessNum = lists.stream().mapToLong(DzEquipmentProNum::getBadnessNum).sum();
                long nowNum = lists.stream().mapToLong(DzEquipmentProNum::getNowNum).sum();
                DzEquipmentProNum res = new DzEquipmentProNum();
                res.setRoughNum(roughNum);
                res.setBadnessNum(badnessNum);
                res.setNowNum(nowNum);
                return res;
            }
        }
        return null;
    }

    @Override
    public MachiningJC machiningNumTotals(MachiningJC jc, LocalDate now, String tableKey, String orderNo) {
        List<MachiningMessageStatus> status = jc.getMachiningMessageStatus();
        List<MachiningNumTotal> machiningNumTotalsX = new ArrayList<>();
        if (status != null) {
            List<String> collect = status.stream().map(s -> s.getEquimentId()).collect(Collectors.toList());
            List<MachiningNumTotal> machiningNumTotals = getEqIdData(now, collect, tableKey, orderNo);
            for (MachiningNumTotal machiningNumTotal : machiningNumTotals) {
                if (machiningNumTotal != null) {
                    machiningNumTotalsX.add(machiningNumTotal);
                }
            }
        }
        jc.setMachiningNumTotal(machiningNumTotalsX);
        return jc;
    }

    @Override
    public List<MachiningNumTotal> getEqIdData(LocalDate now, List<String> collect, String tableKey, String orderNo) {
//        获取总产列表
        List<MachiningNumTotal> total = proNumMapper.getEqIdDataTotal(collect, tableKey, orderNo);
//        获取日产列表
        List<MachiningNumTotal> day = proNumMapper.getEqIdData(now, collect, tableKey,orderNo);
        for (MachiningNumTotal machiningNumTotal : total) {
            for (MachiningNumTotal eqIdDatum : day) {
                if (machiningNumTotal.getEquimentId().equals(eqIdDatum.getEquimentId())) {
                    machiningNumTotal.setDayNum(eqIdDatum.getDayNum());
                    machiningNumTotal.setBadnessNum(eqIdDatum.getBadnessNum());
                    machiningNumTotal.setQualifiedNum(eqIdDatum.getQualifiedNum());
                    machiningNumTotal.setRoughNum(eqIdDatum.getRoughNum());
                    break;
                }
            }
        }
        return total;
    }
}
