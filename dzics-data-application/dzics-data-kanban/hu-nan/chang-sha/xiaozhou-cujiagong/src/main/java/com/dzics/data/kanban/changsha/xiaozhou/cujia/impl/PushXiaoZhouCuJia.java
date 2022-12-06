package com.dzics.data.kanban.changsha.xiaozhou.cujia.impl;

import com.dzics.data.common.base.enums.DeviceSocketSendStatus;
import com.dzics.data.common.base.model.constant.SocketMessageType;
import com.dzics.data.kanban.changsha.xiaozhou.cujia.model.proto.XiaoZhouCuJiaKanBanProtobuf;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.vo.DeviceLogsMsg;
import com.dzics.data.pub.service.Impl.SocketPushServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;

/**
 * @Classname PushAnren
 * @Description 描述
 * @Date 2022/3/13 16:29
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class PushXiaoZhouCuJia extends SocketPushServiceImpl {
    @Value("${cache.base.name}")
    private String cacheBaseName;
    @Override
    public void sendReatimLogs(DeviceLogsMsg b) {
        String eventKey = getEvent(SocketMessageType.DEVICE_LOG_WARN, b.getOrderCode(), b.getLineNo());
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = dateFormat.format(b.getTimestampTime());
            XiaoZhouCuJiaKanBanProtobuf.InitRealTimeLogRes.Builder builder = XiaoZhouCuJiaKanBanProtobuf.InitRealTimeLogRes.newBuilder();
            XiaoZhouCuJiaKanBanProtobuf.RealTimeLogRes.Builder builder1 = XiaoZhouCuJiaKanBanProtobuf.RealTimeLogRes.newBuilder();
            builder1.setClientId(b.getClientId());
            builder1.setMessage(b.getMessage());
            builder1.setRealTime(format);
            builder.addData(builder1);
            if (b.getMessageType() != null && b.getMessageType().intValue() == 1) {
                builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_REAL_TIME_LOG.getInfo());
            } else {
                builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_ALARM_LOG.getInfo());
            }
            byte[] bytes = builder.build().toByteArray();
            socketServerTemplate.sendMessage(SocketMessageType.DEVICE_LOG_WARN, eventKey, bytes);
        }
    }

    @Override
    public synchronized void sendStateEquiment(DzEquipment dzEquipment) {
        boolean b = frequencyLimitation(cacheBaseName + ":PushZhanHui:" + dzEquipment.getId(), 3);
        if (b){
            String eventKey = getEvent(SocketMessageType.DEVICE_STATUS, dzEquipment.getOrderNo(), dzEquipment.getLineNo());
            boolean isSendEvend = getIsSendEvend(eventKey);
            if (isSendEvend) {
                String currentLocation = dzEquipment.getCurrentLocation();
                XiaoZhouCuJiaKanBanProtobuf.MachiningMessageStatus.Builder status = XiaoZhouCuJiaKanBanProtobuf.MachiningMessageStatus.newBuilder();
                if (!StringUtils.isEmpty(currentLocation)) {
                    String[] split = currentLocation.split(",");
                    if (split.length >= 3) {
                        String x = split[0];
                        if (!"0.000".equals(x)) {
                            status.setX(x);
                        }
                        String y = split[1];
                        if (!"0.000".equals(y)) {
                            status.setY(y);
                        }
                        String z = split[2];
                        if (!"0.000".equals(z)) {
                            status.setZ(z);
                        }
                    }
                }
                String deviceNum = dzEquipment.getEquipmentNo();
                Integer deviceType = dzEquipment.getEquipmentType();
                status.setEquimentId(dzEquipment.getId());
                status.setEquipmentNo(deviceNum);
                status.setEquipmentType(deviceType);
                status.setOperatorMode(dzEquipment.getOperatorMode());
                status.setConnectState(dzEquipment.getConnectState());
                status.setRunStatus(dzEquipment.getRunStatus());
                status.setSpeedRatio(dzEquipment.getSpeedRatio());
                status.setMachiningTime(dzEquipment.getMachiningTime());
                status.setSpeedOfMainShaft(dzEquipment.getSpeedOfMainShaft());
                status.setSpeedRatio(dzEquipment.getSpeedRatio());
                status.setFeedSpeed(dzEquipment.getFeedSpeed());
                status.setSpindleLoad(dzEquipment.getSpindleLoad());
                status.setAlarmStatus(dzEquipment.getAlarmStatus());
                XiaoZhouCuJiaKanBanProtobuf.InitDeviceState.Builder builder = XiaoZhouCuJiaKanBanProtobuf.InitDeviceState.newBuilder();
                builder.addStatus(status);
                builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_STATE.getInfo());
                byte[] bytes = builder.build().toByteArray();
                socketServerTemplate.sendMessage(SocketMessageType.DEVICE_STATUS, eventKey, bytes);
            }
        }
    }


}
