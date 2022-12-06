package com.dzics.common.web.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author zhangchengjun
 */
@Data
public class PageBase<T> {

    @ApiModelProperty("当前页")
    private int page = 1;

    @ApiModelProperty("每页查询条数")
    private int limit = 10;

    @ApiModelProperty("排序字段")
    private String field;

    @ApiModelProperty("ASC OR DESC OR 空字符串")
    private String type;

    @ApiModelProperty(value = "订单号", required = true)
    private String orderNo;

    @ApiModelProperty(value = "产线号", required = true)
    private String lineNo;

    @ApiModelProperty(value = "搜索条件")
    private T model;
}
