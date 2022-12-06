package com.dzics.data.pub.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AddOrderVo {
    @ApiModelProperty("id(编辑必填)")
    public Long id;

    @ApiModelProperty("订单编号")
    @NotEmpty(message = "订单编号不能为空")
    public String orderNo;

    @ApiModelProperty("备注")
    public String remarks;
}
