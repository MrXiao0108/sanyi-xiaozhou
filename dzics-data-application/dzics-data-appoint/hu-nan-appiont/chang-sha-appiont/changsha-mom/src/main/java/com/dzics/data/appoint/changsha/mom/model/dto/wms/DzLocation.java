package com.dzics.data.appoint.changsha.mom.model.dto.wms;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 机械手放货位置申请
 *
 * @author ZhangChengJun
 * Date 2021/12/6.
 * @since
 */
@Data
public class DzLocation {
    @ApiModelProperty("RFID信息")
    private String rfid;
    @ApiModelProperty("订单号")
    private String materialCode;
    @ApiModelProperty("下料点")
    private String station;
}
