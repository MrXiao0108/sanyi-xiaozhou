package com.dzics.data.appoint.changsha.mom.model.dto.mom;

import lombok.Data;

/**
 * 空料框移动
 */
@Data
public class MOMAGVHandshakeDto {

    /**
     * 系统编码
     */
    private String SysCode;

    /*
     * 工厂编号
     */
    private String Facility;

    /*
     * 产线
     */
    private String ProductionLine;

    /**
     * 工作中心
     */
    private String WorkCenter;

    /**
     * 0：AGV已到等待点请求进入
     * 1：配送已完成
     */
    private String WaitType;

    /**
     * 2：允许进入
     * 3：配送完成状态已收到
     */
    private String Result;

    /**
     * 料框类型
     */
    private String palletType;

    /**
     * 料框编码
     */
    private String palletNo;

    /**
     * 起点编码
     */
    private String sourceNo;

    /**
     * 终点编码
     */
    private String destNo;

    /**
     * 物料组编码
     */
    private String materialGroup;

    /*
     * 生产订单号
     */
    private String order_id;

    /*
     * AGV反馈到达时间
     */
    private String arriveTime;

    /**
     * 预留参数1
     */
    private String paramRsrv1;

    /**
     * 预留参数2
     */
    private String paramRsrv2;
}
