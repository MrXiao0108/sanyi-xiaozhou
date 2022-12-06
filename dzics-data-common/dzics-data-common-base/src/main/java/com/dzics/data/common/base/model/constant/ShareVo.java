package com.dzics.data.common.base.model.constant;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Classname ShareVo
 * @Description 描述
 * @Date 2022/7/28 10:53
 * @Created by NeverEnd
 */
@Data
public class ShareVo {

    /**
     * 缓存前缀
     */
    @NotNull(message = "缓存前缀必填")
    private String cacheBaseName;
    /**
     * 订单号
     */
    @NotNull(message = "订单号必填")
    private String orderNo;
    /**
     * 产线号
     */
    @NotNull(message = "产线号")
    private String lineNo;
    /**
     * 类型 0 共享机床脉冲数据
     */
    private int type;

    /**
     * true 共享 false 不共享, 是否共享
     */
    @NotNull(message = "是否共享必填")
    private Boolean okShare;
}
