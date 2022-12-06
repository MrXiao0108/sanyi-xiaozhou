package com.dzics.data.appoint.changsha.mom.model.vo;


import com.dzics.data.common.base.model.page.PageLimit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class GetWorkingDetailsVo extends PageLimit {
    @ApiModelProperty("产线id")
    private String lineId;

    @ApiModelProperty("订单Id")
    private String orderId;

    @ApiModelProperty("工位ID")
    private String stationId;
    /**
     *
     */
    @ApiModelProperty("二维码")
    private String qrCode;

    @ApiModelProperty("唯一订单号")
    private String workpieceCode;


    @ApiModelProperty("开始时间")
//    上报开始时间 为开始
    private String startTime;

    @ApiModelProperty("结束时间")
//    上报完成时间 为结束
    private String endTime;
}
