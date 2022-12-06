package com.dzics.data.appoint.changsha.mom.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xnb
 * @date 2022/10/25 0025 10:59
 */
@Data
public class MomQrCodeVo {
    @ApiModelProperty(value = "产品序列号")
    private String SerialNo;

    @ApiModelProperty(value = "订单号")
    private String WorkOrderNo;

    @ApiModelProperty(value = "顺序号")
    private String SequenceNo;

    @ApiModelProperty(value = "订单数量")
    private String OrderQty;

    @ApiModelProperty(value = "二维码")
    private String qrCode;

    @ApiModelProperty(value = "预留参数1")
    private String paramRsrv1;

    @ApiModelProperty(value = "预留参数2")
    private String paramRsrv2;

    @ApiModelProperty(value = "预留参数3")
    private String paramRsrv3;
}
