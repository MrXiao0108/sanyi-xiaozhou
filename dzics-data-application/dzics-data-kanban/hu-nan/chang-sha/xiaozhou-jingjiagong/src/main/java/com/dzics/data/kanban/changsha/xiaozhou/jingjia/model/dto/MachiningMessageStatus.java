package com.dzics.data.kanban.changsha.xiaozhou.jingjia.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备状态默认看板信息
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
    private String x;
    private String y;
    private String z;

    private String j1;
    private String j2;
    private String j3;
    private String j4;
    private String j5;
    private String j6;

    @ApiModelProperty("连接状态")
    private String connectState;
    @ApiModelProperty("操作模式")
    private String operatorMode;
    @ApiModelProperty("运行状态")
    private String runStatus;

    @ApiModelProperty("速度倍率")
    private String speedRatio = "";
    @ApiModelProperty("加工节拍")
    private String machiningTime = "";

    @ApiModelProperty("告警状态")
    private String alarmStatus;
    /**
     * 进给速度
     */
    @ApiModelProperty("进给速度")
    private String feedSpeed;
    /**
     * 主轴转速
     */
    @ApiModelProperty("主轴转速")
    private String speedOfMainShaft;

    @ApiModelProperty("主轴负载")
    private String spindleLoad;
}
