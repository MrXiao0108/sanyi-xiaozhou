package com.dzics.data.tool.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 刀具表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_tool_info")
@ApiModel(value="DzToolInfo对象", description="刀具表")
public class DzToolInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "所属刀具组id")
    @TableField("tool_groups_id")
    private String toolGroupsId;

    @ApiModelProperty(value = "刀具编号")
    @TableField("tool_no")
    private Integer toolNo;

    @ApiModelProperty(value = "刀具寿命")
    @TableField("tool_life")
    private Integer toolLife;

    @ApiModelProperty(value = "刀具寿命计数器")
    @TableField("tool_life_counter")
    private Integer toolLifeCounter;

    @ApiModelProperty(value = "刀具寿命计数器类型 0:数量  1:分钟")
    @TableField("tool_life_counter_type")
    private Integer toolLifeCounterType;

    @ApiModelProperty(value = "刀具半径（磨损）")
    @TableField("tool_wear_radius")
    private BigDecimal toolWearRadius;

    @ApiModelProperty(value = "刀尖方向")
    @TableField("tool_nose_direction")
    private Integer toolNoseDirection;

    @ApiModelProperty(value = "刀具描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    private String orgCode;

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
