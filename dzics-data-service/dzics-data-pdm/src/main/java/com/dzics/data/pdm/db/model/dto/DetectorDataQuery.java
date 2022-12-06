package com.dzics.data.pdm.db.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 检测记录查询条件封装类
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@Data
public class DetectorDataQuery{
    @ApiModelProperty(value = "1大正用户，0普通用户")
    private String userIdentity;
    /**
     * 订单Id
     */
    @ApiModelProperty(value = "订单ID")
    private String orderId;

    @ApiModelProperty("产线ID")
    @NotBlank(message = "产线ID")
    private String lineId;

    @ApiModelProperty(value = "产品id", required = true)
    @NotBlank(message = "请选择查询产品")
    private String productNo;
    @ApiModelProperty("站点id")
    private String departId;

    @ApiModelProperty("检测结果 1 正常，0异常")
    private Integer detectionResult;

    @ApiModelProperty("当前页")
    private int page = 1;
    @ApiModelProperty("每页查询条数")
    private int limit = 10;


    @ApiModelProperty("排序字段")
    private String field;
    @ApiModelProperty("ASC OR DESC OR 空字符串")
    private String type;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty("产线号")
    private String lineNo;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;
}
