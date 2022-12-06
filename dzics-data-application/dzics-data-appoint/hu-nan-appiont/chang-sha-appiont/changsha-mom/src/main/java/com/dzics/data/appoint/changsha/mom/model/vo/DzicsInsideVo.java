package com.dzics.data.appoint.changsha.mom.model.vo;

import com.dzics.data.common.base.model.page.PageLimitBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xnb
 * @date 2022/11/12 0012 15:32
 */
@Data
public class DzicsInsideVo extends PageLimitBase {

    @ApiModelProperty(value = "业务类型(103：满料拖出、17：Mom订单工序下发、4：生产叫料、1：Mom订单下发)")
    private String businessType;

    @ApiModelProperty(value = "处理结果")
    private String state;

    @ApiModelProperty(value = "异常信息")
    private String throwMsg;

    @ApiModelProperty(value = "开始时间")
    private String beginTime;
    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
