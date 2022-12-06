package com.dzics.data.pub.service;

/**
 * 刀具使用次数发送接口 定义
 */
public interface SocketPushToolService {
    boolean sendToolDetection(String msg);
}
