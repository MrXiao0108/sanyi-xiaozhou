package com.dzics.data.kanban.changsha.shuixiang.socket.subscribe;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.dto.log.ReatimLogRes;
import com.dzics.data.common.base.enums.DeviceSocketSendStatus;
import com.dzics.data.common.base.model.constant.LogType;
import com.dzics.data.common.base.model.constant.SocketMessageType;
import com.dzics.data.common.util.SubscribeUtil;
import com.dzics.data.kanban.changsha.shuixiang.model.proto.ShuiXiangKanBanProtobuf;
import com.dzics.data.kanban.changsha.shuixiang.socket.server.SocketIoHandler;
import com.dzics.data.pub.service.DeviceLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Classname DeviceLogSubscribe
 * @Description 日志订阅
 * @Date 2022/3/18 10:01
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class DeviceLogSubscribe {
    private SocketIoHandler socketIoHandler;
    private DeviceLogService logService;

    public DeviceLogSubscribe(SocketIoHandler socketIoHandler, DeviceLogService logService) {
        this.socketIoHandler = socketIoHandler;
        this.logService = logService;
    }

    /**
     * 订阅设备日志
     * @param client
     * @param ackRequest
     * @param data
     */
    @OnEvent(value = SocketMessageType.DEVICE_LOG)
    public void getDeviceLogs(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 设备日志 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            String deviceType = data.getDeviceType();
            //      日志数据
            List<ReatimLogRes> reatimLogRes = logService.getReatimLogRes(orderNo, lineNo, null);
            if (CollectionUtils.isNotEmpty(reatimLogRes)) {
                ShuiXiangKanBanProtobuf.InitRealTimeLogRes.Builder builder = ShuiXiangKanBanProtobuf.InitRealTimeLogRes.newBuilder();
                for (ReatimLogRes reatimLogRe : reatimLogRes) {
                    ShuiXiangKanBanProtobuf.RealTimeLogRes.Builder builder1 = ShuiXiangKanBanProtobuf.RealTimeLogRes.newBuilder();
                    builder1.setClientId(reatimLogRe.getClientId());
                    builder1.setMessage(reatimLogRe.getMessage());
                    builder1.setRealTime(reatimLogRe.getRealTime());
                    builder.addData(builder1);
                }
                builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_REAL_TIME_LOG.getInfo());
                byte[] bytes = builder.build().toByteArray();
                client.sendEvent(SocketMessageType.DEVICE_LOG, bytes);
            }
            socketIoHandler.addEvent(client, SocketMessageType.DEVICE_LOG + orderNo + lineNo + deviceType, "设备日志");
            long b = System.currentTimeMillis();
            log.info("订阅设备日志,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }

    }

    /**
     * 取消订阅设备日志
     *
     * @param client
     * @param ackRequest
     * @param data
     */
    @OnEvent(value = SocketMessageType.UN_DEVICE_LOG)
    public void unGetDeviceLogs(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 订阅设备 ] 日志:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
        String deviceType = data.getDeviceType();
        socketIoHandler.unEvent(client, SocketMessageType.DEVICE_LOG + orderNo + lineNo + deviceType, "设备日志");
    }






    /**
     * 订阅设备告警日志
     *
     * @param client
     * @param ackRequest
     * @param data
     */
    @OnEvent(value = SocketMessageType.DEVICE_LOG_WARN)
    public void getDeviceLogWarns(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 设备告警日志 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            //      日志数据
            List<ReatimLogRes> reatimLogRes = logService.getReatimLogRes(orderNo, lineNo, LogType.warnLog);
            if (CollectionUtils.isNotEmpty(reatimLogRes)) {
                ShuiXiangKanBanProtobuf.InitRealTimeLogRes.Builder builder = ShuiXiangKanBanProtobuf.InitRealTimeLogRes.newBuilder();
                for (ReatimLogRes reatimLogRe : reatimLogRes) {
                    ShuiXiangKanBanProtobuf.RealTimeLogRes.Builder builder1 = ShuiXiangKanBanProtobuf.RealTimeLogRes.newBuilder();
                    builder1.setClientId(reatimLogRe.getClientId());
                    builder1.setMessage(reatimLogRe.getMessage());
                    builder1.setRealTime(reatimLogRe.getRealTime());
                    builder.addData(builder1);
                }
                builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_ALARM_LOG.getInfo());
                byte[] bytes = builder.build().toByteArray();
                client.sendEvent(SocketMessageType.DEVICE_LOG_WARN, bytes);
            }
            socketIoHandler.addEvent(client, SocketMessageType.DEVICE_LOG_WARN + orderNo + lineNo, "设备告警日志");
            long b = System.currentTimeMillis();
            log.info("订阅设备告警日志,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }
    }

    /**
     * 取消订阅设备告警日志
     *
     * @param client
     * @param ackRequest
     * @param data
     */
    @OnEvent(value = SocketMessageType.UN_DEVICE_LOG_WARN)
    public void unGetDeviceLogsWarns(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 订阅设备告警 ] 日志:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
        socketIoHandler.unEvent(client, SocketMessageType.DEVICE_LOG_WARN + orderNo + lineNo, "设备告警日志");
    }
}
