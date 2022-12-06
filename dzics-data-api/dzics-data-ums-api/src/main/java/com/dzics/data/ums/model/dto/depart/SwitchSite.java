package com.dzics.data.ums.model.dto.depart;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ZhangChengJun
 * Date 2021/1/11.
 */
@Data
public class SwitchSite {
    @ApiModelProperty(value = "站点id", required = true)
    @NotNull(message = "请选择站点")
    private String id;
}
