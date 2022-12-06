package com.dzics.data.logms.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 设备运行告警日志
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_real_time_logs")
@ApiModel(value="SysRealTimeLogs对象", description="设备运行告警日志")
public class SysRealTimeLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelIgnore
    @ApiModelProperty(value = "key")
    @TableId(value = "communication_key", type = IdType.ASSIGN_ID)
    private String communicationKey;

    @ApiModelProperty(value = "消息id")
    @TableField("message_id")
    @ExcelProperty("消息编号")
    private String messageId;

    @ExcelIgnore
    @ApiModelProperty(value = "队列名称")
    @TableField("queue_name")
    private String queueName;

    @ExcelIgnore
    @ApiModelProperty(value = "客户端id")
    @TableField("client_id")
    private String clientId;

    @ApiModelProperty(value = "订单编码")
    @TableField("order_code")
    @ExcelProperty("订单编码")
    private String orderCode;

    @ApiModelProperty(value = "产线序号")
    @TableField("line_no")
    @ExcelProperty("产线序号")
    private String lineNo;

    @ApiModelProperty(value = "设备类型")
    @TableField("device_type")
    @ExcelProperty("设备类型")
    private String deviceType;

    @ApiModelProperty(value = "设备编码")
    @TableField("device_code")
    @ExcelProperty("设备编码")
    private String deviceCode;

    @ExcelIgnore
    @ApiModelProperty(value = "1正常日志2告警日志")
    @TableField("message_type")
    private Integer messageType;

    @ApiModelProperty(value = "消息内容")
    @TableField("message")
    @ExcelProperty("消息内容")
    private String message;

    @ApiModelProperty(value = "消息发送时间")
    @TableField("timestamp_time")
    @ExcelProperty("消息发送时间")
    private Date timestampTime;


}
