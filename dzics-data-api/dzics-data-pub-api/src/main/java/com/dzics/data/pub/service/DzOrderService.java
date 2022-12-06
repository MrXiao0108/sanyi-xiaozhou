package com.dzics.data.pub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.entity.DzOrder;

import java.util.List;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
public interface DzOrderService extends IService<DzOrder> {
    /**
     * 根据站点获取站点下的订单
     * @param departId
     * @param sub
     * @return
     */
    Result selOrdersDepart(String departId, String sub);

    /**
     * 所有订单
     *
     * @param departId
     * @param sub
     * @return
     */
    Result setlOrders(String departId, String sub);

    /**
     * 获取当前站点下所有订单编号
     * */
    List<String>getAllOrderNos();
}
