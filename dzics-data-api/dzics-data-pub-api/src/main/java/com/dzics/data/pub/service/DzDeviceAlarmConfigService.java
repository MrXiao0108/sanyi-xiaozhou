package com.dzics.data.pub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.GetDeivceAlarmConfig;
import com.dzics.data.pub.model.entity.DzDeviceAlarmConfig;

import java.util.List;

/**
 * <p>
 * 设备告警配置 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-30
 */
public interface DzDeviceAlarmConfigService extends IService<DzDeviceAlarmConfig> {

    List<DzDeviceAlarmConfig> listCfg(String useOrgCode, String orderId, String lineId, String deivceId, Integer alarmGrade, String equipmentNo);

    Result<List<DzDeviceAlarmConfig>> getGiveAlarmConfig(GetDeivceAlarmConfig alarmConfig, String useOrgCode);

    Result delGiveAlarmConfig(String alarmConfigId, String sub);
}
