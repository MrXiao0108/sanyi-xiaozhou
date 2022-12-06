package com.dzics.data.pdm.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author Administrator
 */
@Data
public class MachineDownExcelVo {
    @ExcelProperty("订单编号")
    private String orderNo;

    @ExcelProperty("产线名称")
    private String lineName;

    @ExcelProperty("机床序号")
    private String equipmentNo;

    @ExcelProperty("机床编号")
    private String equipmentCode;

    @ExcelProperty("机床名称")
    private String equipmentName;

    @ExcelProperty("累计停机时间")
    private String downTime;

    @ExcelProperty("停机次数")
    private String downSum;

}
