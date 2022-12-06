package com.dzics.data.common.base.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetToolInfoDataDo {
    private String orderNo = "";

    private String lineNo = "";
    @ApiModelProperty("设备id")
    private String equipmentId= "";
    @ApiModelProperty("设备名称")
    private String equipmentName= "";
    @ApiModelProperty("设备序号")
    private String equipmentNo= "";
    @ApiModelProperty("刀具信息")
    List<ToolDataDo> toolDataList;
}
