package com.dzics.data.pub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dao.TimeAnalysisCmd;
import com.dzics.data.pub.model.entity.DzDataCollection;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-29
 */
public interface DzDataCollectionService extends IService<DzDataCollection> {

    /**
     * 根据设备类型获取设备状态指令信号
     *
     * @param shardingParameter
     * @return
     */
    List<TimeAnalysisCmd> getDeviceTypeCmdSingal(String shardingParameter);

    TimeAnalysisCmd getDeviceId(String deviceId);


    @Cacheable(cacheNames = {"dataCollectionService.cacheDeviceId"}, key = "#deviceId", unless = "#result == null")
    DzDataCollection cacheDeviceId(String deviceId);

    @CachePut(cacheNames = {"dataCollectionService.cacheDeviceId"}, key = "#dzDataCollection.deviceId", unless = "#result == null")
    DzDataCollection updateDeviceId(DzDataCollection dzDataCollection);

    boolean instert(DzDataCollection dzDataCollection);

    /**
     * IOT数据表缓存设置状态数据
     *
     * @param deviceId 设备唯一属性
     * @return
     */
    @Cacheable(cacheNames = "cacheService.getIotTableDeviceState", key = "#deviceId")
    TimeAnalysisCmd getIotTableDeviceState(String deviceId);

    /**
     * @param dzDataCollection
     * @return
     */
    @CachePut(cacheNames = "cacheService.getIotTableDeviceState", key = "#dzDataCollection.deviceId")
    TimeAnalysisCmd updateIotTableDeviceState(TimeAnalysisCmd dzDataCollection);
}
