package com.dzics.data.pub.service.kanban;

import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.vo.Result;

/**
 * 用时分析接口定义
 */
public interface TimeAnalysisService {
    /**
     * 根据订单产线号查询所有设备当日用时分析(旧)-不分时段
     */
    Result getTimeAnalysis(GetOrderNoLineNo getOrderNoLineNo);
}
