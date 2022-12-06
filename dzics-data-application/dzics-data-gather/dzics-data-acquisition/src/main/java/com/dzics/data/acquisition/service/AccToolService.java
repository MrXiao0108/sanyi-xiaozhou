package com.dzics.data.acquisition.service;

import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.common.base.dto.GetToolInfoDataDo;

public interface AccToolService {
    /**
     * 刀具信息更新
     * @param rabbitmqMessage
     * @return
     */
    GetToolInfoDataDo getEqToolInfoList(RabbitmqMessage rabbitmqMessage);
}
