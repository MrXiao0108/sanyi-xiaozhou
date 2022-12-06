package com.dzics.data.kanban.changsha.xiaozhou.jingjia.service.impl.kanban;

import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.model.vo.kanban.PlanAnalysisDoSingle;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.db.dao.DzProductionPlanDayDao;
import com.dzics.data.pdm.model.entity.DzProductionPlanDay;
import com.dzics.data.pdm.service.DzEquipmentProNumSignalService;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.service.DzProductionLineService;
import com.dzics.data.pub.service.SysConfigService;
import com.dzics.data.pub.service.kanban.PlanAnalysisService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    @Autowired
    private TaktTimeServiceImpl taktTimeService;
    @Autowired
    private DzEquipmentProNumSignalService proNumSignalService;

    /**
     * 产线单日 达成率
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
            DzProductionPlanDay playDay = planDayDao.getPlayDay(line.getId(), LocalDate.now());
            PlanAnalysisDoSingle planAnalysisDo = new PlanAnalysisDoSingle();
            //达成率 = 已完工数量 / 计划生产数量 * 100
            if(playDay.getPlannedQuantity()==null || 0==playDay.getPlannedQuantity()){
                planAnalysisDo.setPercentageComplete(BigDecimal.valueOf(0));
            }else{
                double dcl = playDay.getCompletedQuantity().doubleValue() / playDay.getPlannedQuantity().doubleValue() * 100;
                planAnalysisDo.setPercentageComplete(BigDecimal.valueOf(dcl));
            }
            //生产节拍
            planAnalysisDo.setTaktTime(taktTimeService.getTaktTime(noLineNo));
            //合格率
            planAnalysisDo.setPassRate(proNumSignalService.getSumQuaNum(noLineNo,line.getStatisticsEquimentId(), line.getOrderNo()));
            Result<PlanAnalysisDoSingle> ok = Result.ok(planAnalysisDo);
            redisUtil.set(key, ok, noLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("获取产线达成率错误:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }
}
