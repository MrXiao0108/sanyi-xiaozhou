package com.dzics.data.pub.model.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.dzics.data.common.base.model.write.DeviceTypeConverter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class EquipmentListDo {
    @ExcelIgnore
    private String id;

    @ApiModelProperty("安全门编号")
    @ExcelIgnore
    private String doorCode;

    @ExcelProperty("订单编号")
    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("产线名称")
    @ExcelProperty("产线名称")
    private String lineName;

    @ApiModelProperty(value = "设备序号")
    @ExcelProperty("设备序号")
    private String equipmentNo;

    @ApiModelProperty(value = "设备编码")
    @ExcelProperty("设备编码")
    private String equipmentCode;

    @ApiModelProperty(value = "设备名称")
    @ExcelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("昵称")
    @ExcelProperty("设备昵称")
    private String nickName;

    @ApiModelProperty(value = "设备类型(1检测设备,2机床,3机器人)")
    @ExcelProperty(value = "设备类型",converter = DeviceTypeConverter.class)
    private Integer equipmentType;

    @ApiModelProperty("连接状态")
    @ExcelProperty("连接状态")
    private String connectState;

    @ApiModelProperty("操作模式")
    @ExcelProperty("操作模式")
    private String operatorMode;

    @ApiModelProperty("运行状态")
    @ExcelProperty("运行状态")
    private String runStatus;

    @ApiModelProperty("历史生产总数")
    @ExcelProperty("历史生产总数")
    private String proNum;

    @ApiModelProperty(value = "新增人")
    @ExcelProperty("新增人")
    private String createBy;

    @ApiModelProperty(value = "新增时间")
    @ExcelProperty("新增时间")
    private Date createTime;

    @ApiModelProperty(value = "产线id")
    @ExcelIgnore
    private String lineId;

    @ApiModelProperty("产线序号")
    @ExcelIgnore
    private String lineNo;

    @ApiModelProperty("是否看板显示该设备(0不显示，1显示)")
    @ExcelIgnore
    private Integer isShow;

    @ApiModelProperty("订单id")
    @ExcelIgnore
    private String orderId;

    @ExcelProperty("机床连接状态")
    @ExcelIgnore
    private String b561;
    @ExcelProperty("机床运行状态")
    @ExcelIgnore
    private String b562;
    @ExcelProperty("机床操作模式")
    @ExcelIgnore
    private String b565;

    @ExcelProperty("机器人连接状态")
    @ExcelIgnore
    private String a561;
    @ExcelProperty("机器人运行状态")
    @ExcelIgnore
    private String a563;
    @ExcelProperty("机器人操作模式")
    @ExcelIgnore
    private String a562;

}
