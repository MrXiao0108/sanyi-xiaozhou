package com.dzics.data.kanban.changsha.changpaoguang.socket.subscribe;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.dto.GetToolInfoDataDo;
import com.dzics.data.common.base.dto.ToolDataDo;
import com.dzics.data.common.base.enums.DeviceSocketSendStatus;
import com.dzics.data.common.base.model.constant.SocketMessageType;
import com.dzics.data.common.util.SubscribeUtil;
import com.dzics.data.kanban.changsha.changpaoguang.model.proto.PaoGuangKanBanProtobuf;
import com.dzics.data.kanban.changsha.changpaoguang.socket.server.SocketIoHandler;
import com.dzics.data.pub.service.kanban.ToolCompService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Classname ToolCheckData
 * @Description 刀具
 * @Date 2022/3/17 18:08
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class ToolCheckDataSubScribe {
    private ToolCompService<List<GetToolInfoDataDo>> toolcompservice;
    private SocketIoHandler socketIoHandler;

    public ToolCheckDataSubScribe(ToolCompService<List<GetToolInfoDataDo>> toolcompservice, SocketIoHandler socketIoHandler) {
        this.toolcompservice = toolcompservice;
        this.socketIoHandler = socketIoHandler;
    }

    /**
     * 订阅刀具检测数据
     *
     * @param client
     * @param ackRequest
     * @param data
     */
    @OnEvent(value = SocketMessageType.TOOL_TEST_DATA)
    public void getToolTestData(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 刀具检测数据 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
//        刀具检测
            List<GetToolInfoDataDo> getToolInfoDataX = toolcompservice.getToolInfoData(data.getOrderNo(), data.getLineNo());
            String s = JSONObject.toJSONString(getToolInfoDataX.get(0));
            System.out.println("XXXXXXXXXXXX->" + s);
            if (CollectionUtils.isNotEmpty(getToolInfoDataX)) {
               PaoGuangKanBanProtobuf.InitToolData.Builder builder = PaoGuangKanBanProtobuf.InitToolData.newBuilder();
                for (GetToolInfoDataDo toolInfoDataX : getToolInfoDataX) {
                    PaoGuangKanBanProtobuf.ToolData.Builder builder1 = PaoGuangKanBanProtobuf.ToolData.newBuilder();
                    builder1.setOrderNo(toolInfoDataX.getOrderNo());
                    builder1.setLineNo(toolInfoDataX.getLineNo());
                    builder1.setEquipmentId(toolInfoDataX.getEquipmentId());
                    builder1.setEquipmentName(toolInfoDataX.getEquipmentName());
                    builder1.setEquipmentNo(toolInfoDataX.getEquipmentNo());
                    List<ToolDataDo> toolDataList = toolInfoDataX.getToolDataList();
                    for (ToolDataDo toolDataDo : toolDataList) {
                        PaoGuangKanBanProtobuf.ToolDataDo.Builder builder2 = PaoGuangKanBanProtobuf.ToolDataDo.newBuilder();
                        builder2.setToolNo(toolDataDo.getToolNo());
                        builder2.setToolLifeCounter(toolDataDo.getToolLifeCounter());
                        builder2.setToolLife(toolDataDo.getToolLife());
                        builder1.addToolDataList(builder2);
                    }
                    builder.addToolData(builder1);
                }
                builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_MACHINE_TOOL_INFORMATION.getInfo());
                byte[] bytes = builder.build().toByteArray();
                client.sendEvent(SocketMessageType.TOOL_TEST_DATA, bytes);
            }
            socketIoHandler.addEvent(client, SocketMessageType.TOOL_TEST_DATA + orderNo + lineNo, "刀具检测");
            long b = System.currentTimeMillis();
            log.info("订阅刀具检测数据,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }

    }

    /**
     * 取消订阅刀具检测数据
     *
     * @param client
     * @param ackRequest
     * @param data
     */
    @OnEvent(value = SocketMessageType.UN_TOOL_TEST_DATA)
    public void unGetToolTestData(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 订阅刀具检测 ] 数据:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
        socketIoHandler.unEvent(client, SocketMessageType.TOOL_TEST_DATA + orderNo + lineNo, "刀具检测");
    }

}
