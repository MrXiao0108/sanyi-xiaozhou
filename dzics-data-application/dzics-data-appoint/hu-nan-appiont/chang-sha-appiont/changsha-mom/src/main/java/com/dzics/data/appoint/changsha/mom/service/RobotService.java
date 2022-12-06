package com.dzics.data.appoint.changsha.mom.service;

/**
 * @author: van
 * @since: 2022-07-01
 */

public interface RobotService {

    /**
     * 向机器人发送命令
     *
     * @param proTaskOrderId: MOM订单主键
     * @param typeContro:     DzUdpType
     * @author van
     * @date 2022/7/1
     */
    void sendControCmdRob(String proTaskOrderId, String typeContro);
}
