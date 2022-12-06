package com.dzics.data.pdm.db.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class SelectProductionDetailsVo {
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("搜索起始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    @ApiModelProperty("搜索结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    @ApiModelProperty("查询不填")
    private String tableKey;
    @ApiModelProperty("产线id")
    private String lineId;

    @ApiModelProperty("排序字段")
    private String field;
    @ApiModelProperty("ASC OR DESC OR 空字符串")
    private String type;

    private String eqid;
}
