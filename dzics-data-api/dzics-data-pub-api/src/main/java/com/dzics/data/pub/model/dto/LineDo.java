package com.dzics.data.pub.model.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.dzics.data.common.base.model.dto.DzEquipmentWorkShiftDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class LineDo {
    @ExcelProperty("订单编号")
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ExcelProperty("归属站点")
    @ApiModelProperty("站点名称")
    private String departName;
    @ExcelProperty("产线编码")
    @ApiModelProperty(value = "产线编码")
    private String lineCode;
    @ExcelProperty("产线名称")
    @ApiModelProperty(value = "产线名称")
    private String lineName;
    @ExcelProperty("产线序号")
    @ApiModelProperty(value = "产线序号")
    private String lineNo;
    @ExcelProperty("班次")
    @ApiModelProperty("当前班次名称")
    private String work_name;
    @ExcelProperty("班次起始时间")
    @ApiModelProperty(value = "班次起始时间",dataType = "java.lang.String")
    private String start_time;
    @ExcelProperty("班次结束时间")
    @ApiModelProperty(value = "班次结束时间",dataType = "java.lang.String")
    private String end_time;
    @ExcelProperty("备注")
    @ApiModelProperty(value = "备注")
    private String remarks;
    @ExcelProperty("绑定设备名称")
    @ApiModelProperty(value = "绑定设备名称")
    private String equipmentName;
    @ExcelProperty("绑定设备编号")
    @ApiModelProperty(value = "绑定设备编号")
    private String equipmentCode;
    @ExcelProperty("新增人")
    @ApiModelProperty(value = "创建人")
    private String createBy;


    @ExcelIgnore
    @ApiModelProperty(value = "班次集合")
    List<DzEquipmentWorkShiftDo> workShiftVos;
    @ExcelIgnore
    @ApiModelProperty(value = "绑定设备序号")
    private String equipmentNo;
    @ExcelIgnore
    private String id;
    @ExcelIgnore
    @ApiModelProperty(value = "订单id")
    private String orderId;
    @ExcelIgnore
    @ApiModelProperty(value = "统计产线产量设备id")
    private String statisticsEquimentId;
    @ExcelIgnore
    @ApiModelProperty(value = "机构编码")
    private String orgCode;
    @ExcelIgnore
    @ApiModelProperty(value = "删除状态(0正常 1删除 )")
    private Boolean delFlag;
    @ExcelIgnore
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ExcelIgnore
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    @ExcelIgnore
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    @ExcelIgnore
    @ApiModelProperty(value = "1启动 0禁用")
    private String status;

    @ApiModelProperty(value = "产线类型(活塞杆，缸筒)")
    private String lineType;

}
