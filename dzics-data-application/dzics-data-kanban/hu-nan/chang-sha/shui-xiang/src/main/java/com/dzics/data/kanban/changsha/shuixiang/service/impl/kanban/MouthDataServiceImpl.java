package com.dzics.data.kanban.changsha.shuixiang.service.impl.kanban;


import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.model.vo.kanban.GetMonthlyCapacityListDo;
import com.dzics.data.common.base.model.vo.kanban.MonthData;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.pdm.db.dao.DzEquipmentProNumSignalDao;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.service.DzProductionLineService;
import com.dzics.data.pub.service.SysConfigService;
import com.dzics.data.pub.service.kanban.MouthDataService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计每个月产量 接口实现
 */
@Service
@Slf4j
public class MouthDataServiceImpl implements MouthDataService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzProductionLineService lineService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    public DzEquipmentProNumSignalDao signalDao;
    /**
     * 产线月生产 合格/不合格 数量
     *
     * @param noLineNo
     * @return
     */
    @Override
    public Result getMonthData(GetOrderNoLineNo noLineNo) {
        String key =noLineNo.getProjectModule()+ RedisKey.MONTH_DATA_OK_NG + noLineNo.getOrderNo() + noLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Object o = redisUtil.get(key);
                return (Result) o;
            }
            DzProductionLine dzProductionLine = lineService.getLineIdByOrderNoLineNo(noLineNo);
            if (dzProductionLine == null) {
                log.warn("以月为单位，查询本年生产数据失败，产线不存在。订单:{}， 产线序号:{}", noLineNo.getOrderNo(), noLineNo.getLineNo());
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            if (dzProductionLine.getStatisticsEquimentId() == null) {
                log.warn("产线未绑定记数设备,产线id:{}", dzProductionLine.getId());
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            //获取产线产量记数设备id
            String eqId = dzProductionLine.getStatisticsEquimentId();
            int year = LocalDate.now().getYear();
            List<Map<String, Object>> data = signalDao.getMonthData(year, noLineNo.getOrderNo(), eqId);
            List<MonthData> monthData = new ArrayList<>();
            List<String> allMonth = DateUtil.getAllMonth();
            for (String str : allMonth) {
                MonthData month = new MonthData();
                month.setMonth(str);
                month.setQualified(0L);
                month.setRejects(0L);
                for (Map<String, Object> map : data) {
                    String workMouth = map.get("workMouth").toString();
                    if (workMouth.equals(str)) {
                        month.setQualified(Long.valueOf(map.get("qualified").toString()));
                        month.setRejects(Long.valueOf(map.get("rejects").toString()));
                        break;
                    }
                }
                monthData.add(month);
            }
            List<Long> ok = monthData.stream().map(p -> p.getQualified()).collect(Collectors.toList());
            List<Long> no = monthData.stream().map(p -> p.getRejects()).collect(Collectors.toList());
            GetMonthlyCapacityListDo getMonthlyCapacityListDo = new GetMonthlyCapacityListDo();
            getMonthlyCapacityListDo.setQualifiedList(ok);
            getMonthlyCapacityListDo.setBadnessList(no);
            Result res = Result.ok(getMonthlyCapacityListDo);
            redisUtil.set(key, res, noLineNo.getCacheTime());
            return res;
        } catch (Exception e) {
            log.error("查询产线当年(以月为单位)生产(合格/不合格)数量异常:{}", e.getMessage(),e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }
}
