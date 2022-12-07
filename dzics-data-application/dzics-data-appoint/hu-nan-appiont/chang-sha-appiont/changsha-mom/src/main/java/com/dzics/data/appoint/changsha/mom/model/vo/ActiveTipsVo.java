package com.dzics.data.appoint.changsha.mom.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xnb
 * @date 2022/12/6 0006 14:47
 */
@Data
public class ActiveTipsVo {

    @ApiModelProperty(value = "主键ID")
    private String Id;

    @ApiModelProperty(value = "消息内容")
    private String Message;

    @ApiModelProperty(value = "时间")
    private String dataTime;

    @ApiModelProperty(value = "模块类型 1：维修、2：刀具寿命、3：设备保养、4:巡检")
    private String modelType;

}
