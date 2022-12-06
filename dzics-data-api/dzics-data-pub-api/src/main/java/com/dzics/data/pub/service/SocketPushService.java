package com.dzics.data.pub.service;

import com.dzics.data.common.base.model.dto.SocketDowmSum;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.vo.DeviceLogsMsg;

public interface SocketPushService {

    /** 默认返回true 不同看板重写此方法
     * @param key      校验key
     * @param time 时间秒为单位
     * @return 返回 true 限制解除 false 限制存在 不进行数据发送更新
     */
    default boolean frequencyLimitation(String key,  int time) {
        return true;
    }

    void sendDownDaySum(SocketDowmSum dowmSum);

    /**
     * 发送设备状态
     *
     * @param dzEquipment
     */
    void sendStateEquiment(DzEquipment dzEquipment);

    /**
     * 发送告警日志
     * @Prame logsMsg
     * @return void
     * */
    void sendReatimLogs(DeviceLogsMsg logsMsg);

    void dzRefresh(String msg);
}
