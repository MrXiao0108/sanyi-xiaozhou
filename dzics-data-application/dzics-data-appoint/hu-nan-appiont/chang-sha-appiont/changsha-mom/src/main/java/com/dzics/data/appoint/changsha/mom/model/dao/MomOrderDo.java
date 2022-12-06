package com.dzics.data.appoint.changsha.mom.model.dao;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Classname MomOrderDo
 * @Description MOM 订单列表
 * @Date 2022/4/26 15:21
 * @Created by NeverEnd
 */
@Data
public class MomOrderDo implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "MOM 下发订单表 ID")
    private String proTaskOrderId;

    @ApiModelProperty(value = "产线ID")
    private String lineId;

    @ApiModelProperty("产线名称")
    private String lineName;

    @ApiModelProperty("产线编号")
    private String lineCode;

    @ApiModelProperty(value = "生产任务订单号")
    private String wiporderno;

    /**
     * 订单类型  1：正常订单；2：返工返修订单
     */
    @ApiModelProperty(value = "订单类型 1：正常订单；2：返工返修订单")
    private String wipOrderType;

    /**
     * 订单物料
     */
    @ApiModelProperty(value = "订单物料=订单物料号")
    private String orderProductNo;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量 = 订单数量 = 计划生产数量")
    private Integer quantity;

    /**
     * 产品物料号
     */
    @ApiModelProperty("产品物料号")
    private String productNo;

    /**
     * 工序号
     */
    @ApiModelProperty("工序号")
    private String oprSequenceNo;

    /**
     * 工序名称
     */
    @ApiModelProperty("工序名称")
    private String oprSequenceName;

    /**
     * 顺序号
     */
    @ApiModelProperty("顺序号")
    private String sequenceNo;

    /**
     * 计划开始时间 yyyy-mm-dd hh24:mi:ss
     */
    @ApiModelProperty("计划开始时间")
    @JsonFormat(pattern = "yyy-MM-dd hh:mm:ss")
    private Date scheduledStartDate;

    /**
     * 计划结束时间  yyyy-mm-dd hh24:mi:ss
     */
    @JsonFormat(pattern = "yyy-MM-dd hh:mm:ss")
    @ApiModelProperty("计划结束时间")
    private Date scheduledCompleteDate;

    @ApiModelProperty("实际开始时间")
    @JsonFormat(pattern = "yyy-MM-dd hh:mm:ss")
    private Date realityStartDate;

    @ApiModelProperty("实际结束时间")
    @JsonFormat(pattern = "yyy-MM-dd hh:mm:ss")
    private Date realityCompleteDate;

    @ApiModelProperty("已生产数量")
    private String actualProduction;

    @ApiModelProperty("合格数量")
    private String qualified;

    @ApiModelProperty("不合格数量")
    private String unqualified;

    /**
     * 订单状态 110已下达 120进行中 130已完工 140已删除 150强制关闭
     */
    @ApiModelProperty("订单状态 110已下达 120进行中 130已完工 140已删除 150强制关闭，160暂停")
    private String progressStatus;

    @ApiModelProperty("库存")
    private String storage;

    @ApiModelProperty("订单编号")
    private String orderNo;
}

