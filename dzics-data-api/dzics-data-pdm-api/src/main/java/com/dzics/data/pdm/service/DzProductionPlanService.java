package com.dzics.data.pdm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.pdm.model.entity.DzProductionPlan;
import org.springframework.cache.annotation.Cacheable;

/**
 * <p>
 * 产线日生产计划表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-19
 */
public interface DzProductionPlanService extends IService<DzProductionPlan> {
    @Cacheable(cacheNames = "dzProductionPlanService.getLineId", key = "#id",unless = "#result == null")
    DzProductionPlan getLineId(String id);
}
