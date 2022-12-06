package com.dzics.data.pub.service.kanban;


import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.vo.Result;

/**
 * 统计每个月产量
 */
public interface MouthDataService {
    /**
     * 产线月生产 合格/不合格 数量
     *
     * @param orderNoLineNo
     * @return
     */
    Result getMonthData(GetOrderNoLineNo orderNoLineNo);
}
