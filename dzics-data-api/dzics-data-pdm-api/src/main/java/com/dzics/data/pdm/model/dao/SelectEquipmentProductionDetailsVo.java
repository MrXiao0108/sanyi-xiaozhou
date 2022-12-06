package com.dzics.data.pdm.model.dao;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;

@Data
public class SelectEquipmentProductionDetailsVo {
    @ApiModelProperty("设备id")
    @NotNull(message = "选择设备")
   private String equimentId;
    @ApiModelProperty("日期")
    @NotNull(message = "日期不能为空")
   @DateTimeFormat(pattern = "yyyy-MM-dd")
   private String workDate;
}
