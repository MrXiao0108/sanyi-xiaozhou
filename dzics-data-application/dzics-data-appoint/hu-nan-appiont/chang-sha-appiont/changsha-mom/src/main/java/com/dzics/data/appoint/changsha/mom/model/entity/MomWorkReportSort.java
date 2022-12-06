package com.dzics.data.appoint.changsha.mom.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author NeverEnd
 * @since 2022-05-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_work_report_sort")
@ApiModel(value = "MomWorkReportSort对象", description = "")
public class MomWorkReportSort implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "订单ID")
    @TableField("pro_task_order_id")
    private String proTaskOrderId;

    @ApiModelProperty(value = "MOM订单号")
    @TableField("wip_order_no")
    private String wipOrderNo;
    /**
     * 产线id
     */
    @TableField("line_id")
    private String lineId;

    /**
     * 订单ID
     */
    @TableField("order_id")
    private String orderId;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
