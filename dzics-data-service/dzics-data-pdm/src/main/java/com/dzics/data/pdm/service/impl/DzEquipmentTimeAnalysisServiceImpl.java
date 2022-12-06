package com.dzics.data.pdm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.pdm.db.dao.DzEquipmentTimeAnalysisDao;
import com.dzics.data.pdm.model.entity.DzEquipmentTimeAnalysis;
import com.dzics.data.pdm.model.vo.DeviceStateDetails;
import com.dzics.data.pdm.service.DzEquipmentTimeAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-10-11
 */
@Service
@Slf4j
public class DzEquipmentTimeAnalysisServiceImpl extends ServiceImpl<DzEquipmentTimeAnalysisDao, DzEquipmentTimeAnalysis> implements DzEquipmentTimeAnalysisService {
    @Override
    public Long getEquipmentAvailable(String id, String orderNo) {
        Long runTime = 0L;
        QueryWrapper<DzEquipmentTimeAnalysis> wrapper = new QueryWrapper();
        wrapper.eq("device_id", id);
        wrapper.eq("stop_data", LocalDate.now());
        wrapper.eq("work_state", 1);
        wrapper.eq("order_no", orderNo);
        wrapper.select("work_state", "duration", "stop_time", "reset_time");
        List<DzEquipmentTimeAnalysis> list = list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            for (DzEquipmentTimeAnalysis dzEquipmentTimeAnalysis : list) {
                runTime += dzEquipmentTimeAnalysis.getDuration();
                if (dzEquipmentTimeAnalysis.getResetTime() == null) {
                    long time = System.currentTimeMillis() - dzEquipmentTimeAnalysis.getStopTime().getTime();
                    if (time > 0) {
                        runTime += time;
                    }
                }
            }
        }
        return runTime;
    }

    @Override
    public DzEquipmentTimeAnalysis getResetTimeIsNull(String deviceId) {
        QueryWrapper<DzEquipmentTimeAnalysis> wp = new QueryWrapper<>();
        wp.eq("device_id", deviceId);
        wp.isNull("reset_time");
        List<DzEquipmentTimeAnalysis> dzEquipmentTimeAnalyses = baseMapper.selectList(wp);
        if (CollectionUtils.isNotEmpty(dzEquipmentTimeAnalyses)) {
            if (dzEquipmentTimeAnalyses.size() > 1) {
                log.warn("设备ID:{} 存在多条未设置结束时间记录: dzEquipmentTimeAnalyses: {}", deviceId, dzEquipmentTimeAnalyses);
            }
            return dzEquipmentTimeAnalyses.get(0);
        }
        return null;
    }


    @Override
    public List<DeviceStateDetails> getDeviceStateDetailsStopTime(Date date, String deviceId, String orderNo) {
        return baseMapper.getDeviceStateDetailsStopTime(date, deviceId, orderNo);
    }

    @Override
    public Date getUpdateTimeDesc() {
        return baseMapper.getUpdateTimeDesc();
    }

    @Override
    public List<DzEquipmentTimeAnalysis> getRestTimeIsNull(String shardingParameter) {
        List<DzEquipmentTimeAnalysis> list = baseMapper.getResetTimeIsNull(shardingParameter);
        return list;
    }

    @Override
    public List<DeviceStateDetails> getDeviceAnalysisTime(Date stopTime, String deviceId, String orderNo) {
        return baseMapper.getDeviceAnalysisTime(stopTime, deviceId, orderNo);
    }

    @Override
    public void saveTimeAnlysis(DzEquipmentTimeAnalysis analysis) {
        save(analysis);
    }

    @Override
    public void updateByIdTimeAnalysis(DzEquipmentTimeAnalysis timeAnalysis) {
        QueryWrapper<DzEquipmentTimeAnalysis> wp = new QueryWrapper<>();
        wp.eq("id", timeAnalysis.getId());
        wp.eq("device_id", timeAnalysis.getDeviceId());
        wp.eq("order_no", timeAnalysis.getOrderNo());
        update(timeAnalysis, wp);
    }

    @Override
    public List<DzEquipmentTimeAnalysis> getRestTimeIsNullDeviceId(String deviceId) {
        QueryWrapper<DzEquipmentTimeAnalysis> wp = new QueryWrapper<>();
        wp.isNull("reset_time");
        wp.eq("device_id", deviceId);
        return list(wp);
    }

    @Override
    public void updateByIdList(List<DzEquipmentTimeAnalysis> analysisList) {
        for (DzEquipmentTimeAnalysis timeAnalysis : analysisList) {
            QueryWrapper<DzEquipmentTimeAnalysis> wp = new QueryWrapper<>();
            wp.eq("id", timeAnalysis.getId());
            wp.eq("device_id", timeAnalysis.getDeviceId());
            wp.eq("order_no", timeAnalysis.getOrderNo());
            update(timeAnalysis, wp);
        }
    }

    @Override
    public void insTimeAnalysis(List<DzEquipmentTimeAnalysis> inst) {
        saveBatch(inst);
    }

    @Override
    public Date robTimeAnalysis(String type) {
        Date date = getUpdateTimeDesc();
        return date;
    }

    @Override
    public Date updateRobTimeAnalysis(String type, Date nowDate) {
        return nowDate;
    }

    @Override
    public BigDecimal getEquipmentSumRunTime(String orderNo, String deviceId, LocalDate workData, LocalTime startTime) {
        return this.baseMapper.getEquipmentSumRunTime(orderNo, deviceId, workData, startTime);
    }
}
