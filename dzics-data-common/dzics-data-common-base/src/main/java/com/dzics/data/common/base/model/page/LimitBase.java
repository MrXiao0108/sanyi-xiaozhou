package com.dzics.data.common.base.model.page;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname LimitBase
 * @Description 描述
 * @Date 2022/4/26 15:31
 * @Created by NeverEnd
 */
@Data
public class LimitBase {
    @ApiModelProperty("当前页")
    private int page = 1;
    @ApiModelProperty("每页查询条数")
    private int limit = 10;
}
