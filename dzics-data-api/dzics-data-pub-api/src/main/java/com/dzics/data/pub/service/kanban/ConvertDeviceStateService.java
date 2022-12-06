package com.dzics.data.pub.service.kanban;

/**
 * @Classname ConvertDeviceStateService
 * @Description 转换翻译设备状态
 * @Date 2022/3/17 10:39
 * @Created by NeverEnd
 */
public interface ConvertDeviceStateService<T, M> {

    /**
     * 转化设备状态
     *
     * @param m 设备状态指令信息
     * @return
     */
    T toMachiningMessageStatus(M m);


}
