package com.dzics.data.pub.service.kanban;

import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.vo.Result;

/**
 * 产线计划分析
 * @author Administrator
 */
public interface PlanAnalysisService {
    /**
     * 根据订单产线号查询近五日产线计划分析
     *
     * 产线计划其实就是达成率
     *
     * @return
     */
    Result getPlanAnalysis(GetOrderNoLineNo getOrderNoLineNo) throws Exception;
}
