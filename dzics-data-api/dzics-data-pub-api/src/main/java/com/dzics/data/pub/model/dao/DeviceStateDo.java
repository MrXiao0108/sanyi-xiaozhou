package com.dzics.data.pub.model.dao;

import lombok.Data;

@Data
public class DeviceStateDo {
    private String equipmentId;
    private String equipmentNo;
    private Integer deType;
    private String equipmentName;

    /**
     * 机器人关节坐标
     * */
    private String A501;
    /**
     * 机器人绝对坐标
     */
    private String A502;
    /**
     * 机床绝对坐标 绝对坐标
     */
    private String B501;
    /**
     * 机床 连接状态
     */
    private String B561;
    /**
     * 机器人 连接状态
     */
    private String A561;
    /**
     * 机床 操作模式
     */
    private String B565;

    /**
     * 机床 主轴负载
     * */
    private String B801;
    /**
     * 机器人 操作模式
     */
    private String A562;

    /**
     * 待机状态
     */
    private String A567;
    /**
     * 机床运行状态
     */
    private String B562;
    /**
     * 机器人运行状态
     */
    private String A563;

    /**
     * 急停状态
     */
    private String B568;

    /**
     * 机床告警状态
     */
    private String B569;
    /**
     * 机器人告警状态
     */
    private String A566;

    /**
     * 速度倍率
     */
    private String A521;
    /**
     * 加工节拍
     */
    private String A802;

    /**
     * 主轴转速
     */
    private String B551;
    /**
     * 进给速度
     */
    private String B541;
    /**
     * 连接状态
     */
    private String H561;
    /**
     * 运行状态
     */
    private String H562;
    /**
     * 工作状态
     */
    private String H563;
    /**
     * 告警状态
     */
    private String H565;
    /**
     * 自动/手动模式选择
     */
    private String H566;
    /**
     * 移动速度
     */
    private String H706;
    /**
     * 工件转速
     */
    private String H707;
    /**
     * 冷却液温度 ℃
     */
    private String H801;
    /**
     * 冷却液压力 MPa
     */
    private String H804;
    /**
     * 冷却液流量 L/s
     */
    private String H805;
    /**
     * 连接状态
     */
    private String K561;
    /**
     * 运行状态
     */
    private String K562;
    /**
     * 工作状态
     */
    private String K563;
    /**
     * 急停状态
     */
    private String K564;
    /**
     * 告警状态
     */
    private String K565;
    /**
     * 自动/手动模式选择
     */
    private String K566;
    /**
     * 压头上下位置
     */
    private String K803;
    /**
     * 压头左右位置
     */
    private String K804;

    /**
     * 冷却液流量 L/s（多）
     */
    private String H808;

    private Long downSum;
}
