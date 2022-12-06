package com.dzics.data.appoint.changsha.mom.model.vo;

import com.dzics.data.common.base.model.page.PageLimit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * MOM通讯日志
 * </p>
 *
 * @author van
 * @since 2022-08-19
 */
@Data
public class MomCommunicationLogVo extends PageLimit {


    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "通讯类型（1：mom下发，2：请求mom)")
    private String type;

    @ApiModelProperty(value = "业务类型（101：叫料，102：叫空框，103：满料拖出，104：空框拖出，105：报工，106：查询料框信息")
    private String businessType;

    @ApiModelProperty(value = "处理结果（0：成功，1：失败）")
    private String resultState;

    @ApiModelProperty(value = "请求内容")
    private String requestContent;

    @ApiModelProperty(value = "响应内容")
    private String responseContent;
}
