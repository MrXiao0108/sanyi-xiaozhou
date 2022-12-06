package com.dzics.data.business.service.impl;

import com.dzics.data.business.service.AlarmService;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.AddDeviceAlarmConfig;
import com.dzics.data.pub.model.entity.DzDeviceAlarmConfig;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.service.DzDeviceAlarmConfigService;
import com.dzics.data.pub.service.DzEquipmentService;
import com.dzics.data.pub.service.DzProductionLineService;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AlarmServiceImpl implements AlarmService {
    @Autowired
    private DzEquipmentService equipmentService;
    @Autowired
    private DzProductionLineService lineService;
    @Autowired
    private DzicsUserService sysUserServiceDao;
    @Autowired
    private DzDeviceAlarmConfigService alarmConfigService;

    @Override
    public Result addGiveAlarmConfig(AddDeviceAlarmConfig alarmConfig, String sub) {
        DzEquipment byId = equipmentService.getById(alarmConfig.getDeviceId());
        DzProductionLine lineId = lineService.getLineId(alarmConfig.getLineId());
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzDeviceAlarmConfig deviceAlarmConfig = new DzDeviceAlarmConfig();
        deviceAlarmConfig.setEquipmentNo(byId.getEquipmentNo());
        deviceAlarmConfig.setEquipmentType(byId.getEquipmentType());
        deviceAlarmConfig.setOrderId(alarmConfig.getOrderId());
        deviceAlarmConfig.setLineId(alarmConfig.getLineId());
        deviceAlarmConfig.setOrderNo(lineId.getOrderNo());
        deviceAlarmConfig.setLineNo(lineId.getLineNo());
        deviceAlarmConfig.setDeviceId(alarmConfig.getDeviceId());
        deviceAlarmConfig.setLocationData(alarmConfig.getLocationData());
        deviceAlarmConfig.setAlarmName(alarmConfig.getAlarmName());
        deviceAlarmConfig.setAlarmGrade(alarmConfig.getAlarmGrade());
        deviceAlarmConfig.setOrgCode(byUserName.getUseOrgCode());
        deviceAlarmConfig.setDelFlag(false);
        deviceAlarmConfig.setCreateBy(byUserName.getUsername());
        alarmConfigService.save(deviceAlarmConfig);
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result putGiveAlarmConfig(AddDeviceAlarmConfig alarmConfig, String sub) {
        DzProductionLine lineId = lineService.getLineId(alarmConfig.getLineId());
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzDeviceAlarmConfig deviceAlarmConfig = new DzDeviceAlarmConfig();
        deviceAlarmConfig.setAlarmConfigId(alarmConfig.getAlarmConfigId());
        deviceAlarmConfig.setOrderId(alarmConfig.getOrderId());
        deviceAlarmConfig.setLineId(alarmConfig.getLineId());
        deviceAlarmConfig.setOrderNo(lineId.getOrderNo());
        deviceAlarmConfig.setLineNo(lineId.getLineNo());
        deviceAlarmConfig.setDeviceId(alarmConfig.getDeviceId());
        deviceAlarmConfig.setLocationData(alarmConfig.getLocationData());
        deviceAlarmConfig.setAlarmName(alarmConfig.getAlarmName());
        deviceAlarmConfig.setAlarmGrade(alarmConfig.getAlarmGrade());
        deviceAlarmConfig.setOrgCode(byUserName.getUseOrgCode());
        deviceAlarmConfig.setUpdateBy(byUserName.getUsername());
        alarmConfigService.updateById(deviceAlarmConfig);
        return new Result(CustomExceptionType.OK);
    }
}
