package com.dzics.data.pub.service.kanban;

/**
 * @author Administrator
 */


import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.vo.Result;

/**
 * 产能分析 （达成率、合格率、生产节拍、按当日班次计算）
 *
 * @author Administrator*/
public interface PlanShiftService {
    /**
     * 达成率、合格率、生产节拍
     * */
    Result getWorkShiftCapacity(GetOrderNoLineNo orderNoLineNo) throws Exception;
}
