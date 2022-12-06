package com.dzics.data.appoint.changsha.mom.model.dto.wms;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/12/6.
 * @since
 */
@Data
public class DzOrderCompleted {
    @ApiModelProperty("订单号")
    private String orderNum;
}
