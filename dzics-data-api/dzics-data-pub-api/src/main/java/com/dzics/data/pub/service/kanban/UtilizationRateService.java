package com.dzics.data.pub.service.kanban;

import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.vo.Result;

/**
 * @Classname UtilizationRateService
 * @Description 设备稼动率接口
 * @Date 2022/3/15 10:19
 * @Created by NeverEnd
 */
public interface UtilizationRateService {

    /**
     * 获取订单产线下设备的稼动信息
     *
     * @param orderNoLineNo
     * @return
     */
    Result getSocketUtilization(GetOrderNoLineNo orderNoLineNo);
}
