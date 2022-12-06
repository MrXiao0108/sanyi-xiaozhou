package com.dzics.data.pub.service.kanban;


import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.vo.Result;

/**
 * 统计日产数量
 */
public interface DayDataService {

    /**
     * 订单产线号查询产线当日生产 合格/不合格数量，当日班次生产合格，不合格
     * 日产数据(1月)
     * @return
     */
    Result getDailyOutput(GetOrderNoLineNo getOrderNoLineNo);
}
