package com.dzics.data.appoint.changsha.mom.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 工件编码订单，产线
 *
 * @author ZhangChengJun
 * Date 2021/5/20.
 * @since
 */
@Data
public class ReqWorkQrCodeOrder extends StartWokeOrderMooM {
    private Long orderId;

    private Long lineId;
    /**
     * 二维码
     */
    private String qrCode;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 产线号
     */
    private String lineNo;


    /**
     * 完成时间
     */
    private Date completeTime;

    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 工件制作流程记录 id
     */
    private String processFlowId;

    /**
     * 工位ID
     */
    private String stationId;
    /**
     * 1进开工 2出完工
     */
    private String outInputType;


    /**
     * 产品物料号
     */
    private String productNo;

    @ApiModelProperty("报工工位编号")
    private String dzStationCode;

    @ApiModelProperty("报工工位编号")
    private String dzStationCodeSpare;

    private String groupId;
}
