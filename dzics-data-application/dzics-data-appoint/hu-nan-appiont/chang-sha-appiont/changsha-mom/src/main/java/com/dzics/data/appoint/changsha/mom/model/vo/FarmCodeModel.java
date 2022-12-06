package com.dzics.data.appoint.changsha.mom.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author xnb
 * @date 2022/10/31 0031 10:41
 */
@Data
public class FarmCodeModel {

    @ApiModelProperty(value = "主键")
    @NotBlank(message = "主键不能为空，并且长度要大于0")
    private String materialPointId;

    @ApiModelProperty(value = "料框编号")
    @NotBlank(message = "料框编号不能为空")
    private String farmCode;

}
