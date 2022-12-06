package com.dzics.data.tool.db.model.dao;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.dzics.data.tool.model.entity.DzToolCompensationData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetToolInfoDataListDo extends DzToolCompensationData {

    @ExcelIgnore
    @ApiModelProperty("订单编号")
    private String orderNo;

    @ExcelProperty("产线名称")
    @ApiModelProperty("产线名称")
    private String lineName;

    @ApiModelProperty("站点名称")
    @ExcelIgnore
    private String departName;

}
