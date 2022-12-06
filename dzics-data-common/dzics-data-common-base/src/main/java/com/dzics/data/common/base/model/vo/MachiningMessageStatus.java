package com.dzics.data.common.base.model.vo;

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

    @ApiModelProperty("连接状态")
    private String connectState;
    @ApiModelProperty("操作模式")
    private String operatorMode;
    @ApiModelProperty("运行状态")
    private String runStatus;

    @ApiModelProperty("速度倍率")
    private String speedRatio;
    @ApiModelProperty("加工节拍")
    private String machiningTime;
    /**
     * 淬火机 移动速度  mm/s
     */
    private String movementSpeed;
    /**
     * 工件转速 Rad/min
     */
    private String workpieceSpeed;

    /**
     * 冷却液温度 ℃
     */
    private String coolantTemperature;
    /**
     * 冷却液压力 MPa
     */
    private String coolantPressure;
    /**
     * 冷却液流量 L/s
     */
    private String coolantFlow;


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

    /**
     * 平均节拍
     */
    private String averageBeat;

    /**
     * 产品编号
     */
    private String proNum;
}
