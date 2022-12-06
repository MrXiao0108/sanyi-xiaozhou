package com.dzics.data.logms.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.dzics.data.common.base.model.write.DeviceTypeConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 通信日志
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_communication_log")
@ApiModel(value = "SysCommunicationLog对象", description = "通信日志")
public class SysCommunicationLog implements Serializable {
    private static final long serialVersionUID = 1L;
    @ExcelIgnore
    @ApiModelProperty(value = "key")
    @TableId(value = "communication_key", type = IdType.ASSIGN_ID)
    private String communicationKey;

    @ApiModelProperty(value = "订单编号")
    @ExcelProperty("订单编号")
    @TableField("order_code")
    private String orderCode;


    @ApiModelProperty(value = "产线序号")
    @ExcelProperty("产线序号")
    @TableField("line_no")
    private String lineNo;


    @ApiModelProperty(value = "队列名称")
    @ExcelProperty("队列名称")
    @TableField("queue_name")
    private String queueName;
    /**
     * 设备ID
     */
    @ApiModelProperty("设备ID")
    @TableField("device_id")
    private String deviceId;

    @ApiModelProperty(value = "机器人编号")
    @ExcelProperty("机器人编号")
    @TableField("client_id")
    private String clientId;

    @ApiModelProperty(value = "消息编号")
    @ExcelProperty("消息编号")
    @TableField("message_id")
    private String messageId;

    @ApiModelProperty(value = "设备类型")
    @ExcelProperty(value = "设备类型", converter = DeviceTypeConverter.class)
    @TableField("device_type")
    private String deviceType;

    @ApiModelProperty(value = "设备编码")
    @ExcelProperty("设备编码")
    @TableField("device_code")
    private String deviceCode;


    @ApiModelProperty(value = "消息")
    @ExcelProperty("消息")
    @TableField("message")
    private String message;

    @ApiModelProperty(value = "时间")
    @ExcelProperty("时间")
    @TableField("send_timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date sendTimestamp;


    @ApiModelProperty(value = "创建时间")
    @ExcelIgnore
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date createTime;
    /**
     * 检验是否通过 true 通过 false 未通过
     */
    @ApiModelProperty(value = "检验是否通过 true 通过 false 未通过")
    @ExcelIgnore
    @TableField("ok_check")
    private Boolean okCheck;

}
