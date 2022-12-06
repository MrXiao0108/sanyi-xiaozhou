package com.dzics.data.acquisition.service;

import com.dzics.data.common.base.model.dto.RabbitmqMessage;

/**
 * @Classname ProductPositionService
 * @Description 描述
 * @Date 2022/8/24 14:18
 * @Created by NeverEnd
 */
public interface ProductPositionService {

    /**
     * 处理报工
     *
     * @param arr 数组
     * @return true: 处理成功
     */
    boolean productPositionForRobot(String[] arr);

    /**
     * 处理报工（临时码）
     *
     * @param arr 数组
     * @return true: 处理成功
     */
    boolean baoGongTempCode(String[] arr);

    /**
     * 临时码数据处理
     *
     * @param message 实体
     */
    void baoGongTempCodeWrapper(RabbitmqMessage message);
}
