package com.dzics.data.pdm.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetDayAndMonthDataDo {

    @ApiModelProperty("日产")
    private DayDataDo dayDataDo;
    @ApiModelProperty("月产")
    private DayDataDo monthDataDo;
}
