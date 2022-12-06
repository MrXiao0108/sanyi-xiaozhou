package com.dzics.data.appoint.changsha.mom.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 巡检维修表
 * </p>
 *
 * @author xnb
 * @since 2022-11-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dzics_maintenance_patrol")
@ApiModel(value="DzicsMaintenancePatrol对象", description="巡检维修表")
public class DzicsMaintenancePatrol implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "订单ID")
    @TableField("order_id")
    private String orderId;

    @ApiModelProperty(value = "订单编号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty(value = "类型（1：巡检、2：维修）")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "间隔时间")
    @TableField("interval_time")
    private Integer intervalTime;

    @ApiModelProperty(value = "执行时间")
    @TableField("execute_data")
    private String executeData;

    @ApiModelProperty(value = "下一次执行时间")
    @TableField("next_execute_data")
    private String nextExecuteData;

    @ApiModelProperty(value = "内容")
    @TableField("message")
    private String message;

    @ApiModelProperty(value = "是否展示（0展示，1不展示）")
    @TableField("is_show")
    private Integer isShow;

    @JsonIgnore
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @JsonIgnore
    @ApiModelProperty(value = "修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @JsonIgnore
    @ApiModelProperty(value = "模块类型（1、定期巡检维修，2、主动巡检维修）")
    @TableField(value = "model_type", fill = FieldFill.INSERT_UPDATE)
    private String modelType;


}
