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
 * 待报工记录
 * </p>
 *
 * @author NeverEnd
 * @since 2022-04-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_wait_work_report")
@ApiModel(value = "MomWaitWorkReport对象", description = "待报工记录")
public class MomWaitWorkReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "MOM订单ID")
    @TableField("order_id")
    private String orderId;

    @TableField("line_id")
    private String lineId;

    @TableField("order_no")
    private String orderNo;

    @TableField("line_no")
    private String lineNo;

    @TableField("qr_code")
    private String qrCode;

    @TableField("out_input_type")
    private String outInputType;

    @TableField("production_time")
    private Date productionTime;

    @TableField("station_json_a")
    private String stationJsonA;

    @TableField("station_json_b")
    private String stationJsonB;

    @ApiModelProperty(value = "MOM报工响应")
    @TableField("mom_response")
    private String momResponse;

    @ApiModelProperty(value = "删除状态(0正常 1删除 )")
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


}
