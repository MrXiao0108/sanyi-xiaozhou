package com.dzics.data.pub.service.kanban;

import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.vo.Result;

/**
 * 产线稼动率
 */
public interface OperationRatioService {
    /**
     * 根据订单产线号查询近五日稼动率
     *
     * @return
     */
    Result getProductionPlanFiveDay(GetOrderNoLineNo getOrderNoLineNo);
}
