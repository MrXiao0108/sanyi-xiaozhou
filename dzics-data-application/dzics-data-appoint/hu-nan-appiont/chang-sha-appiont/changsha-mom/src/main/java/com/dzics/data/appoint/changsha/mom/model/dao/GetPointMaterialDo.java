package com.dzics.data.appoint.changsha.mom.model.dao;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xnb
 * @date 2022/11/1 0001 9:07
 */
@Data
public class GetPointMaterialDo {

    @ApiModelProperty(value = "二维码")
    private String qrCode;

    @ApiModelProperty(value = "Mom订单号")
    private String momOrder;

    @ApiModelProperty(value = "物料编号")
    private String productNo;

    @ApiModelProperty(value = "数量")
    private String quantity;

    @ApiModelProperty(value = "料框号")
    private String frameCode;

    @ApiModelProperty(value = "料点编码")
    private String externalCode;
}
