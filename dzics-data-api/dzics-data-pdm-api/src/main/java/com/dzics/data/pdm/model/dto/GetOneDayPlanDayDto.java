package com.dzics.data.pdm.model.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author xnb
 * @date 2022/11/28 0028 10:12
 */
@Data
public class GetOneDayPlanDayDto {
    @ApiModelProperty(value = "每次生产计划记录id")
    @TableId(value = "plan_day_id", type = IdType.ASSIGN_ID)
    private String planDayId;

    @ApiModelProperty(value = "计划id")
    @TableField("plan_id")
    private String planId;

    @ApiModelProperty(value = "计划生产数量")
    @TableField("planned_quantity")
    private Long plannedQuantity;

    @ApiModelProperty("生产日期")
    @TableField("detector_time")
    private LocalDate detectorTime;
    @ApiModelProperty(value = "已完成数量")
    @TableField("completed_quantity")
    private Long completedQuantity;

    @ApiModelProperty(value = "完成率")
    @TableField("percentage_complete")
    private BigDecimal percentageComplete;

    /**
     * 产出率
     */
    @ApiModelProperty("产出率")
    @TableField("output_rate")
    private BigDecimal outputRate;
    /**
     * 合格率
     */
    @ApiModelProperty("合格率")
    @TableField("pass_rate")
    private BigDecimal passRate;

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
