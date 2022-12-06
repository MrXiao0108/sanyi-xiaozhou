package com.dzics.data.common.base.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ToolDataDo {
    @ApiModelProperty("刀具号")
    private String toolNo;
    @ApiModelProperty("已用")
    private Integer toolLifeCounter;
    @ApiModelProperty("刀具总寿命")
    private Integer toolLife;
}
