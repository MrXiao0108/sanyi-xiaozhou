package com.dzics.data.appoint.changsha.mom.task;

import com.dzics.data.appoint.changsha.mom.db.dao.MomOrderDao;
import com.dzics.data.appoint.changsha.mom.model.constant.MomConstant;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.appoint.changsha.mom.service.CachingApi;
import com.dzics.data.appoint.changsha.mom.service.MomOrderService;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Classname MomTask
 * @Description MOM订单任务处理
 * @Date 2022/4/28 9:42
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class MomTask {
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private MomOrderDao momOrderDao;
    @Autowired
    private CachingApi cachingApi;

    /**
     * 监控MOM 订单 如果符合条件 修改订单状态 开始订单
     * 每10秒执行一次
     */
    @Transactional(rollbackFor = Exception.class)
    /*@Scheduled(initialDelay = 10000, fixedDelay = 17000)*/
    public void momOrderMonitor() {
        log.info("MOM订单任务处理");
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        int count = momOrderDao.countStart(line.getOrderId(), line.getId());
        if (count > 0) {
            return;
        }
        PageHelper.startPage(1, 1);
        List<MomOrder> momOrders = momOrderDao.getStateOrderByStartTime(line.getOrderId(), line.getId());
        PageInfo<MomOrder> info = new PageInfo<>(momOrders);
        if (info.getTotal() > 0) {
            MomOrder momOrder = info.getList().get(0);
            momOrderService.orderStart(null,momOrder,null,null);
        }
    }
}
