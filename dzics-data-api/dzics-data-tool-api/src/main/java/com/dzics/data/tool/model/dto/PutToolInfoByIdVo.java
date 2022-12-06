package com.dzics.data.tool.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PutToolInfoByIdVo {

   @ApiModelProperty("刀具id")
   @NotNull(message = "刀具id不能为空")
   private Long id;
    @ApiModelProperty("更改后刀具编号")
    @NotNull(message = "更改后刀具编号不能为空")
    private Integer toolNo;
}
