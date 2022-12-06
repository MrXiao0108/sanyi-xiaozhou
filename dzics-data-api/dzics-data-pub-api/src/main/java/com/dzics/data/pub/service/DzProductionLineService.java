package com.dzics.data.pub.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.BingEquipmentVo;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.vo.OrderIdLineId;
import com.dzics.data.pub.model.vo.SelOrders;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * <p>
 * 产线表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
public interface DzProductionLineService extends IService<DzProductionLine> {
    @Cacheable(cacheNames = "cacheService.getOrderNoLineNoId", key = "#orderCode+#lineNo", unless = "#result == null")
    OrderIdLineId getOrderNoLineNoId(String orderCode, String lineNo);
    /**
     * 根据订单号和产线序号查询产线对象
     * @return
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.getLineIdByOrderNoLineNo", key = "#getOrderNoLineNo.orderNo+#getOrderNoLineNo.lineNo", unless = "#result == null")
    DzProductionLine getLineIdByOrderNoLineNo(GetOrderNoLineNo getOrderNoLineNo);
    /**
     * @param productionLineNumber 产线序号
     * @return
     */
    String getOnelineNo(String productionLineNumber);

    OrderIdLineId getOrderNoAndLineNo(String orderCode, String lineNo);



    Result putStatus(String sub, Long id);

    /**
     * 绑定产线统计产量的设备
     * @param sub
     * @return
     */
    Result bingEquipment(String sub, BingEquipmentVo bingEquipmentVo);

    /**
     * 查询所有产线
     * @param useOrgCode
     * @return
     */
    Result allLineList(String useOrgCode);


    Result getByOrderIdV2(String sub, Long valueOf);

    /**
     * 根据产线ID获取产线信息
     * @param lineId 产线ID
     * @return
     */
    DzProductionLine getLineId(String lineId);

    /**
     * 根据订单号和产线号查询产线    清除缓存
     */
    @CacheEvict(cacheNames={"dzDetectionTemplCache.getLineIdByOrderNoLineNo"}, allEntries = true)
    void deleteLineIdByOrderNoLineNo();

    /**
     * 根据订单查询所有产线
     * @param selOrders
     * @param useOrgCode
     * @return
     */
    Result selLines(SelOrders selOrders, String useOrgCode);
}
