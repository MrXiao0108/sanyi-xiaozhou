package com.dzics.data.acquisition.service;

import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.pdm.model.entity.DzWorkpieceData;

public interface AccWorkpieceDataService {
    DzWorkpieceData handleCheckout(RabbitmqMessage rabbitmqMessage);
}
