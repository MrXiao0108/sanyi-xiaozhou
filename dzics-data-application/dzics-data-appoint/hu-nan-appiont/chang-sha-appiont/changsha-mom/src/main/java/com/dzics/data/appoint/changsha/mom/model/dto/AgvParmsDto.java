package com.dzics.data.appoint.changsha.mom.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @Classname AgvParmsDto
 * @Description 请求AGV 参数
 * @Date 2022/5/12 14:05
 * @Created by NeverEnd
 */
@Data
public class AgvParmsDto {
    /**
     * 请求ID
     */
    private String reqId;
    /**
     * 系统编码
     */
    private String reqSys;
    /**
     * 操作编码
     */
    private String reqType;
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
     * 物料组编码
     */
    private String materialGroup;

    /*
     * 生产订单号
     */
    private String wipOrderNo;

    /*
     * 工厂编号
     */
    private String Facility;

    /*
     * 顺序号
     */
    private String SequenceNo;

    /*
     * 工序号
     */
    private String OprSequenceNo;

    /**
     * 终点编码
     */
    private String destNo;

    /**
     * 目的投料点类型
     */
    private String destPointType;

    /**
     * 需求时间
     */
    private String requireTime;
    /**
     * 发送时间
     */
    private String sendTime;
    /**
     * 预留参数1
     */
    private String paramRsrv1;
    /**
     * 预留参数2
     */
    private String paramRsrv2;
    /**
     * 预留参数3
     */
    private String paramRsrv3;

    /**
     * 物料清单
     */
    private List<MaterialParmsDto> materialList;

}
