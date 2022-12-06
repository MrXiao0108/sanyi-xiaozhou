package com.dzics.data.appoint.changsha.mom.model.vo.wms;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/12/6.
 * @since
 */
@Data
public class WmsRespone {

    @ApiModelProperty("接收状态")
    private Boolean status;

    @ApiModelProperty("结果描述")
    private String message;

}
