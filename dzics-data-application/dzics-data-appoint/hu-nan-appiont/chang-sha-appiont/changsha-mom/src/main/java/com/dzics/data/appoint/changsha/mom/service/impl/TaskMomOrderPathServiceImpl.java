package com.dzics.data.appoint.changsha.mom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrderPath;
import com.dzics.data.appoint.changsha.mom.service.MomOrderPathService;
import com.dzics.data.appoint.changsha.mom.service.TaskMomOrderPathService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/5/27.
 * @since
 */
@Service
@Slf4j
public class TaskMomOrderPathServiceImpl implements TaskMomOrderPathService {
    @Autowired
    private MomOrderPathService momOrderPathService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public List<MomOrderPath> saveMomOrderPath(List<MomOrderPath> momOrderPath) {
        boolean save = momOrderPathService.saveBatch(momOrderPath);
        return momOrderPath;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void delByOrderId(String orderId) {
        momOrderPathService.remove(new QueryWrapper<MomOrderPath>().eq("mom_order_id",orderId));
    }

    @Override
    public List<MomOrderPath> getListByOrderId(String orderId) {
        return momOrderPathService.list(new QueryWrapper<MomOrderPath>().eq("mom_order_id",orderId));
    }
}
