package com.dzics.data.acquisition.service;

import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.pub.model.entity.DzEquipment;

public interface AccDeviceHandleState {
    /**
     *处理状态数据 只做推送的数据处理，不进行库的插入更新操作
     * @param rabbitmqMessage
     * @return
     */
    DzEquipment analysisNumStatePush(RabbitmqMessage rabbitmqMessage);
}
