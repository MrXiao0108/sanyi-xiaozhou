package com.dzics.data.pdm.db.model.dto;

import com.dzics.data.common.base.model.page.PageLimit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Administrator
 */
@Data
public class EquipmentDownExcelDo extends PageLimit {
    @ApiModelProperty(value = "站点Id")
    @NotNull(message = "站点Id不能为空")
    private String departId;

    @ApiModelProperty(value = "设备编号")
    private String equipmentNo;

    @ApiModelProperty(value = "机构编码")
    @JsonIgnore
    private String orgCode;

}
