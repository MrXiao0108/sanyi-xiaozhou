package com.dzics.data.kanban.changsha.shuixiang.service.impl.kanban;

import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.model.vo.kanban.GetOutputByLineIdDo;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.DateRangeUtil;
import com.dzics.data.pdm.db.dao.DzEquipmentProNumDao;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.vo.RunDataModel;
import com.dzics.data.pub.service.DzProductionLineService;
import com.dzics.data.pub.service.SysConfigService;
import com.dzics.data.pub.service.kanban.YieldAnalysisService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 多少日内看板产量分析 接口实现
 */
@Service
@Slf4j
public class YieldAnalysisServiceImpl implements YieldAnalysisService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzProductionLineService lineService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    public DzEquipmentProNumDao proNumDao;

    /**
     * 根据订单产线号获取绑定设备的五日内产量
     *
     * @param noLineNo
     * @return
     */
    @Override
    public Result getOutputByLineId(GetOrderNoLineNo noLineNo) {
        String orderNo = noLineNo.getOrderNo();
        String key =noLineNo.getProjectModule()+ RedisKey.HOW_MUCH_DAILY_OUTPUT + orderNo+ noLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Object o = redisUtil.get(key);
                return (Result) o;
            }
            DzProductionLine lineIdByOrderNoLineNo = lineService.getLineIdByOrderNoLineNo(noLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            String tableKey = ((RunDataModel) sysConfigService.systemRunModel(null).getData()).getTableName();
            DzProductionLine dzProductionLine = lineService.getById(lineIdByOrderNoLineNo.getId());
            GetOutputByLineIdDo getOutputByLineIdDo = new GetOutputByLineIdDo();
            List<String> fiveDate = DateRangeUtil.getRecentDaysYMD(5);
            getOutputByLineIdDo.setDateList(DateRangeUtil.getRecentDaysMD(5));
            if (dzProductionLine != null && dzProductionLine.getStatisticsEquimentId() != null) {
                //获取产线产量记数设备id
                String eqId = dzProductionLine.getStatisticsEquimentId();
                //查询五日内生产数据
                List<Long> longs = new ArrayList<>();
                for (String localDate : fiveDate) {
                    Long data = proNumDao.getOutputByEqId(eqId, tableKey, localDate, orderNo);
                    longs.add(data);
                }
                getOutputByLineIdDo.setList(longs);
            } else {
                log.warn("查询产线绑定设备五日数据异常，产线id：{}", lineIdByOrderNoLineNo.getId());
                if (dzProductionLine != null) {
                    log.warn("查询产线绑定设备五日数据异常，产线绑定设备id：{}", dzProductionLine.getStatisticsEquimentId());
                }
                Long[] list = {0L, 0L, 0L, 0L, 0L};
                List<Long> longs = Arrays.asList(list);
                getOutputByLineIdDo.setList(longs);
            }
            Result ok = Result.ok(getOutputByLineIdDo);
            redisUtil.set(key, ok, noLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("根据产线获取绑定设备的五日内产量异常:{}", e.getMessage(),e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }
}
