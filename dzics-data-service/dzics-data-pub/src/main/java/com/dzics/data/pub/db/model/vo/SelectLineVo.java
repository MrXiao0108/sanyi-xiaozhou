package com.dzics.data.pub.db.model.vo;

import com.dzics.data.common.base.model.page.PageLimit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SelectLineVo extends PageLimit {
    private String id;
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ApiModelProperty("站点名称")
    private String departName;
    @ApiModelProperty("产线名称")
    private String lineName;
    @ApiModelProperty("机构编码(查询忽略此项)")
    private String useOrgCode;
    @ApiModelProperty("产线类型")
    private String lineType;
}
