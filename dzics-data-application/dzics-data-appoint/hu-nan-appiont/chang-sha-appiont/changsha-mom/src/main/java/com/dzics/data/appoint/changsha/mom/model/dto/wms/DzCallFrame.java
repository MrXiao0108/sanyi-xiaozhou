package com.dzics.data.appoint.changsha.mom.model.dto.wms;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ZhangChengJun
 * Date 2021/12/6.
 * @since
 */
@Data
public class DzCallFrame {


    @ApiModelProperty("订单号")
    private String orderNum;

    @ApiModelProperty("物料号")
    @NotNull(message = "物料号必填")
    private String materialCode;


}
