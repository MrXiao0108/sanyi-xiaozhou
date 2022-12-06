package com.dzics.data.appoint.changsha.mom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.appoint.changsha.mom.service.CachingApi;
import com.dzics.data.appoint.changsha.mom.service.MomOrderService;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.vo.OrderIdLineId;
import com.dzics.data.pub.service.DzProductionLineService;
import com.dzics.data.pub.service.SysDictItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CachingApiImpl implements CachingApi {

    @Value("${order.code}")
    private String orderCode;

    @Value("${mom.run.model.key}")
    private String momRunModelKey;

    @Autowired
    private DzProductionLineService dzProductionLineService;

    @Autowired
    private SysDictItemService dictItemService;

    @Autowired
    private MomOrderService momOrderService;

    @Override
    public DzProductionLine getOrderIdAndLineId() {
        try {
            List<DzProductionLine> data = dzProductionLineService.list(new QueryWrapper<DzProductionLine>().eq("order_no", orderCode).orderByDesc("create_time"));
            if (data.size() == 0) {
                return null;
            }
            DzProductionLine obj = data.get(0);
            return obj;
        } catch (Exception e) {
            log.error("查询加工岛产线ID和订单ID异常:{}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public DzProductionLine updateOrderIdAndLineId() {
        try {
            List<DzProductionLine> data = dzProductionLineService.list(new QueryWrapper<DzProductionLine>().eq("order_no", orderCode).orderByDesc("create_time"));
            if (data.size() == 0) {
                return null;
            }
            DzProductionLine obj = data.get(0);
            return obj;
        } catch (Exception e) {
            log.error("查询加工岛产线ID和订单ID异常:{}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public OrderIdLineId getOrderNoLineNoId(String orderCode, String lineNo) {
        return dzProductionLineService.getOrderNoLineNoId(orderCode, lineNo);
    }

    @Override
    public String getMomRunModel() {
        String model = dictItemService.getMomRunModel(momRunModelKey, orderCode);
        return model;
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public String updateAgvRunModel(Integer rm,DzProductionLine line) {
        String s = dictItemService.updateAgvRunModel(momRunModelKey, orderCode, rm);
        return s;
    }

}
