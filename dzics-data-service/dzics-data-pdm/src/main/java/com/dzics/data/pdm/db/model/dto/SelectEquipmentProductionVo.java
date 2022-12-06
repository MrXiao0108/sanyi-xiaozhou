package com.dzics.data.pdm.db.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class SelectEquipmentProductionVo {
    @ApiModelProperty("站点名称")
    private String departName;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("产线id")
    @NotNull(message = "请选择产线")
    private String lineId;

    @ApiModelProperty("搜索起始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    @ApiModelProperty("搜索结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    @ApiModelProperty("查询不填")
    @JsonIgnore
    private String orgCode;

    @ApiModelProperty("查询不填")
    @JsonIgnore
    private String tableKey;

    @ApiModelProperty("排序字段")
    private String field;

    @ApiModelProperty("ASC OR DESC OR 空字符串")
    private String type;
}
