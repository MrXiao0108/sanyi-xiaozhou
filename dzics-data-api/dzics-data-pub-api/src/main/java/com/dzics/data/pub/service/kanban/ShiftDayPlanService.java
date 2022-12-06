package com.dzics.data.pub.service.kanban;

import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.vo.Result;

/**
 * @Classname ShiftDayPlanService
 * @Description 日生产计划，以及班次生产详情
 * @Date 2022/4/21 11:39
 * @Created by NeverEnd
 */
public interface ShiftDayPlanService {
    /**
     * 查询日生产计划
     * @param orderNoLineNo
     * @return
     */
    Result getShiftProductionDetails(GetOrderNoLineNo orderNoLineNo);
}
