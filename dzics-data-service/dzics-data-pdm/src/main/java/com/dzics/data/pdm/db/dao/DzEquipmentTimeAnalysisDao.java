package com.dzics.data.pdm.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.dzics.data.pdm.model.entity.DzEquipmentTimeAnalysis;
import com.dzics.data.pdm.model.vo.DeviceStateDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-10-11
 */
@Mapper
public interface DzEquipmentTimeAnalysisDao extends BaseMapper<DzEquipmentTimeAnalysis> {

    /**
     * 设备用时分析
     * @param stopTime
     * @param deviceId
     * @param orderNo
     * @return
     */
    @Deprecated
    List<DeviceStateDetails> getDeviceStateDetailsStopTime(@Param("stopTime") Date stopTime, @Param("deviceId") String deviceId, @Param("orderNo") String orderNo);

    /**
     * 设备用时分析
     * @param stopTime
     * @param deviceId
     * @param orderNo
     * @return
     */
    List<DeviceStateDetails> getDeviceAnalysisTime(@Param("stopTime") Date stopTime, @Param("deviceId") String deviceId, @Param("orderNo") String orderNo);

    Date getUpdateTimeDesc();

    List<DzEquipmentTimeAnalysis> getResetTimeIsNull(@Param("shardingParameter") String shardingParameter);

    BigDecimal getEquipmentSumRunTime(@Param("orderNo") String orderNo, @Param("deviceId") String deviceId, @Param("workData") LocalDate workData, @Param("startTime") LocalTime startTime);

}
