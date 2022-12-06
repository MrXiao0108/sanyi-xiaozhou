package com.dzics.data.appoint.changsha.mom.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author xnb
 * @date 2022/11/21 0021 11:28
 */
@Data
public class EditMainTenPatrolVo {

    @ApiModelProperty(value = "巡检ID",required = true)
    @NotBlank(message = "巡检ID不能为空")
    private String id;

    @ApiModelProperty(value = "类型（1：巡检、2：维修）",required = true)
    private Integer type;

    @ApiModelProperty(value = "内容")
    private String message;
}
