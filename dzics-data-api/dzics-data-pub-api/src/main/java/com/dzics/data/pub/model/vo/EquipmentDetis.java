package com.dzics.data.pub.model.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EquipmentDetis {
    private String id;
    private String postscript;

    private Integer isShow;
    private String nickName;
    private String equipmentName;

    private String doorCode;

    private Integer equipmentType;

    private String equipmentNo;

    private String equipmentCode;

    private String lineId;

    @ApiModelProperty("站点名称")
    @ExcelProperty("归属站点")
    private String departName;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("产线名称")
    @ExcelProperty("产线名称")
    private String lineName;

    @ApiModelProperty("订单id")
    @ExcelIgnore
    private String orderId;

    @ApiModelProperty("历史生产总数")
    @ExcelIgnore
    private String totalNum;

}

