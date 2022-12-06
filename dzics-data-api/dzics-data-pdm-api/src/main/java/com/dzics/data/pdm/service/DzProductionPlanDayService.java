package com.dzics.data.pdm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.model.dto.GetOneDayPlanDayDto;
import com.dzics.data.pdm.model.entity.DzProductionPlanDay;
import com.dzics.data.pdm.model.entity.PlanDayLineNo;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 日计划产量统计生产率 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-19
 */
public interface DzProductionPlanDayService extends IService<DzProductionPlanDay> {

    /**
     * 生成每日计划基础数据
     */
    @Transactional(rollbackFor = Throwable.class)
    void datRunMeth(LocalDate now);


    /**
     * 更新生产率
     */
    void updateCompletionRate(LocalDate localDate,List<PlanDayLineNo> planDayLineNos);

    Result<List<GetOneDayPlanDayDto>> getList(PageLimitBase pageLimitBase);

    /**
     * 获取产线日生产计划表
     * */
    DzProductionPlanDay getPlayDay(String lineId,LocalDate localDate);

}
