package com.dzics.data.appoint.changsha.mom.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 向MOM报工的参数
 */
@Data
public class WorkReportVo {
    /**
     * 请求ID
     */
    private String reqId;
    /**
     * 系统编码
     */
    private String reqSys;
    /**
     * 工厂编号
     */
    @JsonProperty("")
    private String Facility;
    /**
     * 订单号
     */
    @JsonProperty("")
    private String WipOrderNo;
    /**
     * 顺序号
     */
    @JsonProperty("")
    private String SequenceNo;
    /**
     * 工序号
     */
    @JsonProperty("")
    private String OprSequenceNo;
    /**
     * 实际开始时间
     */
    @JsonProperty("")
    private String ActualStartDate;
    /**
     * 实际结束时间
     */
    @JsonProperty("")
    private String ActualCompleteDate;
    /**
     * 设备编号
     */
    @JsonProperty("")
    private String DeviceID;
    /**
     * 工序报工类型
     */
    @JsonProperty("")
    private String ProgressType;
    /**
     * 合格数量
     */
    @JsonProperty("")
    private BigDecimal Quantity;
    /**
     * 不合格数量
     */
    @JsonProperty("")
    private BigDecimal NGQuantity;
    /**
     * 报工员工号
     */
    @JsonProperty("")
    private String EmployeeNo;
    /**
     * 产品物料号
     */
    @JsonProperty("")
    private String ProductNo;
    /**
     * 产品序列号
     */
    @JsonProperty("")
    private String SerialNo;
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
     * 预留参数4
     */
    private String paramRsrv4;
    /**
     * 预留参数5
     */
    private String paramRsrv5;
    /**
     * 关重件信息
     */
    private List<KeyAccessory> keyaccessoryList;


}


