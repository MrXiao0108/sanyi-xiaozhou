package com.dzics.data.pub.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class DeviceLogsMsg {
    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    @ApiModelProperty(value = "消息发送时间")
    private Date timestampTime;

    @ApiModelProperty(value = "消息内容")
    private String message;

    @ApiModelProperty(value = "客户端id")
    private String clientId;

    @ApiModelProperty(value = "1正常日志2告警日志")
    private Integer messageType;

    @ApiModelProperty(value = "产线序号")
    private String lineNo;

    @ApiModelProperty(value = "订单编码")
    private String orderCode;
}
