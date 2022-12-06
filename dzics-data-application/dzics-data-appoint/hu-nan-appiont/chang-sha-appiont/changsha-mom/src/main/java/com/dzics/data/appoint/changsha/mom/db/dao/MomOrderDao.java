package com.dzics.data.appoint.changsha.mom.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.appoint.changsha.mom.model.constant.MomConstant;
import com.dzics.data.appoint.changsha.mom.model.dao.MomOrderDo;
import com.dzics.data.appoint.changsha.mom.model.dto.SearchOrderParms;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * mom下发订单表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
@Mapper
@Repository
public interface MomOrderDao extends BaseMapper<MomOrder> {

    /**
     * 统计生产中订单数量
     *
     * @param orderId 订单Id
     * @param id      产线ID
     * @return 订单数量
     */
    default int countStart(String orderId, String id) {
        QueryWrapper<MomOrder> wp = new QueryWrapper<>();
        wp.eq("order_id", orderId);
        wp.eq("line_id", id);
        wp.eq("ProgressStatus", MomConstant.ORDER_STATUS_START);
        Integer integer = selectCount(wp);
        return integer == null ? 0 : integer;
    }

    /**
     * 获取生产中的订单
     *
     * @param orderId 订单Id
     * @param id      产线ID
     * @return 生产中的订单
     */
    default MomOrder getOrderPro(String orderId, String id) {
        QueryWrapper<MomOrder> wp = new QueryWrapper<>();
        wp.eq("order_id", orderId);
        wp.eq("line_id", id);
        wp.eq("ProgressStatus", MomConstant.ORDER_STATUS_START);
        MomOrder order = selectOne(wp);
        return order;
    }

    /**
     * 订单列表 搜索
     *
     * @param orderPar
     * @return
     */
    List<MomOrderDo> getOrders(SearchOrderParms orderPar);

    /**
     * 根据计划时间正序排列
     *
     * @param orderId 订单Id
     * @param id      产线ID
     * @return 订单列表
     */
    default List<MomOrder> getStateOrderByStartTime(String orderId, String id) {
        QueryWrapper<MomOrder> wp = new QueryWrapper<>();
        wp.eq("order_id", orderId);
        wp.eq("line_id", id);
        wp.eq("ProgressStatus", MomConstant.ORDER_STATUS_LOWER);
//        计划开始时间 正序排列
        wp.orderByAsc("ScheduledStartDate");
        return selectList(wp);
    }

    /**
     * 查询未完工的一个订单（ScheduledStartDate 最早的）
     *
     * @return 查询结果
     * @author van
     * @date 2022/6/27
     */
    MomOrder getUncompletedOrder();

    /**
     * 修改订单状态
     *
     * @param wiporderno
     * @return true/false
     */
    boolean updateOrderState(String wiporderno);

    default String selProductNo(String materialCode, String orderStatusStart) {
        QueryWrapper<MomOrder> wp = new QueryWrapper<>();
        wp.eq("ProductNo", materialCode);
        wp.eq("ProgressStatus", orderStatusStart);
        List<MomOrder> momOrders = selectList(wp);
        if (CollectionUtils.isNotEmpty(momOrders)) {
            return momOrders.get(0).getWiporderno();
        }
        return null;
    }

    /**
     * 获取当前加工中心正在变更中的订单
     * @param orderId
     * @param lineId
     * @param workStation
     * @return MomOrder
     */
    MomOrder getWorkingOrder(@Param("orderId")String orderId,@Param("lineId") String lineId,@Param("workStation") String workStation);


    /**
     * 自动匹配查找，相同物料、已下发、无状态变更的Mom订单
     *
     * @param orderId 订单ID
     * @param lineId  产线ID
     * @param productNo 物料编码
     * @param down 订单状态110
     * @return MomOrder Mom订单
     */
    MomOrder getOrderCallMaterialStatus(@Param("orderId") String orderId, @Param("lineId") String lineId, @Param("productNo") String productNo, @Param("down") String down);
}
