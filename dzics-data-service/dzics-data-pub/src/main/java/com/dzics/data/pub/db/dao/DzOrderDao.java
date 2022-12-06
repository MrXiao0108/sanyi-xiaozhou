package com.dzics.data.pub.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.dzics.data.pub.model.entity.DzOrder;
import com.dzics.data.pub.model.vo.DzOrderDo;
import com.dzics.data.pub.model.vo.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Mapper
@Repository
public interface DzOrderDao extends BaseMapper<DzOrder> {

    List<DzOrderDo> listOrder(@Param("orgCode") String orgCode,
                              @Param("orderNo") String orderNo,
                              @Param("departId") String departId,
                              @Param("field") String field,
                              @Param("type") String type);

    List<Orders> selOrders(@Param("departId") String departId);


    List<Orders> selOrdersDepart(@Param("departId") String departId);

}
