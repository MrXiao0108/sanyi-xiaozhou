package com.dzics.data.pdm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.pdm.model.entity.DzEquipmentTimeAnalysis;
import com.dzics.data.pdm.model.vo.DeviceStateDetails;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-10-11
 */
public interface DzEquipmentTimeAnalysisService extends IService<DzEquipmentTimeAnalysis> {

    /**
     * 根据设备id 查询设备今日作业时间 单位毫秒
     *
     * @param id
     * @param orderNo
     * @return
     */
    Long getEquipmentAvailable(String id, String orderNo);

    DzEquipmentTimeAnalysis getResetTimeIsNull(String deviceId);


    List<DeviceStateDetails> getDeviceStateDetailsStopTime(Date date, String deviceId, String orderNo);

    Date getUpdateTimeDesc();

    List<DzEquipmentTimeAnalysis> getRestTimeIsNull(String shardingParameter);

    /**
     * 设备用时
     *
     * @param stopTime stopTime
     * @param deviceId deviceId
     * @param orderNo  orderNo
     * @return
     */
    List<DeviceStateDetails> getDeviceAnalysisTime(Date stopTime, String deviceId, String orderNo);

    void saveTimeAnlysis(DzEquipmentTimeAnalysis analysis);

    void updateByIdTimeAnalysis(DzEquipmentTimeAnalysis timeAnalysis);


    List<DzEquipmentTimeAnalysis> getRestTimeIsNullDeviceId(String deviceId);

    @Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW)
    void updateByIdList(List<DzEquipmentTimeAnalysis> analysisList);

    @Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW)
    void insTimeAnalysis(List<DzEquipmentTimeAnalysis> inst);

    /**
     * 设备用时分析上次执行时间
     *
     * @return
     */
    @Cacheable(cacheNames = "cacheService.robTimeAnalysis", key = "#type")
    Date robTimeAnalysis(String type);

    @CachePut(cacheNames = "cacheService.robTimeAnalysis", key = "#type")
    Date updateRobTimeAnalysis(String type, Date nowDate);

    BigDecimal getEquipmentSumRunTime(String orderNo, String deviceId, LocalDate workData, LocalTime startTime);
}
