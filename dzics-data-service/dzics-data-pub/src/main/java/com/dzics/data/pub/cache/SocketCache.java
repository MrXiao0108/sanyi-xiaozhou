package com.dzics.data.pub.cache;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class SocketCache {
    /**
     * 存储客户端连接后触发的事件 map
     */
    public static ConcurrentHashMap<String, ConcurrentSkipListSet<UUID>> connectType = new ConcurrentHashMap<>();

    /**
     * 存储客户端 触发事件时的 连接UUID 和 时间类型
     */
    public static ConcurrentHashMap<String, ConcurrentSkipListSet<String>> connectTypeUUID = new ConcurrentHashMap<>();

    public static ConcurrentSkipListSet<UUID> getConnectType(String eventType) {
        return SocketCache.connectType.get(eventType);
    }
}
