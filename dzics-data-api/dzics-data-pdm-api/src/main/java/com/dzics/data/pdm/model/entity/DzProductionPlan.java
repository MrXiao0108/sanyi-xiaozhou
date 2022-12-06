package com.dzics.data.pdm.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 产线日生产计划表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_production_plan")
@ApiModel(value="DzProductionPlan对象", description="产线日生产计划表")
public class DzProductionPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type =IdType.ASSIGN_ID)
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


}
