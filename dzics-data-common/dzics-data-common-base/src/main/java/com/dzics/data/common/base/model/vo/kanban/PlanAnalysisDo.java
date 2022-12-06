package com.dzics.data.common.base.model.vo.kanban;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PlanAnalysisDo {

    @ApiModelProperty("完成率")
    private List<BigDecimal> percentageComplete;
    @ApiModelProperty("产出率")
    private List<BigDecimal> outputRate;
    @ApiModelProperty("合格率")
    private List<BigDecimal> passRate;
    @ApiModelProperty("日期")
    private List<String> dateList;
}
