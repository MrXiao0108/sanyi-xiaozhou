package com.dzics.data.appoint.changsha.mom.hardware.service;

/**
 * @author: van
 * @since: 2022-07-12
 */
public interface HardwareService {

    /**
     * 处理UDP消息
     *
     * @param msg: 消息内容
     * @author van
     * @date 2022/7/12
     */
    void udpHandle(String msg);

    /**
     * 回传设备控制指令状态处理
     *
     * @param split: 反馈报文数组
     * @author van
     * @date 2022/7/13
     */
    void udpCmdControlFeedbackHandle(String[] split);
}
