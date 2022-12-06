package com.dzics.data.appoint.changsha.mom.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * mom下发订单表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_order")
@ApiModel(value = "MomOrder对象", description = "mom下发订单表")
public class MomOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "mom下发订单表 ID")
    @TableId(value = "pro_task_order_id", type = IdType.ASSIGN_ID)
    private String proTaskOrderId;

    @ApiModelProperty(value = "接口类型")
    @TableField("taskType")
    private String tasktype;

    @ApiModelProperty(value = "消息ID")
    @TableField("taskId")
    private String taskid;

    @ApiModelProperty(value = "协议版本")
    @TableField("version")
    private Integer version;

    @ApiModelProperty(value = "唯一订单号")
    @TableField("WipOrderNo")
    private String wiporderno;

    @ApiModelProperty("临时下发订单号")
    @TableField("productAlias_productionLine")
    private String productAliasProductionLine;

    /**
     * 订单类型  1：正常订单；2：返工返修订单
     */
    @ApiModelProperty(value = "订单类型 1：正常订单；2：返工返修订单")
    @TableField("WipOrderType")
    private String wipOrderType;

    /**
     * 产线 例如1.5m/2.5m/活塞杆/缸筒/装配线
     */
    @ApiModelProperty("产线 例如1.5m/2.5m/活塞杆/缸筒/装配线")
    @TableField("ProductionLine")
    private String productionLine;

    /**
     * 工作中心  可为空
     */
    @ApiModelProperty("工作中心  可为空")
    @TableField("WorkCenter")
    private String workCenter;

    /**
     * 组  用来确定唯一工艺路线
     */
    @ApiModelProperty("组 用来确定唯一工艺路线")
    @TableField("WipOrderGroup")
    private String wipOrderGroup;

    /**
     * 组计数器  用来确定唯一工艺路线
     */
    @ApiModelProperty("组计数器 用来确定唯一工艺路线")
    @TableField("GroupCount")
    private String groupCount;

    /**
     * 订单物料
     */
    @ApiModelProperty(value = "订单物料")
    @TableField("ProductNo")
    private String productNo;

    /**
     * 产品ID
     */
    @ApiModelProperty(value = "产品ID")
    @TableField("product_id")
    private String productId;

    /**
     * 订单物料描述
     */
    @ApiModelProperty(value = "订单物料描述")
    @TableField("ProductName")
    private String productName;

    /**
     * 订单物料简码  中兴专用
     */
    @ApiModelProperty(value = "订单物料简码")
    @TableField("ProductAlias")
    private String productAlias;

    /**
     * 工厂  1820-中兴
     */
    @ApiModelProperty("工厂编号")
    @TableField("Facility")
    private String facility;

    /**
     * 数量  订单工件数量
     */
    @ApiModelProperty(value = "订单工件数量")
    @TableField("Quantity")
    private Integer quantity;


    /**
     * 实际开始时间
     */
    @ApiModelProperty("实际开始时间")
    @TableField("reality_start_date")
    @JsonFormat(pattern = "yyy-MM-dd hh:mm:ss")
    @DateTimeFormat(pattern = "yyy-MM-dd hh:mm:ss")
    private Date realityStartDate;

    /**
     * 实际结束时间
     */
    @ApiModelProperty("实际结束时间")
    @TableField("reality_complete_date")
    @JsonFormat(pattern = "yyy-MM-dd hh:mm:ss")
    @DateTimeFormat(pattern = "yyy-MM-dd hh:mm:ss")
    private Date realityCompleteDate;

    /**
     * 计划开始时间 yyyy-mm-dd hh24:mi:ss
     */
    @ApiModelProperty("计划开始时间")
    @TableField("ScheduledStartDate")
    private Date scheduledStartDate;

    /**
     * 计划结束时间  yyyy-mm-dd hh24:mi:ss
     */
    @ApiModelProperty("计划结束时间")
    @TableField("ScheduledCompleteDate")
    private Date scheduledCompleteDate;

    /**
     * 0未开始叫料  1已开始叫料  2叫料完成
     */
    @TableField("call_material_status")
    @ApiModelProperty("叫料状态")
    private Integer callMaterialStatus;

    /**
     * 110已下达 120进行中 130已完工 140已删除 150强制关闭，160暂停
     */
    @ApiModelProperty(value = "订单状态")
    @TableField("ProgressStatus")
    private String progressStatus;

    @ApiModelProperty(value = "创建数据机构编码")
    @TableField("org_code")
    private String orgCode;

    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @TableField("del_flag")
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 订单状态请求变更结果 1.操作进行中  2.操作完成
     */
    @ApiModelProperty(value = "订单状态请求变更结果 1.操作进行中  2.操作完成")
    @TableField("order_operation_result")
    private Integer orderOperationResult;

    @ApiModelProperty("报工状态")
    @TableField("work_reporting_status")
    private Boolean workReportingStatus;

    @ApiModelProperty(value = "订单实际产出数量")
    @TableField("order_output")
    private Integer orderOutput;

    @ApiModelProperty("ok 数量")
    @TableField("ok_num")
    private Integer okNum;

    @ApiModelProperty("NG 数量")
    @TableField("ng_num")
    private Integer ngNum;

    @ApiModelProperty(value = "订单发送请求前的状态")
    @TableField("order_old_state")
    private String orderOldState;

    /**
     * 对接码
     */
    @ApiModelProperty(value = "对接码")
    @TableField("docking_code")
    private String dockingCode;

    /**
     * 产线id
     */
    @ApiModelProperty(value = "产线id")
    @TableField("line_id")
    private String lineId;

    /**
     * 订单ID
     */
    @ApiModelProperty(value = "订单ID")
    @TableField("order_id")
    private String orderId;

    @ApiModelProperty(value = "预留参数1（工艺路线ID）")
    @TableField("paramRsrv1")
    private String paramRsrv1;

    @ApiModelProperty(value = "预留参数2")
    @TableField("paramRsrv2")
    private String paramRsrv2;

    @ApiModelProperty(value = "Mom下发订单原Json格式")
    @TableField("JsonOriginalData")
    private String JsonOriginalData;
}
