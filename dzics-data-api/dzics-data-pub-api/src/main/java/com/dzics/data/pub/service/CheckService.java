package com.dzics.data.pub.service;

import java.util.Map;

/**
 * @Classname CheckService
 * @Description 检测记录接口
 * @Date 2022/3/15 14:39
 * @Created by NeverEnd
 */
public interface CheckService {
    /**
     * 发送给前端
     * @param dzWorkpieceData 检测设备数据
     * @return
     */
    boolean sendWorkpieceData(Map dzWorkpieceData);
}
