package com.dzics.data.business.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.business.service.EquipmentProNumService;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.vo.kanban.WorkShiftSum;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.pdm.db.dao.DzEquipmentProNumDao;
import com.dzics.data.pdm.db.model.dao.DayDataResultDo;
import com.dzics.data.common.base.model.dao.QualifiedAndOutputDo;
import com.dzics.data.pdm.model.vo.DayDataDo;
import com.dzics.data.pdm.model.vo.HomeWorkShiftData;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.vo.RunDataModel;
import com.dzics.data.pub.service.DzProductionLineService;
import com.dzics.data.pub.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class EquipmentProNumServiceImpl implements EquipmentProNumService {
    @Autowired
    private SysConfigService cacheService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private DzEquipmentProNumDao dzEquipmentProNumDao;
    @Autowired
    private DzProductionLineService productionLineService;

    @Override
    public Result getOutputAndQualified(String lineId) {
        QualifiedAndOutputDo qualifiedAndOutputDo = outputCapacity(lineId);
        return Result.ok(qualifiedAndOutputDo);
    }

    /**
     * 注：产出率=生产数量/毛坯
     *
     * @param lineId
     * @return
     */
    @Override
    public QualifiedAndOutputDo outputCapacity(String lineId) {
        DzProductionLine line = productionLineService.getLineId(lineId);
        String nowDate = DateUtil.getDate();
        QualifiedAndOutputDo res = dzEquipmentProNumDao.outputCapacity(lineId, nowDate, line.getOrderNo(), line.getStatisticsEquimentId());
        if (res.getRoughNum().intValue() == 0) {
            res.setOutput(new BigDecimal(0));
        } else {
            BigDecimal roughNum = new BigDecimal(res.getRoughNum());
            BigDecimal nowNum = new BigDecimal(res.getNowNum() * 100);
            BigDecimal output = nowNum.divide(roughNum, 2, BigDecimal.ROUND_HALF_UP);
            res.setOutput(output);//产出率
        }
        return res;

    }

    @Override
    public List<WorkShiftSum> getWorkShiftSum(String orderNo, String equimentId) {
        List<WorkShiftSum> workShiftSumList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int monthValue = now.getMonthValue();
        List<String> mouthDate = sysConfigService.getMouthDate(year, monthValue);
        String mouth = LocalDate.now().toString().substring(0, 7);
        List<Map<String, Object>> workShift = dzEquipmentProNumDao.getWorkShift(orderNo, equimentId, mouth);
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
            List<Object> longListOk = new ArrayList<>();
            List<Object> longListNg = new ArrayList<>();
            for (String s : mouthDate) {
                Map<String, Object> mapx = null;
                for (Map<String, Object> map : value) {
                    String string = map.get("workData").toString();
                    if (s.equals(string)) {
                        mapx = map;
                        break;
                    }
                }
                longListOk.add(mapx != null ? mapx.get("qualifiedNum") : 0);
                longListNg.add(mapx != null ? mapx.get("badnessNum") : 0);
            }
            WorkShiftSum shiftdataOK = new WorkShiftSum();
            shiftdataOK.setData(longListOk);
            shiftdataOK.setName(key + "OK");
            shiftdataOK.setStack(key);
            shiftdataOK.setMouthValue(String.valueOf(monthValue));
            workShiftSumList.add(shiftdataOK);
            WorkShiftSum shiftdataNG = new WorkShiftSum();
            shiftdataNG.setData(longListNg);
            shiftdataNG.setName(key + "NG");
            shiftdataNG.setStack(key);
            shiftdataNG.setMouthValue(String.valueOf(monthValue));
            workShiftSumList.add(shiftdataNG);
        }
        return workShiftSumList;
    }


    @Override
    public Result geDayAndMonthDataV2(String lineId) {
        DzProductionLine dzProductionLine = productionLineService.getLineId(lineId);
        if (dzProductionLine == null || dzProductionLine.getStatisticsEquimentId() == null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR17);
        }
        List<WorkShiftSum> dayWorkShiftSum = getWorkShiftSum(dzProductionLine.getOrderNo(), dzProductionLine.getStatisticsEquimentId());
        DayDataDo dayDataDo = monthData(dzProductionLine);
        HomeWorkShiftData homeWorkShiftData = new HomeWorkShiftData();
        homeWorkShiftData.setDayWorkShiftSum(dayWorkShiftSum);
        homeWorkShiftData.setMouthWorkShiftSum(dayDataDo);
        if (CollectionUtils.isEmpty(dayWorkShiftSum) || dayWorkShiftSum.size() == 0) {
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH) + 1;
            homeWorkShiftData.setMouthValue(String.valueOf(month));
        }
        if (CollectionUtils.isNotEmpty(dayWorkShiftSum)) {
            homeWorkShiftData.setMouthValue(dayWorkShiftSum.get(0).getMouthValue());
        }
        return Result.ok(homeWorkShiftData);
    }

    @Override
    public DayDataDo monthData(DzProductionLine dzProductionLine) {
        //合格
        List<BigDecimal> qualified = new ArrayList<>();
        //不合格
        List<BigDecimal> badness = new ArrayList<>();
        String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getTableName();
        List<String> allMonth = DateUtil.getAllMonth();
        for (String month : allMonth) {
            //查询指定月份数据
            DayDataResultDo dayDataResultDo = dzEquipmentProNumDao.monthDataByLine(month, dzProductionLine.getOrderNo(), dzProductionLine.getStatisticsEquimentId());
            if (dayDataResultDo != null) {
                if (dayDataResultDo.getQualifiedNum() != null) {
                    qualified.add(dayDataResultDo.getQualifiedNum());
                } else {
                    qualified.add(new BigDecimal(0));
                }
                if (dayDataResultDo.getBadnessNum() != null) {
                    badness.add(dayDataResultDo.getBadnessNum());
                } else {
                    badness.add(new BigDecimal(0));
                }
            } else {
                log.error("查询月生产数据异常:{}，表:{}", month, tableKey);
            }
        }
        DayDataDo dayDataDo = new DayDataDo();
        dayDataDo.setQualifiedNum(qualified);
        dayDataDo.setBadnessNum(badness);
        return dayDataDo;
    }
}
