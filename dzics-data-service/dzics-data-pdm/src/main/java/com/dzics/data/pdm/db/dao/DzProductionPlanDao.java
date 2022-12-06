package com.dzics.data.pdm.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pdm.db.model.dto.SelectProductionPlanVo;
import com.dzics.data.pdm.db.model.vo.ProductionPlanDo;
import com.dzics.data.pdm.model.dto.GetOneDayPlanDto;
import com.dzics.data.pdm.model.entity.DzProductionPlan;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 产线日生产计划表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-19
 */
@Mapper
public interface DzProductionPlanDao extends BaseMapper<DzProductionPlan> {

    List<ProductionPlanDo> list(SelectProductionPlanVo selectProductionPlanVo);
    long list_COUNT(SelectProductionPlanVo selectProductionPlanVo);


    /**
     * 获取日计划产量
     * */
    List<GetOneDayPlanDto>getOneDayPlan();
}
