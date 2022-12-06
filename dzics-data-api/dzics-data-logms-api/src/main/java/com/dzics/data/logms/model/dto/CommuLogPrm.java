package com.dzics.data.logms.model.dto;

import com.dzics.data.common.base.model.dto.SearchTimeBase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 通信日志参数
 *
 * @author ZhangChengJun
 * Date 2021/3/8.
 * @since
 */
@Data
public class CommuLogPrm extends SearchTimeBase {
    @ApiModelProperty("队列名称")
    private String queuename;

    @ApiModelProperty("订单编号")
    @NotNull(message = "订单编号必填")
    private String orderNo;

    @ApiModelProperty("设备类型")
    @NotNull(message = "设备类型必填")
    private String devicetype;

    @ApiModelProperty("产线ID")
    @NotNull(message = "产线必填")
    private String lineId;

    @ApiModelProperty("产线编码")
    @JsonIgnore
    private String lineNo;

    @ApiModelProperty("设备编码")
    @NotNull(message = "设备编码必填")
    private String devicecode;


}
