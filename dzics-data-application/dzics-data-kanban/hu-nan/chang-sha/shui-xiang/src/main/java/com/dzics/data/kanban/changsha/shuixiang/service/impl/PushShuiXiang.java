package com.dzics.data.kanban.changsha.shuixiang.service.impl;

import com.dzics.data.common.base.enums.DeviceSocketSendStatus;
import com.dzics.data.common.base.model.constant.SocketMessageType;
import com.dzics.data.kanban.changsha.shuixiang.model.proto.ShuiXiangKanBanProtobuf;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.vo.DeviceLogsMsg;
import com.dzics.data.pub.service.Impl.SocketPushServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;

/**
 * @Classname PushShuiXiang
 * @Description 描述
 * @Date 2022/3/22 8:51
 * @Created by NeverEnd
 */
@Slf4j
@Service
public class PushShuiXiang extends SocketPushServiceImpl {

    @Value("${cache.base.name}")
    private String cacheBaseName;
    @Override
    public synchronized void sendStateEquiment(DzEquipment dzEquipment) {
        boolean b = frequencyLimitation(cacheBaseName + ":PushShuiXiang:" + dzEquipment.getId(), 3);
        if (b){
            String eventKey = getEvent(SocketMessageType.DEVICE_STATUS, dzEquipment.getOrderNo(), dzEquipment.getLineNo());
            boolean isSendEvend = getIsSendEvend(eventKey);
            if (isSendEvend) {
                String currentLocation = dzEquipment.getCurrentLocation();
                ShuiXiangKanBanProtobuf.MachiningMessageStatus.Builder status = ShuiXiangKanBanProtobuf.MachiningMessageStatus.newBuilder();
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
                status.setAlarmStatus(dzEquipment.getAlarmStatus());
                ShuiXiangKanBanProtobuf.InitDeviceState.Builder builder = ShuiXiangKanBanProtobuf.InitDeviceState.newBuilder();
                builder.addStatus(status);
                builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_STATE.getInfo());
                byte[] bytes = builder.build().toByteArray();
                socketServerTemplate.sendMessage(SocketMessageType.DEVICE_STATUS, eventKey, bytes);
            }
        }


    }


    @Override
    public void sendReatimLogs(DeviceLogsMsg b) {
        String eventKey = getEvent(SocketMessageType.DEVICE_LOG, b.getOrderCode(), b.getLineNo());
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = dateFormat.format(b.getTimestampTime());
            ShuiXiangKanBanProtobuf.InitRealTimeLogRes.Builder builder = ShuiXiangKanBanProtobuf.InitRealTimeLogRes.newBuilder();
            ShuiXiangKanBanProtobuf.RealTimeLogRes.Builder builder1 = ShuiXiangKanBanProtobuf.RealTimeLogRes.newBuilder();
            builder1.setClientId(b.getClientId());
            builder1.setMessage(b.getMessage());
            builder1.setRealTime(format);
            builder.addData(builder1);
            if (b.getMessageType() != null && b.getMessageType().intValue() == 1) {
                builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_REAL_TIME_LOG.getInfo());
                byte[] bytes = builder.build().toByteArray();
                socketServerTemplate.sendMessage(SocketMessageType.DEVICE_LOG, eventKey, bytes);
            } else {
                builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_ALARM_LOG.getInfo());
                byte[] bytes = builder.build().toByteArray();
                socketServerTemplate.sendMessage(SocketMessageType.DEVICE_LOG_WARN, eventKey, bytes);
            }

        }
    }

}
