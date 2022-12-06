package com.dzics.data.pub.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.db.dao.DzOrderDao;
import com.dzics.data.pub.model.entity.DzOrder;
import com.dzics.data.pub.model.vo.Orders;
import com.dzics.data.pub.service.DzOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Service
public class DzOrderServiceImpl extends ServiceImpl<DzOrderDao, DzOrder> implements DzOrderService {
    @Autowired
    DzOrderDao dzOrderMapper;

    @Override
    public Result selOrdersDepart(String departId, String sub) {
        List<Orders> orders = dzOrderMapper.selOrdersDepart(departId);
        return Result.OK(orders);
    }

    @Override
    public Result setlOrders(String departId, String sub) {
        List<Orders> orders = dzOrderMapper.selOrders(departId);
        return Result.ok(orders, Long.valueOf(orders.size()));
    }

    @Override
    public List<String> getAllOrderNos() {
        List<String>ods=new ArrayList<>();
        List<DzOrder> dzOrders = dzOrderMapper.selectList(new QueryWrapper<>());
        if(CollectionUtils.isEmpty(dzOrders)){
            return ods;
        }
        ods=dzOrders.stream().map(s->s.getOrderNo()).collect(Collectors.toList());
        return ods;
    }
}
