package com.dzics.data.kanban.changsha.changpaoguang.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 致煌设备状态信息
 *
 * @author ZhangChengJun
 * Date 2021/3/11.
 * @since
 */
@Data
public class MachiningMessageStatus implements Serializable {
    /**
     * 设备ID
     */
    @ApiModelProperty("设备ID")
    private String equimentId;
    @ApiModelProperty("设备序号")
    private String equipmentNo;
    @ApiModelProperty("设备类型")
    private Integer equipmentType;
    @ApiModelProperty("设备名称")
    private String equipmentName;
    private String x;
    private String y;
    private String z;

    @ApiModelProperty("连接状态")
    private String connectState = "";
    @ApiModelProperty("操作模式")
    private String operatorMode = "";
    @ApiModelProperty("运行状态")
    private String runStatus = "";

    @ApiModelProperty("速度倍率")
    private String speedRatio = "";
    @ApiModelProperty("加工节拍")
    private String machiningTime = "";

    @ApiModelProperty("告警状态")
    private String alarmStatus = "";

}
