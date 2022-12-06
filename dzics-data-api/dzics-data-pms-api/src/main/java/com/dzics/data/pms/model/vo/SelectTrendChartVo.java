package com.dzics.data.pms.model.vo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SelectTrendChartVo {

    @ApiModelProperty(value = "产品id(序号)", required = true)
    @NotBlank(message = "请选择产品")
    private String productNo;
    @ApiModelProperty(value = "关联产品检测配置表 id", required = true)
    @NotNull(message = "请选择检测项")
    private String detectionId;

    @ApiModelProperty(value = "搜索起始时间")
    private String startTime;

    @ApiModelProperty(value = "搜索结束时间")
    private String endTime;
    /**
     * 检测项
     */
    private String detectionName;

    @JsonIgnore
    private String orderNo;
    @JsonIgnore
    private String lineNo;

}
