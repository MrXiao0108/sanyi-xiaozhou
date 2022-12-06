package com.dzics.data.pub.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 公共订单
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Data
public class SelOrders {
    @ApiModelProperty("订单Id")
    private String orderId;
}
