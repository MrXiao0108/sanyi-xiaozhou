package com.dzics.data.pdm.db.model.dao;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EquipmentDataDo {
    @ApiModelProperty(value = "订单编号")
    @ExcelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty(value = "产线名称")
    @ExcelProperty("产线名称")
    private String lineName;

    @ApiModelProperty(value = "设备序号")
    @ExcelProperty("机器人序号")
    private String equipmentNo;


    @ApiModelProperty(value = "设备名称")
    @ExcelProperty("机器人名称")
    private String equipmentName;

    @ApiModelProperty(value = "班次日期")
    @ExcelProperty("日期")
    private String workData;

    @ApiModelProperty("班次")
    @ExcelProperty("班次")
    private String workName;

    @ApiModelProperty("班次时间")
    @ExcelProperty("班次时间")
    private String timeRange;

    @ApiModelProperty("生产数量(当前产量)")
    @ExcelProperty("生产数量")
    private String nowNum;

    @ApiModelProperty("毛坯数量")
    @ExcelProperty("毛坯数量")
    private String roughNum;

    @ApiModelProperty("合格数量")
    @ExcelProperty("合格数量")
    private String qualifiedNum;

    @ApiModelProperty("不良品数量")
    @ExcelProperty("不良品数量")
    private String badnessNum;


}
