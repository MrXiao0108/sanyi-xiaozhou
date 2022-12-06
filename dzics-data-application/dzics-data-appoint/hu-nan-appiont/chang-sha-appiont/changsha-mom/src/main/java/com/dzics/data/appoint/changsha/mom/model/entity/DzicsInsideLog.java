package com.dzics.data.appoint.changsha.mom.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.dzics.data.appoint.changsha.mom.model.enumeration.DzicsInsideConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author xnb
 * @since 2022-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dzics_inside_log")
@ApiModel(value="DzicsInsideLog对象", description="")
public class DzicsInsideLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelIgnore
    @JsonIgnore
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private BigInteger id;


    @ExcelProperty(value = "业务类型(103：满料拖出、17：Mom订单工序下发、4：生产叫料、1：Mom订单下发)",converter = DzicsInsideConverter.class)
    @ApiModelProperty(value = "业务类型")
    @TableField("business_type")
    private String businessType;

    @ExcelIgnore
    @JsonIgnore
    @ApiModelProperty(value = "请求参数")
    @TableField("request_content")
    private String requestContent;

    @ExcelIgnore
    @JsonIgnore
    @ApiModelProperty(value = "返回信息")
    @TableField("redone_content")
    private String redoneContent;

    @ExcelProperty(value = "异常信息")
    @ApiModelProperty(value = "异常信息")
    @TableField("throw_msg")
    private String throwMsg;

    @ExcelProperty(value = "处理结果")
    @ApiModelProperty(value = "处理结果")
    @TableField("state")
    private String state;

    @ExcelProperty(value = "时间")
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ExcelIgnore
    @JsonIgnore
    @ApiModelProperty(value = "订单ID")
    @TableField("order_id")
    private String orderId;

    @ExcelIgnore
    @JsonIgnore
    @ApiModelProperty(value = "订单编号")
    @TableField("order_no")
    private String orderNo;

    @ExcelIgnore
    @JsonIgnore
    @ApiModelProperty(value = "产线ID")
    @TableField("line_id")
    private String lineId;

    @ExcelIgnore
    @JsonIgnore
    @ApiModelProperty(value = "产线编号")
    @TableField("line_no")
    private String lineNo;

    @ExcelIgnore
    @JsonIgnore
    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;


}
