package com.dzics.data.acquisition.service;

import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.pdm.model.entity.DzEquipmentProNum;

public interface AccProNumService {
    /**
     * 处理生产数据
     *
     * @param msg
     */
    DzEquipmentProNum analysisNum(RabbitmqMessage msg);
}
