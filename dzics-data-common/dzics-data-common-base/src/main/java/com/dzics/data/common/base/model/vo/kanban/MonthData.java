package com.dzics.data.common.base.model.vo.kanban;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MonthData {
    @ApiModelProperty("月份")
    private String month;
    @ApiModelProperty("合格")
    private Long qualified;
    @ApiModelProperty("不合格")
    private Long rejects;
    @ApiModelProperty("生产数量")
    private Long nowNum;
}
