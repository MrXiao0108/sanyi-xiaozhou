package com.dzics.data.kanban.changsha.shuixiang.service.impl.kanban;

import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.model.vo.kanban.PlanAnalysisDo;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.DateRangeUtil;
import com.dzics.data.pdm.db.dao.DzProductionPlanDayDao;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.vo.RunDataModel;
import com.dzics.data.pub.service.DzProductionLineService;
import com.dzics.data.pub.service.SysConfigService;
import com.dzics.data.pub.service.kanban.PlanAnalysisService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 产线计划分析 接口实现
 */
@Service
@Slf4j
public class PlanAnalysisServiceImpl implements PlanAnalysisService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzProductionLineService lineService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    public DzProductionPlanDayDao planDayDao;

    /**
     * 根据订单产线号查询近五日产线计划分析
     *
     * @param noLineNo
     * @return
     */
    @Override
    public Result getPlanAnalysis(GetOrderNoLineNo noLineNo) {
        String key = noLineNo.getProjectModule() + RedisKey.PLAN_ANALYSIS + noLineNo.getOrderNo() + noLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Object o = redisUtil.get(key);
                return (Result) o;
            }

            DzProductionLine line = lineService.getLineIdByOrderNoLineNo(noLineNo);
            if (line == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            String planId = planDayDao.getPlanId(line.getId());
//        完成率
            List<BigDecimal> percentageComplete = new ArrayList<>();
//       产出率
            List<BigDecimal> outputRate = new ArrayList<>();
//        合格率
            List<BigDecimal> passRate = new ArrayList<>();
            String tableKey = ((RunDataModel) sysConfigService.systemRunModel(null).getData()).getPlanDay();
            LocalDate localDate = LocalDate.now().plusDays(-5);
            List<Map<String, BigDecimal>> list = planDayDao.getPlanAnalysis(planId, tableKey, localDate);
            for (Map<String, BigDecimal> data : list) {
                percentageComplete.add(data.get("percentageComplete"));
                outputRate.add(data.get("outputRate"));
                passRate.add(data.get("passRate"));
            }
            PlanAnalysisDo planAnalysisDo = new PlanAnalysisDo();
            planAnalysisDo.setPercentageComplete(percentageComplete);
            planAnalysisDo.setOutputRate(outputRate);
            planAnalysisDo.setPassRate(passRate);
            planAnalysisDo.setDateList(DateRangeUtil.getRecentDaysMD(5));
            Result ok = Result.ok(planAnalysisDo);
            redisUtil.set(key, ok, noLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("根据产线id查询近五日产线计划分析异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }
}
