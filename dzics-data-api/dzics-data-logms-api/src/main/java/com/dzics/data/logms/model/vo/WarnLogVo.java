package com.dzics.data.logms.model.vo;

import com.dzics.data.common.base.model.page.PageLimitBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;

/**
 * @author xnb
 * @date 2022/11/10 0010 14:02
 */
@Data
public class WarnLogVo extends PageLimitBase {

    @ApiModelProperty(value = "订单编号",required = true)
    @NotNull(message = "订单编号必填")
    private String orderNo;

//    @ApiModelProperty(value = "产线编号",required = true)
//    @NotNull(message = "产线编号必填")
//    private String lineNo;

    @ApiModelProperty(value = "设备编号",required = true)
    @NotNull(message = "设备编号必填")
    private String equipmentCode;

    @ApiModelProperty(value = "设备类型",required = true)
    @NotNull(message = "设备类型必填")
    private String equipmentType;

    @ApiModelProperty(value = "告警内容")
    private String message;

    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String beginTime;

    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;


}
