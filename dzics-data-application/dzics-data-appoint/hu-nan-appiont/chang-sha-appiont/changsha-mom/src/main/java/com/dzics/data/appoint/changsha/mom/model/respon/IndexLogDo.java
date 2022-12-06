package com.dzics.data.appoint.changsha.mom.model.respon;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xnb
 * @date 2022/11/14 0014 10:25
 */
@Data
public class IndexLogDo {
    @ApiModelProperty(value = "其他内部日志")
    private DzicsInsidDo dzicsInsidDo;

    @ApiModelProperty(value = "Mom通讯日志")
    private MomTcpDo momTcpDo;
}
