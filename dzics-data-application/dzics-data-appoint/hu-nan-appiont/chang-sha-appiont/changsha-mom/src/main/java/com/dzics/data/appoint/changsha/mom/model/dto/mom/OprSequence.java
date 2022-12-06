package com.dzics.data.appoint.changsha.mom.model.dto.mom;

import com.dzics.data.appoint.changsha.mom.model.dto.ComponentList;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订单工序组
 */
@Data
public class OprSequence implements Serializable {
    /**
     * WipOrderNo : 850026989585
     * ProductNo : BCB005675939
     * ProductAlias :
     * Facility : 5802
     * ProductionLine : 5802XL01
     * SequenceNo : 000000
     * OprSequenceNo : 0040
     * OprSequenceName : 修割
     * ScheduledStartDate : 2022-04-22 08:12:57
     * ScheduledCompleteDate : 2022-04-22 08:12:57
     * OprSequenceType : 1
     * WorkCenter : 106122
     * WorkCenterName : 备料中心-修割
     * WorkStation : 5802XL01A0331
     * WorkStationName : 修割打磨工位
     * ProgressStatus : 110
     * Quantity : 4
     * NextSequenceNo : 000000
     * NextOprSequenceNo : 0050
     * NextOprSequenceName : 打磨
     * NextWorkCenter : 106122
     * paramRsrv1 :
     * paramRsrv2 :
     * ComponentList : []
     */
    /**
     * 订单号
     */
    private String WipOrderNo;

    /**
     *产品物料号
     */
    private String ProductNo;
    /**
     *订单物料简码
     */
    private String ProductAlias;
    /**
     *工厂
     */
    private String Facility;
    /**
     *产线
     */
    private String ProductionLine;
    /**
     *顺序号
     */
    private String SequenceNo;
    /**
     *工序号
     */
    private String OprSequenceNo;
    /**
     *工序名称
     */
    private String OprSequenceName;
    /**
     *计划开始时间
     */
    private String ScheduledStartDate;
    /**
     *计划结束时间
     */
    private String ScheduledCompleteDate;
    /**
     *工序类型
     */
    private String OprSequenceType;
    /**
     *工作中心
     */
    private String WorkCenter;
    /**
     *工作中心描述
     */
    private String WorkCenterName;
    /**
     *工位
     */
    private String WorkStation;
    /**
     *工位描述
     */
    private String WorkStationName;
    /**
     *工序状态
     */
    private String ProgressStatus;
    /**
     *工序数量
     */
    private int Quantity;
    /**
     *下一工序顺序号
     */
    private String NextSequenceNo;
    /**
     *下一工序工序号
     */
    private String NextOprSequenceNo;
    /**
     *下一工序工序名称
     */
    private String NextOprSequenceName;
    /**
     *下一工序工作中心
     */
    private String NextWorkCenter;
    /**
     *预留参数1
     */
    private String paramRsrv1;
    /**
     *预留参数2
     */
    private String paramRsrv2;
    /**
     *组件列表
     */
    private List<com.dzics.data.appoint.changsha.mom.model.dto.ComponentList> ComponentList;
}
