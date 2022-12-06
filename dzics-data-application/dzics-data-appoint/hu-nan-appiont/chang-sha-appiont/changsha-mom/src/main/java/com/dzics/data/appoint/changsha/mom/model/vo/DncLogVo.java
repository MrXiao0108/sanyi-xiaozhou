package com.dzics.data.appoint.changsha.mom.model.vo;

import com.dzics.data.common.base.model.page.PageLimitBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xnb
 * @date 2022/11/11 0011 16:16
 */
@Data
public class DncLogVo extends PageLimitBase {
    @ApiModelProperty(value = "设备编码")
    private String equipmentCode;

    @ApiModelProperty(value = "主程序名")
    private String programName;

    @ApiModelProperty(value = "DNC响应信息")
    private String dncResponse;

    @ApiModelProperty(value = "处理结果")
    private String state;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
