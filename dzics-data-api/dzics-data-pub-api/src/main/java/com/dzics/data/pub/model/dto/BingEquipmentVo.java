package com.dzics.data.pub.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BingEquipmentVo {
    @NotNull(message = "请选择设备")
    private String equipmentId;
    @NotNull(message = "请选择产线")
    private String lineId;
}
