package com.dzics.data.pdm.db.model.dao;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 设备生产数据
 */
@Data
public class EquipmentDataDetailsDo {
    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("生产数量")
    private Long workNum;
    @ApiModelProperty("创建时间")
    private String workDate;

}
