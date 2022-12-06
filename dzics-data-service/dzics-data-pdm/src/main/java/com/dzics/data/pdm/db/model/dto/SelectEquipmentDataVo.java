package com.dzics.data.pdm.db.model.dto;


import com.dzics.data.common.base.model.page.PageLimit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SelectEquipmentDataVo extends PageLimit {
    @ApiModelProperty("设备类型(不用管这个字段)")
    private Integer equipmentType;
    @ApiModelProperty("产线id")
    private String lineId;
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ApiModelProperty("机器人编码")
    private String equipmentCode;
    @ApiModelProperty("班次名称")
    private String workName;
    @ApiModelProperty(value = "班次开始时间",dataType = "java.lang.String")
    private String startTime;
    @ApiModelProperty(value = "班次结束时间",dataType = "java.lang.String")
    private String endTime;

    @JsonIgnore
    private String orgCode;
}
