package com.dzics.data.appoint.changsha.mom.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author xnb
 * @date 2022/11/21 0021 10:53
 */
@Data
public class AddMainTenPatrolVo {

    @ApiModelProperty(value = "订单ID",required = true)
    @NotBlank(message = "订单ID不能为空")
    private String orderId;

    @ApiModelProperty(value = "订单编号",required = true)
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "类型（1：巡检、2：维修）",required = true)
    @NotBlank(message = "类型不能为空")
    private String type;

    @ApiModelProperty(value = "内容")
    private String message;

    @Min(1)
    @ApiModelProperty(value = "间隔时间")
    private Integer intervalTime;

    @ApiModelProperty(value = "模块类型（1、定期巡检维修，2、主动巡检维修）",required = true)
    @NotBlank(message = "模块类型 必填1或2")
    private String modelType;
}
