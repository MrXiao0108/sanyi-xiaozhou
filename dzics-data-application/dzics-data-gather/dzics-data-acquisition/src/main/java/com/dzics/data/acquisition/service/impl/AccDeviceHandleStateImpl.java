package com.dzics.data.acquisition.service.impl;

import com.alibaba.fastjson.JSON;
import com.dzics.data.common.base.model.custom.EqMentStatus;
import com.dzics.data.common.base.model.dto.CmdTcp;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.acquisition.service.AccDeviceHandleState;
import com.dzics.data.common.util.NumberUtils;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.service.DzEquipmentService;
import com.dzics.data.pub.util.TcpStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AccDeviceHandleStateImpl implements AccDeviceHandleState {
    @Autowired
    private TcpStringUtil tcpStringUtil;
    @Autowired
    private DzEquipmentService dzEquipmentService;

    @Override
    public DzEquipment analysisNumStatePush(RabbitmqMessage cmd) {
        List<CmdTcp> cmdTcps = tcpStringUtil.getCmdTcp(cmd.getMessage());
        String orderNumber = cmd.getOrderCode();
        String lineNum = cmd.getLineNo();
        String deviceTypeStr = cmd.getDeviceType();
        Integer deviceType = Integer.valueOf(deviceTypeStr);
        String deviceNum = cmd.getDeviceCode();
        DzEquipment upDzDqState = dzEquipmentService.getTypeLingEqNoPush(deviceNum, lineNum, deviceTypeStr, orderNumber);
        if (upDzDqState == null) {
            log.error("AccDeviceHandleStateImpl [analysisNumStatePush] 设备不存在 结束执行:" +
                    "lineNum:{},deviceNum:{},deviceType:{},orderNumber:{}", lineNum, deviceNum, deviceType, orderNumber);
            return null;
        }
        boolean falg = false;
        boolean falgLocation = false;
        List<String> logs = new ArrayList<>();
        List<String> logsWar = new ArrayList<>();
        String deviceName = upDzDqState.getEquipmentName();
        for (CmdTcp commData : cmdTcps) {
            // 指令
            String tcpValue = commData.getTcpValue();
            // 指令值
            String deviceItemValue = commData.getDeviceItemValue();
            // 解释翻译后内容 例如 1 设备正常正常 [31.454,45.464] 位置信息
            String tcpDescription = commData.getTcpDescription();
            switch (tcpValue) {
                case EqMentStatus.CMD_CNC_CUTTING_TIME:
                    if (upDzDqState.getB526() == null || !tcpDescription.equals(upDzDqState.getB526())) {
                        falg = true;
                        upDzDqState.setB526(tcpDescription);
                    }
                    continue;
                case EqMentStatus.TCP_ROB_WORK_PIECE:
                    if (upDzDqState.getA812() == null || !tcpDescription.equals(upDzDqState.getA812())) {
                        falg = true;
                        upDzDqState.setA812(tcpDescription);
                    }
                    continue;
                case EqMentStatus.CMD_CNC_RUN_TIME:
                    if (upDzDqState.getA541() == null || !tcpDescription.equals(upDzDqState.getA541())) {
                        falg = true;
                        upDzDqState.setA541(tcpDescription);
                    }
                    continue;
                case EqMentStatus.CMD_CNC_TOOL_NO:
                    if (upDzDqState.getB809() == null || !tcpDescription.equals(upDzDqState.getB809())) {
                        falg = true;
                        upDzDqState.setB809(tcpDescription);
                    }
                    continue;
                    // 清零状态
                case EqMentStatus.TCP_CL_CO_ST:
                    boolean numeric = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric) {
                        Integer cleaState = Integer.valueOf(deviceItemValue);
                        if (upDzDqState.getClearCountStatusValue() == null || upDzDqState.getClearCountStatusValue().intValue() != cleaState.intValue()) {
                            falg = true;
                            upDzDqState.setClearCountStatus(tcpDescription);
                            upDzDqState.setClearCountStatusValue(cleaState);
                        }
                    }
                    continue;
                    //连接状态，如联机、脱机、虚拟机
                case EqMentStatus.TCP_CL_ST_CNC:
                case EqMentStatus.TCP_CL_ST_ROB:
                case EqMentStatus.TCP_CL_ST_CHJ:
                case EqMentStatus.TCP_CL_ST_JZJ:
                    boolean numeric1 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                   /* log.error(" TCP_CL_ST_JZJ ---> tcpValue: {}, deviceItemValue: {}", tcpValue, deviceItemValue);
                    log.error(" TCP_CL_ST_JZJ ---> numeric1: {}", numeric1);*/
                    if (numeric1) {
                        Integer connectState = Integer.valueOf(deviceItemValue);
                        if (upDzDqState.getConnectStateValue() == null || upDzDqState.getConnectStateValue().intValue() != connectState.intValue()) {
                            falg = true;
                            logs.add(deviceName + deviceNum + ":" + tcpDescription);
                            upDzDqState.setConnectState(tcpDescription);
                            upDzDqState.setConnectStateValue(connectState);
                        }
                    }
                    continue;
                    //操作模式，自动/手动
                case EqMentStatus.TCP_OPE_MODE_CNC:
                case EqMentStatus.TCP_OPE_MODE_ROB:
                case EqMentStatus.TCP_OPE_MODE_CHJ:
                case EqMentStatus.TCP_OPE_MODE_JZJ:
                    boolean numeric2 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric2) {
                        Integer operatorMode = Integer.valueOf(deviceItemValue);
                        if (upDzDqState.getOperatorModeValue() == null || upDzDqState.getOperatorModeValue().intValue() != operatorMode.intValue()) {
                            falg = true;
                            logs.add(deviceName + deviceNum + ":操作模式切换为" + tcpDescription);
                            upDzDqState.setOperatorMode(tcpDescription);
                            upDzDqState.setOperatorModeValue(operatorMode);
                        }
                    }
                    continue;
                    //绝对坐标
                case EqMentStatus.TCP_ABS_POS_ROB:
                case EqMentStatus.TCP_ABS_POS_CNC:
                    if (upDzDqState.getCurrentLocation() == null || !tcpDescription.equals(upDzDqState.getCurrentLocation())) {
                        falgLocation = true;
                        upDzDqState.setCurrentLocation(tcpDescription);
                    }
                    continue;
                    // 机器人运行状态
                case EqMentStatus.TCP_RUN_STATE_CNS:
                case EqMentStatus.TCP_RUN_STATE_ROB:
                case EqMentStatus.TCP_RUN_STATE_CHJ:
                case EqMentStatus.TCP_RUN_STATE_JZJ:
                    boolean numeric3 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric3) {
                        Integer runStatus = Integer.valueOf(deviceItemValue);
                        if (upDzDqState.getRunStatusValue() == null || upDzDqState.getRunStatusValue().intValue() != runStatus.intValue()) {
                            falg = true;
                            logs.add(deviceName + deviceNum + ":" + tcpDescription);
                            upDzDqState.setRunStatus(tcpDescription);
                            upDzDqState.setRunStatusValue(runStatus);
                        }
                    }
                    continue;
                    //待机状态
                case EqMentStatus.CMD_ROB_WAIT_STATUS:
                    upDzDqState.setA567(tcpDescription);
                    continue;
                    // 工作状态
                case EqMentStatus.TCP_WORK_STATE_CNS:
                case EqMentStatus.TCP_WORK_STATE_CHJ:
                case EqMentStatus.TCP_WORK_STATE_JZJ:
                    boolean numericGz = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numericGz) {
                        Integer status = Integer.valueOf(deviceItemValue);
                        if (upDzDqState.getWorkStatusValue() == null || upDzDqState.getWorkStatusValue().intValue() != status.intValue()) {
                            falg = true;
                            logs.add(deviceName + deviceNum + ":" + tcpDescription);
                            upDzDqState.setWorkStatus(tcpDescription);
                            upDzDqState.setWorkStatusValue(status);
                        }
                    }
                    continue;
                    //急停状态
                case EqMentStatus.TCP_EMERGENCY_STATUS_CNS:
                case EqMentStatus.TCP_EMERGENCY_STATUS_ROB:
                case EqMentStatus.TCP_EMERGENCY_STATUS_CHJ:
                case EqMentStatus.TCP_EMERGENCY_STATUS_JZJ:
                    boolean numeric8 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric8) {
                        Integer status = Integer.valueOf(deviceItemValue);
                        if (upDzDqState.getEquipmentStatusValue() == null || upDzDqState.getEquipmentStatusValue().intValue() != status.intValue()) {
                            falg = true;
                            logs.add(deviceName + deviceNum + ":" + tcpDescription);
                            upDzDqState.setEquipmentStatus(tcpDescription);
                            upDzDqState.setEquipmentStatusValue(status);
                            upDzDqState.setEmergencyStatus(tcpDescription);
                            upDzDqState.setEmergencyStatusValue(status);
                        }

                    }
                    continue;
                    //告警状态
                case EqMentStatus.TCP_ALARM_STATUS_CNC:
                case EqMentStatus.TCP_ALARM_STATUS_ROB:
                case EqMentStatus.TCP_ALARM_STATUS_CHJ:
                case EqMentStatus.TCP_ALARM_STATUS_JZJ:
                    boolean numeric4 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric4) {
                        Integer alarmStatus = Integer.valueOf(deviceItemValue);
                        if (upDzDqState.getAlarmStatusValue() == null || upDzDqState.getAlarmStatusValue().intValue() != alarmStatus.intValue()) {
                            falg = true;
                            logsWar.add(deviceName + deviceNum + ":" + tcpDescription);
                            upDzDqState.setAlarmStatus(tcpDescription);
                            upDzDqState.setAlarmStatusValue(alarmStatus);
                        }
                    }
                    continue;
                case EqMentStatus.CMD_ROB_PROCESS_TIME:
                    if (upDzDqState.getMachiningTime() == null || !upDzDqState.getMachiningTime().equals(deviceItemValue)) {
                        falg = true;
                        upDzDqState.setMachiningTime(deviceItemValue);
                    }
                    continue;
                case EqMentStatus.CMD_ROB_SPEED_RATIO:
                    if (upDzDqState.getSpeedRatio() == null || !upDzDqState.getSpeedRatio().equals(deviceItemValue)) {
                        falg = true;
                        upDzDqState.setSpeedRatio(deviceItemValue);
                    }
                    continue;
                case EqMentStatus.TCP_HEAD_POSITION_UD_JZJ:
                    if (upDzDqState.getHeadPositionUd() == null || !upDzDqState.getHeadPositionUd().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setHeadPositionUd(tcpDescription);
                    }
                    continue;
                case EqMentStatus.TCP_HEAD_POSITION_LR_JZJ:
                    if (upDzDqState.getHeadPostionLr() == null || !upDzDqState.getHeadPostionLr().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setHeadPostionLr(tcpDescription);
                    }
                    continue;
                case EqMentStatus.TCP_CHJ_SPEED:
                    if (upDzDqState.getMovementSpeed() == null || !upDzDqState.getMovementSpeed().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setMovementSpeed(tcpDescription);
                    }
                    continue;
                case EqMentStatus.TCP_CHJ_WORKPIECE_SPEED:
                    if (upDzDqState.getWorkpieceSpeed() == null || !upDzDqState.getWorkpieceSpeed().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setWorkpieceSpeed(tcpDescription);
                    }
                    continue;
                case EqMentStatus.TCP_CHJ_COOL_TEMP:
                    if (upDzDqState.getCoolantTemperature() == null || !upDzDqState.getCoolantTemperature().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setCoolantTemperature(tcpDescription);
                    }
                    continue;
                case EqMentStatus.TCP_CHJ_COOL_PRESS:
                    if (upDzDqState.getCoolantPressure() == null || !upDzDqState.getCoolantPressure().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setCoolantPressure(tcpDescription);
                    }
                    continue;
                case EqMentStatus.TCP_CHJ_COOL_FLOW:
                    if (upDzDqState.getCoolantFlow() == null || !upDzDqState.getCoolantFlow().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setCoolantFlow(tcpDescription);
                    }
                    continue;
                case EqMentStatus.TCO_CNC_JIE_PAI:
                    if (upDzDqState.getCleanTime() == null || !upDzDqState.getCleanTime().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setB527(tcpDescription);
                        upDzDqState.setCleanTime(tcpDescription);
                    }
                    continue;
                case EqMentStatus.CMD_CNC_SPINDLE_SPEED:
                    if (upDzDqState.getSpeedOfMainShaft() == null || !upDzDqState.getSpeedOfMainShaft().equals(tcpDescription)) {
                        upDzDqState.setSpeedOfMainShaft(tcpDescription);
                        falg = true;
                    }
                    continue;
                case EqMentStatus.CMD_CNC_GAS_FLOW:
                    if (upDzDqState.getGasFlow() == null || !upDzDqState.getGasFlow().equals(tcpDescription)) {
                        upDzDqState.setGasFlow(tcpDescription);
                        falg = true;
                    }
                case EqMentStatus.CMD_CNC_FEED_SPEED:
                    if (upDzDqState.getFeedSpeed() == null || !upDzDqState.getFeedSpeed().equals(tcpDescription)) {
                        upDzDqState.setFeedSpeed(tcpDescription);
                        falg = true;
                    }
                    continue;
                case EqMentStatus.CMD_CHJ_POWER:
                    upDzDqState.setH592(tcpDescription);
                    falg = true;
                    continue;
                case EqMentStatus.CMD_CHJ_SETPOWER:
                    upDzDqState.setH593(tcpDescription);
                    falg = true;
                    continue;
                case EqMentStatus.CMD_CHJ_COOL_FLOWS:
                    if (upDzDqState.getCoolantFlows() == null || !upDzDqState.getCoolantFlows().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setCoolantFlows(tcpDescription);
                    }
                    continue;
                case EqMentStatus.CMD_CNC_SPINDLE_LOAD:
                    if (upDzDqState.getSpindleLoad() == null || !upDzDqState.getSpindleLoad().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setSpindleLoad(tcpDescription);
                    }
                    continue;
                default:
                    log.debug("未识别运行状态类型跳过:lineNum:{},deviceNum:{},deviceType:{},tcpValue:{}", lineNum, deviceNum, deviceType, tcpValue);
            }
        }
        if (falg) {
            DzEquipment dzEquipment = dzEquipmentService.updateByLineNoAndEqNoPush(upDzDqState);
            log.debug("AccDeviceHandleStateImpl [AccDeviceHandleStateImpl] 更新设备状态 upDzDqState:{}", upDzDqState);
        } else {
            log.debug("设备状态未变化:lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
        }
        if ("待机".equals(upDzDqState.getA567())) {
            upDzDqState.setRunStatus(upDzDqState.getA567());
        }
        if (falg || falgLocation) {
            upDzDqState.setLogs(logs);
            upDzDqState.setLogsWar(logsWar);
            return upDzDqState;
        } else {
            log.debug("AccDeviceHandleStateImpl [analysisNumStatePush] (falg || falgLocation) is false cmd{}", JSON.toJSONString(cmd));
        }
        return null;
    }
}
