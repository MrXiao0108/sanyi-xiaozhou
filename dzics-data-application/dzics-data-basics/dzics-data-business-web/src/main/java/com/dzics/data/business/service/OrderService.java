package com.dzics.data.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.AddOrderVo;
import com.dzics.data.pub.model.dto.DepartParms;
import com.dzics.data.pub.model.dto.OrderParmsModel;
import com.dzics.data.pub.model.entity.DzOrder;
import com.dzics.data.pub.model.vo.DzOrderDo;
import com.dzics.data.pub.model.vo.SelOrders;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

public interface OrderService{
    Result<DzOrder> add(String sub, AddOrderVo data);


    Result del(String sub, Long id);

    Result<List<DzOrderDo>> list(String sub, OrderParmsModel orderParmsModel);

    /**
     * 根据订单号查询订单
     *
     * @param orderNo
     * @return
     */
    DzOrder selOrderNo(String orderNo);

    Result put(String sub, AddOrderVo data);

    /**
     * 所有订单
     *
     * @param selOrders
     * @param sub
     * @return
     */
    Result setlOrders(SelOrders selOrders, String sub);


    /**
     * 根据站点获取站点下的订单
     * @param departParms
     * @param sub
     * @return
     */
    Result selOrdersDepart(DepartParms departParms, String sub);

    /**
     * 根据订单号和产线号查询产线    清除缓存
     */
    @CacheEvict(cacheNames={"dzDetectionTemplCache.getLineIdByOrderNoLineNo"}, allEntries = true)
    void deleteLineIdByOrderNoLineNo();
}
