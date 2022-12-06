package com.dzics.data.appoint.changsha.mom.model.dto;

import com.dzics.data.common.base.model.page.LimitBase;
import com.dzics.data.common.base.model.page.PageLimit;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Classname SearchOrderParms
 * @Description 描述
 * @Date 2022/4/26 15:30
 * @Created by NeverEnd
 */
@Data
public class SearchOrderParms extends PageLimit implements Serializable {

    @ApiModelProperty(value = "产线ID")
    private String lineId;

    @ApiModelProperty(value = "订单号")
    private String wipOrderNo;

    @ApiModelProperty(value = "订单类型 1：正常订单；2：返工返修订单")
    private String wipOrderType;

    @ApiModelProperty(value = "生产任务订单号")
    private String wiporderno;

    @ApiModelProperty(value = "产品物料号")
    private String productNo;
    /**
     * 110	已下达	SAP生产订单释放后，通过接口下发MOM系统
     * 120	进行中	首道工序开工后，订单状态转为 进行中
     * 130	已完工	最后一道工序全数报完工后，订单状态转为已完工
     * 140	已删除	接收SAP的删除订单，开工之前可以进行删除
     * 150	强制关闭 	接收SAP的强制关闭信息，中断现场操作
     */
    @ApiModelProperty(value = " 110已下达 120进行中 130已完工 140已删除 150强制关闭")
    private String progressStatus;


    /**
     * 计划开始时间 yyyy-mm-dd hh24:mi:ss
     * 格式化接受时间
     */
    @ApiModelProperty(value = "计划开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date scheduledStartDate;

    /**
     * 计划结束时间  yyyy-mm-dd hh24:mi:ss
     */
    @ApiModelProperty(value = "计划结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date scheduledCompleteDate;

    @ApiModelProperty("起始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String startTime;

    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String endTime;
}
