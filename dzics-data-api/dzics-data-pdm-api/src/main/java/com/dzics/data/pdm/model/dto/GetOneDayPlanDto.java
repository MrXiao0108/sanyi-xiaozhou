package com.dzics.data.pdm.model.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author xnb
 * @date 2022/11/28 0028 9:55
 */
@Data
public class GetOneDayPlanDto {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "产线id")
    @TableField("line_id")
    private String lineId;

    @ApiModelProperty(value = "0日生产计划，1周计划，2月计划，3年计划")
    @TableField("plan_type")
    private Integer planType;

    @ApiModelProperty(value = "计划生产数量")
    @TableField("planned_quantity")
    private Long plannedQuantity;

    @ApiModelProperty(value = "状态（1启用，0不启用）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    private String orgCode;

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

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "产线名称")
    private String lineName;
}
