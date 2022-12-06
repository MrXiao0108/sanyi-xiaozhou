package com.dzics.data.appoint.changsha.mom.service;

import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.vo.OrderIdLineId;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

public interface CachingApi {
    @Cacheable(cacheNames = "cachingApi.getOrderIdAndLineId", key = "'runOrder'", unless = "#result == null")
    DzProductionLine getOrderIdAndLineId();

    @CachePut(cacheNames = "cachingApi.getOrderIdAndLineId", key = "'runOrder'", unless = "#result == null")
    DzProductionLine updateOrderIdAndLineId();

    /**
     * 根据订单序号和产线序号获取 订单和产线id
     *
     * @param orderCode
     * @param lineNo
     * @return
     */
    @Cacheable(cacheNames = "cachingApi.getOrderNoLineNoId", key = "#orderCode+#lineNo", unless = "#result == null")
    OrderIdLineId getOrderNoLineNoId(String orderCode, String lineNo);

    /**
     * 查询当前产线Agv呼叫模式
     * @return String
     * */
    @Cacheable(cacheNames = "cachingApi.getMomRunModel", key = "'runMomModel'", unless = "#result == null")
    String getMomRunModel();

    /**
     * 修改Agv呼叫模式
     * @return String
     * */
    @CachePut(cacheNames = "cachingApi.getMomRunModel", key = "'runMomModel'", unless = "#result == null")
    String updateAgvRunModel(Integer rm,DzProductionLine line);
}
