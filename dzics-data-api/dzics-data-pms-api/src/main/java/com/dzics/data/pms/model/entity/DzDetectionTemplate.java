package com.dzics.data.pms.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 产品检测设置默认模板
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_detection_template")
@ApiModel(value="DzDetectionTemplate对象", description="产品检测设置默认模板")
public class DzDetectionTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一属性")
    @TableId(value = "detection_id", type = IdType.ASSIGN_ID)
    private String detectionId;

    @ApiModelProperty(value = "0展示1不展示")
    @TableField("is_show")
    private Integer isShow;

    @ApiModelProperty(value = "表格字段值")
    @TableField("table_col_val")
    private String tableColVal;

    @ApiModelProperty(value = "检测内容 列名")
    @TableField("table_col_con")
    private String tableColCon;

    @ApiModelProperty(value = "检测编号")
    @TableField("serial_number")
    private String serialNumber;

    @ApiModelProperty(value = "标准值")
    @TableField("standard_value")
    private BigDecimal standardValue;

    @ApiModelProperty(value = "上线值")
    @TableField("upper_value")
    @JsonIgnore
    private BigDecimal upperValue;

    @ApiModelProperty(value = "下线值")
    @TableField("lower_value")
    @JsonIgnore
    private BigDecimal lowerValue;

    @ApiModelProperty(value = "补偿值")
    @TableField("compensation_value")
    private BigDecimal compensationValue;

    @ApiModelProperty(value = "偏移值")
    @TableField("deviation_value")
    @JsonIgnore
    private BigDecimal deviationValue;


}
