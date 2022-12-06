package com.dzics.data.pdm.db.model.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author Administrator
 */
@Data
public class ProductionPlanDo {

    @ExcelIgnore
    @ApiModelProperty("id，修改必填")
    @NotBlank(message = "Id不能为空")
    private String id;

    @ExcelProperty("日订单生产计划(件)")
    @ApiModelProperty("计划生产数量，修改必填")
    @Min(0)
    private Long plannedQuantity;

}
