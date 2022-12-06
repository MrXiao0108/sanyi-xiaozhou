package com.dzics.data.common.base.model.dao;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QualifiedAndOutputDo {

    @ApiModelProperty("毛坯数量")
    private Long roughNum;
    @ApiModelProperty("当前产量")
    private Long nowNum;
    @ApiModelProperty("合格数量")
    private Long qualifiedNum;

    @ApiModelProperty("产出率")
    private BigDecimal output;
    @ApiModelProperty("合格率")
    private BigDecimal qualified;
}
