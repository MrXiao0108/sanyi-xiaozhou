package com.dzics.data.pdm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.pdm.db.dao.DzProductionPlanDao;
import com.dzics.data.pdm.model.entity.DzProductionPlan;
import com.dzics.data.pdm.service.DzProductionPlanService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产线日生产计划表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-19
 */
@Service
public class DzProductionPlanServiceImpl extends ServiceImpl<DzProductionPlanDao, DzProductionPlan> implements DzProductionPlanService {

    @Override
    public DzProductionPlan getLineId(String id) {
        return  getOne(new QueryWrapper<DzProductionPlan>().eq("line_id", id).eq("plan_type", 0));
    }
}
