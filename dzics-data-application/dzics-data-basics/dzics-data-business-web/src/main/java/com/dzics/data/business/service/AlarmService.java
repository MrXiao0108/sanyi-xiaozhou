package com.dzics.data.business.service;

import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.AddDeviceAlarmConfig;

public interface AlarmService {
    Result addGiveAlarmConfig(AddDeviceAlarmConfig alarmConfig, String sub);

    Result putGiveAlarmConfig(AddDeviceAlarmConfig alarmConfig, String sub);
}
