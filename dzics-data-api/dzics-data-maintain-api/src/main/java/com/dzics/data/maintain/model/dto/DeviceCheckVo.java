package com.dzics.data.maintain.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeviceCheckVo {
    @ApiModelProperty("添加不填，修改必填")
    private String checkHistoryId;

    @ApiModelProperty("产线ID")
    private String lineId;
    @ApiModelProperty("设备id")
    private String deviceId;
    @ApiModelProperty("巡检类型")
    private String checkType;
    @ApiModelProperty("操作账号")
    private String username;
    @ApiModelProperty("巡检项数组")
    private List<DeviceCheckItemVo> historyItemList;

}
