package com.dzics.data.pub.service.kanban;

import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.vo.Result;

/**
 * @Classname DayPassRate
 * @Description 当日工件合格率
 * @Date 2022/4/21 13:18
 * @Created by NeverEnd
 */
public interface DayPassRate {
    /**
     * 当日生产合格率
     */
    Result dailyPassRate(GetOrderNoLineNo orderNoLineNo);
}
