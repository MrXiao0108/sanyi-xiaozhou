package com.dzics.data.acquisition.service;

import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.pdm.model.entity.DzEquipmentProNumSignal;

public interface AccNumSignalService {
    /**
     * 检验数据重复
     * @param rabbitmqMessage
     * @return
     */
    boolean queuePylseSignalCheck(RabbitmqMessage rabbitmqMessage);

    /**
     * 解析脉冲数据
     *
     * @param rabbitmqMessage
     */
    DzEquipmentProNumSignal queuePylseSignal(RabbitmqMessage rabbitmqMessage);

    /**
     * 根据设备ID 设置缓存频率
     * @param equimentId 设备ID
     * @param sendSignalTime 触发事件
     */
    void setRedisSignalValue(String equimentId, Long sendSignalTime);

    /**
     * 处理补偿数据
     * @param dzEquipmentProNumSignal
     */
    Long compensate(DzEquipmentProNumSignal dzEquipmentProNumSignal);
}
