package com.dzics.data.pdm.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;

@Data
public class GetByEquipmentNoVo {

    @NotEmpty(message = "选择设备")
    @ApiModelProperty("设备id")
    private String id;

    @ApiModelProperty("起始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String startTime;
    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String endTime;

    @ApiModelProperty("参数不用传")
    private String orgCode;

    @ApiModelProperty("排序字段")
    private String field;
    @ApiModelProperty("ASC OR DESC OR 空字符串")
    private String type;

    @ApiModelProperty("订单号")
    private String orderNo;
}
