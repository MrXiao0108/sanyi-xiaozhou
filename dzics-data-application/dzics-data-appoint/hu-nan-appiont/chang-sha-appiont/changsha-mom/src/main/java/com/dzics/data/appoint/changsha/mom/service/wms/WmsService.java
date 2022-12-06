package com.dzics.data.appoint.changsha.mom.service.wms;


import com.dzics.data.appoint.changsha.mom.model.dto.wms.DzCallFrame;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.DzLocation;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.DzOrderCompleted;

/**
 * wms 接口
 *
 * @author ZhangChengJun
 * Date 2021/12/6.
 * @since
 */
public interface WmsService<T> {

    /**
     * 呼叫料框
     *
     * @param dzCallFrame         RFID信息 订单号 物料号
     * @return
     */
    T callFrame(DzCallFrame dzCallFrame);

    /**
     * 请求WMS 服务器
     * 机械手放货位置申请
     * @param dzLocation         RFID信息 订单号 下料点
     * @return
     */
    T rfidMaterialCodeStation(DzLocation dzLocation);


    /**
     * 订单完成信号
     *
     * @param orderNum 订单号
     * @return
     */
    T orderCompleted(DzOrderCompleted orderNum);

}
