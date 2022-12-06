package com.dzics.data.pub.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ZhangChengJun
 * Date 2021/5/19.
 * @since
 */
@Data
public class OrderIdLineId implements Serializable {
    /**
     * 产线Id
     */
    private String lineId;

    /**
     * 订单Id
     */
    private String orderId;

    /**
     * 产线序号
     */
    private String lienNo;
    /**
     * 订单序号
     */
    private String orderNo;
}
