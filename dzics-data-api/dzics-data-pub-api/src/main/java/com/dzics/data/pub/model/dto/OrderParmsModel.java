package com.dzics.data.pub.model.dto;

import com.dzics.data.common.base.model.page.PageLimit;
import lombok.Data;

/**
 * 订单列表参数
 *
 * @author ZhangChengJun
 * Date 2021/7/5.
 * @since
 */
@Data
public class OrderParmsModel extends PageLimit {
    String orderNo;
}
