package com.dzics.data.kanban.changsha.xiaozhou.cujia.socket.subscribe;

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
import com.dzics.data.kanban.changsha.xiaozhou.cujia.model.proto.XiaoZhouCuJiaKanBanProtobuf;
import com.dzics.data.kanban.changsha.xiaozhou.cujia.socket.server.SocketIoHandler;
import com.dzics.data.pub.service.DeviceLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Classname DeviceLogsSubscribe
 * @Description 描述
 * @Date 2022/3/17 17:23
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class DeviceLogsSubscribe {

    private DeviceLogService logService;
    private SocketIoHandler socketIoHandler;

    public DeviceLogsSubscribe(DeviceLogService logService, SocketIoHandler socketIoHandler) {
        this.logService = logService;
        this.socketIoHandler = socketIoHandler;
    }

    /**
     * 订阅设备实时日志
     *
     * @param client
     * @param ackRequest
     * @param data
     */
    @OnEvent(value = SocketMessageType.DEVICE_LOG)
    public void getDeviceLogs(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 设备实时日志 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            //      日志数据
            List<ReatimLogRes> reatimLogRes = logService.getReatimLogRes(orderNo, lineNo, null);
            if (CollectionUtils.isNotEmpty(reatimLogRes)) {
                XiaoZhouCuJiaKanBanProtobuf.InitRealTimeLogRes.Builder builder = XiaoZhouCuJiaKanBanProtobuf.InitRealTimeLogRes.newBuilder();
                for (ReatimLogRes reatimLogRe : reatimLogRes) {
                    XiaoZhouCuJiaKanBanProtobuf.RealTimeLogRes.Builder builder1 = XiaoZhouCuJiaKanBanProtobuf.RealTimeLogRes.newBuilder();
                    builder1.setClientId(reatimLogRe.getClientId());
                    builder1.setMessage(reatimLogRe.getMessage());
                    builder1.setRealTime(reatimLogRe.getRealTime());
                    builder.addData(builder1);
                }
                builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_REAL_TIME_LOG.getInfo());
                byte[] bytes = builder.build().toByteArray();
                client.sendEvent(SocketMessageType.DEVICE_LOG, bytes);
            }
            socketIoHandler.addEvent(client, SocketMessageType.DEVICE_LOG + orderNo + lineNo, "设备实时日志");
            long b = System.currentTimeMillis();
            log.info("订阅设备实时日志,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }
    }

    /**
     * 取消订阅设备实时日志
     *
     * @param client
     * @param ackRequest
     * @param data
     */
    @OnEvent(value = SocketMessageType.UN_DEVICE_LOG)
    public void unGetDeviceLogs(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 订阅设备实时 ] 日志:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
        socketIoHandler.unEvent(client, SocketMessageType.DEVICE_LOG + orderNo + lineNo, "设备实时日志");
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
                XiaoZhouCuJiaKanBanProtobuf.InitRealTimeLogRes.Builder builder = XiaoZhouCuJiaKanBanProtobuf.InitRealTimeLogRes.newBuilder();
                for (ReatimLogRes reatimLogRe : reatimLogRes) {
                    XiaoZhouCuJiaKanBanProtobuf.RealTimeLogRes.Builder builder1 = XiaoZhouCuJiaKanBanProtobuf.RealTimeLogRes.newBuilder();
                    builder1.setClientId(reatimLogRe.getClientId());
                    //数据分割后的集合（单独一条完整数据）
                    reatimLogRe.setMessage(reatimLogRe.getMessage().replace("[", ""));
                    reatimLogRe.setMessage(reatimLogRe.getMessage().replace("]", ""));
                    reatimLogRe.setMessage(reatimLogRe.getMessage().trim());
                    String[] details = reatimLogRe.getMessage().split(",");
                    //报警代码
                    String AlarmNo = details[0];
                    //报警类型
                    String AlarmType = details[1];
                    //轴编号
                    String AxisNo = details[2];
                    //报警内容长度
                    String MsgLength = details[3];
                    //报警时间
                    String Datetime = details[4];
                    //报警内容
                    String AlarmMsg = details[5];
                    builder1.setMessage("报警代码:"+AlarmNo+",报警内容:"+AlarmMsg+",报警时间:"+Datetime);
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
