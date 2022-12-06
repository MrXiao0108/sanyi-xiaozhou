package com.dzics.data.kanban.changsha.xiaozhou.cujia.socket.subscribe;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.enums.DeviceSocketSendStatus;
import com.dzics.data.common.base.model.constant.SocketMessageType;
import com.dzics.data.common.util.SubscribeUtil;
import com.dzics.data.kanban.changsha.xiaozhou.cujia.model.dto.MachiningMessageStatus;
import com.dzics.data.kanban.changsha.xiaozhou.cujia.model.proto.XiaoZhouCuJiaKanBanProtobuf;
import com.dzics.data.kanban.changsha.xiaozhou.cujia.socket.server.SocketIoHandler;
import com.dzics.data.pub.service.DeviceStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Classname DeviceStatusSubscribe
 * @Description 描述
 * @Date 2022/3/17 17:21
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class DeviceStatusSubscribe {
    private SocketIoHandler socketIoHandler;
    private DeviceStateService<List<MachiningMessageStatus>> stateService;

    public DeviceStatusSubscribe(SocketIoHandler socketIoHandler, DeviceStateService<List<MachiningMessageStatus>> stateService) {
        this.socketIoHandler = socketIoHandler;
        this.stateService = stateService;
    }

    /**
     * 设备状态订阅
     *
     * @param client
     * @param ackRequest
     * @param data
     */
    @OnEvent(value = SocketMessageType.DEVICE_STATUS)
    public void getDeviceStatus(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP：{}，开始订阅 [ 设备状态 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
//        设备状态
            List<MachiningMessageStatus> ok = stateService.getDeivceState(orderNo, lineNo);
            if (CollectionUtils.isNotEmpty(ok)) {
                XiaoZhouCuJiaKanBanProtobuf.InitDeviceState.Builder builder = XiaoZhouCuJiaKanBanProtobuf.InitDeviceState.newBuilder();
                for (MachiningMessageStatus status : ok) {
                    XiaoZhouCuJiaKanBanProtobuf.MachiningMessageStatus.Builder builder1 = XiaoZhouCuJiaKanBanProtobuf.MachiningMessageStatus.newBuilder();
                    builder1.setEquimentId(status.getEquimentId());
                    builder1.setEquipmentNo(status.getEquipmentNo());
                    builder1.setEquipmentType(status.getEquipmentType());
                    if(3!=status.getEquipmentType()){
                        builder1.setX(status.getX());
                        builder1.setY(status.getY());
                        builder1.setZ(status.getZ());
                    }else{
                        builder1.setJ1(status.getJ1());
                        builder1.setJ2(status.getJ2());
                        builder1.setJ3(status.getJ3());
                        builder1.setJ4(status.getJ4());
                        builder1.setJ5(status.getJ5());
                        builder1.setJ6(status.getJ6());
                    }
                    builder1.setFeedSpeed(status.getFeedSpeed());
                    builder1.setConnectState(status.getConnectState());
                    builder1.setOperatorMode(status.getOperatorMode());
                    builder1.setRunStatus(status.getRunStatus());
                    builder1.setSpeedRatio(status.getSpeedRatio());
                    builder1.setMachiningTime(status.getMachiningTime());
                    builder1.setAlarmStatus(status.getAlarmStatus());
                    builder1.setSpeedOfMainShaft(status.getSpeedOfMainShaft());
                    if(!StringUtils.isEmpty(status.getSpindleLoad())){
                        builder1.setSpindleLoad(status.getSpindleLoad());
                    }
                    builder.addStatus(builder1);
                }
                builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_DEVICE.getInfo());
                byte[] bytes = builder.build().toByteArray();
                client.sendEvent(SocketMessageType.DEVICE_STATUS, bytes);
            }
            socketIoHandler.addEvent(client, SocketMessageType.DEVICE_STATUS + orderNo + lineNo, "设备状态");
            long b = System.currentTimeMillis();
            log.info("设备状态订阅,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), data);
        }
    }

    /**
     * 取消设备状态订阅
     *
     * @param client
     * @param ackRequest
     * @param data
     */
    @OnEvent(value = SocketMessageType.UN_DEVICE_STATUS)
    public void unGetDeviceStatus(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 设备状态 ] 订阅:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
        socketIoHandler.unEvent(client, SocketMessageType.DEVICE_STATUS + orderNo + lineNo, "设备状态");
    }
}
