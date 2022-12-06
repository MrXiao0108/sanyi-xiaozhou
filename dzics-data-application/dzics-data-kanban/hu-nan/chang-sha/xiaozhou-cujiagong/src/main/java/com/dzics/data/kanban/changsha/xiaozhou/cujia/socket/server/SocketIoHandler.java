package com.dzics.data.kanban.changsha.xiaozhou.cujia.socket.server;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.enums.DeviceSocketSendStatus;
import com.dzics.data.common.base.model.custom.JCEquimentBase;
import com.dzics.data.common.base.model.constant.SocketMessageType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.cache.SocketCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author ZhangChengJun
 * Date 2021/3/24.
 */
@Component
@Slf4j
public class SocketIoHandler {
    /**
     * 监听客户端连接
     *
     * @param client
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        log.info("....客户端IP:{} 连接sid:{}....", client.getRemoteAddress(),client.getSessionId());
    }

    /**
     * 监听客户端断开
     *
     * @param client
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        log.info("....客户端IP:{} 断开连接sid:{}....", client.getRemoteAddress(),client.getSessionId());
        UUID sessionId = client.getSessionId();
        String id = sessionId.toString();
//        根据sessid 获取连接的所有事件类型
        ConcurrentSkipListSet<String> skipListSet = SocketCache.connectTypeUUID.get(id);
        if (skipListSet != null && !skipListSet.isEmpty()) {
            for (String type : skipListSet) {
                ConcurrentSkipListSet<UUID> concurrentSkipListSet =  SocketCache.connectType.get(type);
                if (concurrentSkipListSet == null || concurrentSkipListSet.isEmpty()) {
                    continue;
                }
                concurrentSkipListSet.remove(sessionId);
                SocketCache.connectType.put(type, concurrentSkipListSet);
            }
        }
        SocketCache.connectTypeUUID.remove(id);
    }

    public ConcurrentSkipListSet<UUID> getConnectType(String eventType) {
        ConcurrentSkipListSet<UUID> concurrentSkipListSet =  SocketCache.connectType.get(eventType);
        return concurrentSkipListSet;
    }

    /**
     * 订阅事件
     *
     * @param client
     * @param hostManagerNumberState
     * @param subscriptionType
     */
    public void addEvent(SocketIOClient client, String hostManagerNumberState, String subscriptionType) {
        UUID sessionId = client.getSessionId();
        String id = sessionId.toString();
//        ------------事件->连接存储   处理 发送事件 对应 的客户端------------------------
        ConcurrentSkipListSet<UUID> concurrentSkipListSet =  SocketCache.connectType.get(hostManagerNumberState);
        if (concurrentSkipListSet == null || concurrentSkipListSet.isEmpty()) {
            concurrentSkipListSet = new ConcurrentSkipListSet();
        }
        concurrentSkipListSet.add(sessionId);
        SocketCache.connectType.put(hostManagerNumberState, concurrentSkipListSet);
//      ---------------- 事件对应的客户端连接更新完必-------------------------------------------
//      ---------------------------------------------------------------------------------------
//      ---------------- 连接对应的时间 存储更新开始 ------------------------------------------
        ConcurrentSkipListSet uidTypeList =  SocketCache.connectTypeUUID.get(id);
        if (uidTypeList == null || uidTypeList.isEmpty()) {
            uidTypeList = new ConcurrentSkipListSet();
        }
        uidTypeList.add(hostManagerNumberState);
        SocketCache.connectTypeUUID.put(sessionId.toString(), uidTypeList);
        log.info("IP:{},完成订阅:[ {} ]事件成功：addEventType: {}", client.getRemoteAddress(), subscriptionType, hostManagerNumberState);
//         ---------------- 连接对应的时间 存储更新结束 ------------------------------------------
    }

    /**
     * 取消订阅事件
     *
     * @param client
     * @param hostManagerNumberState
     */
    public void unEvent(SocketIOClient client, String hostManagerNumberState, String subscriptionType) {
        UUID sessionId = client.getSessionId();
        String sid = sessionId.toString();
//        根据sessid 获取连接的事件
//        ---------------删除处理客户端id 存储的事件 开始 -----------
        ConcurrentSkipListSet uidTypeList =  SocketCache.connectTypeUUID.get(sid);
        if (uidTypeList != null && !uidTypeList.isEmpty()) {
            uidTypeList.remove(hostManagerNumberState);
            SocketCache.connectTypeUUID.put(sid, uidTypeList);
        }
//        ---------------删除处理客户端id 存储的事件 结束 -------------

//        --------------- 删除事件对应的客户端 id 开始-----------------
        ConcurrentSkipListSet<UUID> concurrentSkipListSet =  SocketCache.connectType.get(hostManagerNumberState);
        if (concurrentSkipListSet == null || concurrentSkipListSet.isEmpty()) {
            return;
        }
        concurrentSkipListSet.remove(sessionId);
        SocketCache.connectType.put(hostManagerNumberState, concurrentSkipListSet);
        log.info("IP：{}，取消: [ {} ] 事件成功：addEventType:{}", client.getRemoteAddress(), subscriptionType, hostManagerNumberState);
    }



    /**
     * 版本刷新订阅
     *
     * @param client
     * @param ackRequest
     */
    @OnEvent(value = SocketMessageType.GET_VERSION_PUSH_REFRESH)
    public void getVersionPushRefresh(SocketIOClient client, AckRequest ackRequest) {
        log.info("IP:{},开始订阅 [ 版本刷新 ]", client.getRemoteAddress());
        JCEquimentBase jcEquimentBase = new JCEquimentBase();
        jcEquimentBase.setData("OK");
        jcEquimentBase.setType(DeviceSocketSendStatus.REFRESH.getInfo());
        client.sendEvent(SocketMessageType.GET_VERSION_PUSH_REFRESH, Result.ok(jcEquimentBase));
        addEvent(client, SocketMessageType.GET_VERSION_PUSH_REFRESH, "版本刷新");
    }

    /**
     * 版本刷新订阅 取消
     *
     * @param client
     * @param ackRequest
     * @param data
     */
    @OnEvent(value = SocketMessageType.UN_GET_VERSION_PUSH_REFRESH)
    public void unGetVersionPushRefresh(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 订阅版本刷新 ]:->data:{}", client.getRemoteAddress());
        unEvent(client, SocketMessageType.GET_VERSION_PUSH_REFRESH, "版本刷新");
    }


}
