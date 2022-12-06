package com.dzics.data.appoint.changsha.mom.model.vo.wms;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/12/6.
 * @since
 */
@Data
public class LocationResp extends WmsRespone {
    @ApiModelProperty("是否允许放料")
    private Boolean allowed;
}
