package com.dzics.data.common.base.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class DzEquipmentWorkShiftDo {

    @ApiModelProperty(value = "班次名称")
    @TableField("work_name")
    private String workName;


    @ApiModelProperty(value = "班次开始时间",dataType = "java.lang.String")
    @TableField("start_time")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private String startTime;

    @ApiModelProperty(value = "班次结束时间",dataType = "java.lang.String")
    @TableField("end_time")
    private String endTime;

    @ApiModelProperty(value = "排序值,开始班次值最小")
    @TableField("sort_no")
    private Integer sortNo;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    private String orgCode;



}
