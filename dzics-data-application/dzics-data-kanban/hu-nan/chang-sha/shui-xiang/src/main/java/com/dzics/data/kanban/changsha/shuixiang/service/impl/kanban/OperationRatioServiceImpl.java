package com.dzics.data.kanban.changsha.shuixiang.service.impl.kanban;

import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.model.vo.kanban.ProductionPlanFiveDayDo;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.DateRangeUtil;
import com.dzics.data.pdm.db.dao.DzProductionPlanDayDao;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.vo.RunDataModel;
import com.dzics.data.pub.service.DzProductionLineService;
import com.dzics.data.pub.service.SysConfigService;
import com.dzics.data.pub.service.kanban.OperationRatioService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 产线稼动率 接口实现
 */
@Service
@Slf4j
public class OperationRatioServiceImpl implements OperationRatioService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzProductionLineService lineService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    public DzProductionPlanDayDao planDayDao;
    /**
     * 根据订单产线号查询近五日稼动率
     *
     * @param noLineNo
     * @return
     */
    @Override
    public Result getProductionPlanFiveDay(GetOrderNoLineNo noLineNo) {
        String key = noLineNo.getProjectModule()+ RedisKey.FIVE_DAY_CROP_MOVEMENT_RATE + noLineNo.getOrderNo() + noLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Object o = redisUtil.get(key);
                return (Result) o;
            }
            DzProductionLine line = lineService.getLineIdByOrderNoLineNo(noLineNo);
            if (line == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            String tableKey = ((RunDataModel) sysConfigService.systemRunModel(null).getData()).getPlanDay();
            String planId = planDayDao.getPlanId(line.getId());
            LocalDate localDate = LocalDate.now().plusDays(-5);
            List<BigDecimal> list = planDayDao.getProductionPlanFiveDay(planId, tableKey,localDate);
            ProductionPlanFiveDayDo res = new ProductionPlanFiveDayDo();
            res.setList(list);
            res.setDateList(DateRangeUtil.getRecentDaysMD(5));
            Result ok = Result.ok(list);
            redisUtil.set(key, ok, noLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("根据产线id查询近五日稼动率异常:{}", e.getMessage(),e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }


}
