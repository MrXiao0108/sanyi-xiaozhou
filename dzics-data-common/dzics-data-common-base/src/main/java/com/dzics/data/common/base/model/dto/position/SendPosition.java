package com.dzics.data.common.base.model.dto.position;

import lombok.Data;

import java.util.Date;

/**
 * @Classname SendPosition
 * @Description 报工数据发送到看板
 * @Date 2022/3/16 12:36
 * @Created by NeverEnd
 */
@Data
public class SendPosition {
    /**
     * 订单id
     */
    private String orderId;
    /**
     * 产线id
     */
    private String lineId;

    private String orderNo;
    private String lineNo;
    /**
     * 工件二维码
     */
    private String qrCode;

    /**
     *  2=去工位取料  出
     *  1=去工位放料 进
     */
    private String outInputType;

    /**
     * 进出时间 生产时间
     */
    private Date productionTime;

    /**
     * 工位详情json 数据 1
     */
    private String stationJsonA;
    /**
     * 工位详情json 数据 2
     */
    private String stationJsonB;

}
