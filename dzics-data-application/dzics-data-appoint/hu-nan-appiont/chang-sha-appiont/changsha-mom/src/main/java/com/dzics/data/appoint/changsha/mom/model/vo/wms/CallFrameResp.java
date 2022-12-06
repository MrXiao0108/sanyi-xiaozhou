package com.dzics.data.appoint.changsha.mom.model.vo.wms;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 呼叫料框 信息返回
 * @author ZhangChengJun
 * Date 2021/12/6.
 * @since
 */
@Data
public class CallFrameResp extends WmsRespone {
    @ApiModelProperty("下料点")
    private String station;
}
