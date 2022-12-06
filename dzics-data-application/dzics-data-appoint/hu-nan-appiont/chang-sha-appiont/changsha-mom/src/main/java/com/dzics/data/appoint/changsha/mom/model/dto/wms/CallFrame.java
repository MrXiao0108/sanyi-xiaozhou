package com.dzics.data.appoint.changsha.mom.model.dto.wms;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/12/6.
 * @since
 */
@Data
public class CallFrame extends DzCallFrame {

    @ApiModelProperty("RFID信息")
    private String rfid;

}
