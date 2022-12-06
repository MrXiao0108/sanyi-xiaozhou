package com.dzics.data.common.base.model.vo.kanban;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class PlanAnalysisDoSingle implements Serializable {
//    类版本号
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("完成率")
    private BigDecimal percentageComplete;

    @ApiModelProperty("生产节拍")
    private BigDecimal taktTime;

    @ApiModelProperty("合格率")
    private BigDecimal passRate;

    @ApiModelProperty("计划生产数量")
    private BigDecimal plannedQuantity;

    @ApiModelProperty("已完成数量")
    private BigDecimal completedQuantity;

    @ApiModelProperty("产品名称")
    private String productName;



}
