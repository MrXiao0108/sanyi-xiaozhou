package com.dzics.data.pub.service.kanban;

import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.vo.Result;

/**
 * 多少日内看板产量分析 接口定义
 */
public interface YieldAnalysisService {
    /**
     * 根据订单产线号获取绑定设备的五日内产量
     *
     * @return
     *
     */

    Result getOutputByLineId(GetOrderNoLineNo getOrderNoLineNo);
}
