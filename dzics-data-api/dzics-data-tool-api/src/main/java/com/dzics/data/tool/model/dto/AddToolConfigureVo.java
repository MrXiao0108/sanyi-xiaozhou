package com.dzics.data.tool.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddToolConfigureVo {
    @ApiModelProperty("修改必填，添加不填")
    private Long id;
    @ApiModelProperty("机床id")
    @NotNull(message = "机床id不能为空")
    private String equipmentId;

    @ApiModelProperty("组编号")
    @NotNull(message = "组编号不能为空")
    private Integer groupNo;

    @ApiModelProperty("刀具编号")
    @NotNull(message = "刀具编号不能为空")
    private Integer toolNo;

    @ApiModelProperty(value = "订单id")
    @NotNull(message = "订单id不能为空")
    private String orderId;

    @ApiModelProperty(value = "产线id")
    @NotNull(message = "产线id不能为空")
    private String lineId;
}
