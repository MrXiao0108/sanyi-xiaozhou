package com.dzics.data.pub.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/9/28.
 * @since
 */
@Data
public class AddFaultRecordParmsInner {
    @ApiModelProperty("维修详情ID")
    private String repairDetailsId;
    @ApiModelProperty("故障位置")
    private String faultLocation;
    @ApiModelProperty("故障描述")
    private String faultDescription;
}
