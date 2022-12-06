package com.dzics.data.appoint.changsha.mom.model.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author xnb
 * @date 2022/10/31 0031 14:29
 */
@Data
public class PointMaterialsModel{

    @ApiModelProperty(value = "投料点编号")
    @NotBlank(message = "投料点编号不能为空")
    private String externalCode;


    @ApiModelProperty(value = "Mom订单号")
    private String momOrder;

    @ApiModelProperty(value = "二维码")
    private String qrCode;

    @ApiModelProperty(value = "料框新料点")
    @ApiParam(hidden = true)
    private String newExCode;


}
