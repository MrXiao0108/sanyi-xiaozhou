package com.dzics.data.pub.service.kanban;

import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.vo.Result;

/**
 * @Classname DaySumAndTotalSumService
 * @Description 设备日产总产
 * @Date 2022/2/14 12:33
 * @Created by NeverEnd
 */
public interface DaySumAndTotalSumService {
    /**
     * 根据订单和产线序号查询该产线下（所有机器人和机床）的（日产、总产）
     *
     * @param getOrderNoLineNo 订单和产线序号
     * @return 日产、总产
     */
    Result getDeviceProductionQuantity(GetOrderNoLineNo getOrderNoLineNo);
}
