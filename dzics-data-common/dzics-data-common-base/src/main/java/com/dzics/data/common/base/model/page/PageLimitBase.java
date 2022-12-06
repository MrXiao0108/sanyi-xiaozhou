package com.dzics.data.common.base.model.page;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @param
 * @author zhangchengjun
 */
@Data
public class PageLimitBase extends LimitBase {
    @ApiModelProperty("排序字段")
    private String field;
    @ApiModelProperty("ASC OR DESC OR 空字符串")
    private String type;
}
