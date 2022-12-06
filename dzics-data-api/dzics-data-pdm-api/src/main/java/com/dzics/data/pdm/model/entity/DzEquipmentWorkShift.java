package com.dzics.data.pdm.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;

/**
 * <p>
 * 设备工作班次表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_equipment_work_shift")
@ApiModel(value="DzEquipmentWorkShift对象", description="设备工作班次表")
public class DzEquipmentWorkShift implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type =IdType.ASSIGN_ID)
    private String id;

    @TableField("production_line_id")
    private String productionLineId;

    @ApiModelProperty(value = "班次名称")
    @TableField("work_name")
    private String workName;


    @ApiModelProperty(value = "班次开始时间",dataType = "java.lang.String")
    @TableField("start_time")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @ApiModelProperty(value = "班次结束时间",dataType = "java.lang.String")
    @TableField("end_time")
    private LocalTime endTime;

    @ApiModelProperty(value = "排序值,开始班次值最小")
    @TableField("sort_no")
    private Integer sortNo;

    @ApiModelProperty(value = "机构编码")
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



}
