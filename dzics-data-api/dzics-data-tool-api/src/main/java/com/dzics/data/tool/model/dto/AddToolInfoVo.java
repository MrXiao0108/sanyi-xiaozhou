package com.dzics.data.tool.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddToolInfoVo {
    @ApiModelProperty("刀具组id")
    @NotNull(message = "刀具组id不能为空")
    private String toolGroupId;
    @ApiModelProperty("新增刀具编号")
    @NotNull(message = "新增刀具编号不能为空")
    private Integer toolNo;
}
