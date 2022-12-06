package com.dzics.data.wrp.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * <p>
 * 工件制作流程记录
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_working_flow_big")
@ApiModel(value="DzWorkingFlowBig对象", description="工件制作流程记录")
public class DzWorkingFlowBig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工序流程ID")
    @TableId(value = "process_flow_big_id", type = IdType.ASSIGN_ID)
    private String processFlowBigId;

    @ApiModelProperty(value = "工件二维码")
    @TableField("qr_code")
    private String qrCode;

    @ApiModelProperty(value = "订单Id")
    @TableField("order_id")
    private String orderId;

    @ApiModelProperty(value = "产线ID")
    @TableField("line_id")
    private String lineId;

    @ApiModelProperty(value = "时间")
    @TableField("work_time")
    private Date workTime;

    @TableField("work_date")
    private LocalDate workDate;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;



}
