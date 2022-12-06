package com.dzics.data.maintain.model.dto;

import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class GetDeviceCheckVo extends PageLimitBase {
    @ApiModelProperty("产线id")
    private Long lineId;
    @ApiModelProperty("设备编号")
    private String equipmentNo;
    @ApiModelProperty("巡检单号")
    private Long checkNumber;
    @ApiModelProperty("巡检类型")
    private String checkType;

    @ApiModelProperty(value = "起始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    @JsonIgnore
    private String useOrgCode;

}
