package com.dzics.data.kanban.changsha.xiaozhou.cujia.impl.kanban;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.model.vo.kanban.WorkShiftSum;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.db.dao.DzEquipmentProNumDao;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.service.DzProductionLineService;
import com.dzics.data.pub.service.SysConfigService;
import com.dzics.data.pub.service.kanban.DayDataService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计日产数量
 */
@Service
@Slf4j
public class DayDataServiceImpl implements DayDataService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzProductionLineService lineService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    public DzEquipmentProNumDao proNumDao;

    /**
     * 订单产线号查询产线当日生产 合格/不合格数量，当日班次生产合格，不合格
     *
     * @param noLineNo
     * @return
     */
    @Override
    public Result getDailyOutput(GetOrderNoLineNo noLineNo) {
        String orderNo = noLineNo.getOrderNo();
        String key = noLineNo.getProjectModule() + RedisKey.DAY_WORK_SHIFT_NG_OK + orderNo + noLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Object o = redisUtil.get(key);
                return (Result) o;
            }
            DzProductionLine lineIdByOrderNoLineNo = lineService.getLineIdByOrderNoLineNo(noLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
            }
            List<WorkShiftSum> workShiftSum = getWorkShiftSum(lineIdByOrderNoLineNo.getStatisticsEquimentId(), orderNo);
            Result res = Result.ok(workShiftSum);
            redisUtil.set(key, res, noLineNo.getCacheTime());
            return res;
        } catch (Exception e) {
            log.error("查询产线月日生产(白班/晚班)数量异常:{}", e.getMessage(),e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }

    private List<WorkShiftSum> getWorkShiftSum(String statisticsEquimentId, String orderNo) {
        List<WorkShiftSum> workShiftSumList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int monthValue = now.getMonthValue();
        List<String> mouthDate = sysConfigService.getMouthDate(year, monthValue);
        String mouth = now.toString().substring(0, 7);
        List<Map<String, Object>> workShift = proNumDao.getWorkShiftNowNum(orderNo, statisticsEquimentId, mouth);
        Map<String, List<Map<String, Object>>> shift = new HashMap<>();
        for (Map<String, Object> map : workShift) {
            String workName = map.get("workName").toString();
            List<Map<String, Object>> list = shift.get(workName);
            if (CollectionUtils.isNotEmpty(list)) {
                list.add(map);
            } else {
                list = new ArrayList<>();
                list.add(map);
                shift.put(workName, list);
            }
        }
        for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : shift.entrySet()) {
            List<Map<String, Object>> value = stringListEntry.getValue();
            String key = stringListEntry.getKey();
            List<Object> longListNow = new ArrayList<>();
            for (String s : mouthDate) {
                Map<String, Object> mapx = null;
                for (Map<String, Object> map : value) {
                    String string = map.get("workData").toString();
                    if (s.equals(string)) {
                        mapx = map;
                        break;
                    }
                }
                longListNow.add(mapx != null ? mapx.get("nowNum") : 0);
            }
            WorkShiftSum shiftdataOK = new WorkShiftSum();
            shiftdataOK.setData(longListNow);
            shiftdataOK.setName(key);
            shiftdataOK.setStack(key);
            workShiftSumList.add(shiftdataOK);
        }
        return workShiftSumList;
    }
}
