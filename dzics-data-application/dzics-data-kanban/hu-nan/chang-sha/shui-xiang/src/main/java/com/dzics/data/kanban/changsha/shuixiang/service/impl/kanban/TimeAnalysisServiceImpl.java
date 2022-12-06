package com.dzics.data.kanban.changsha.shuixiang.service.impl.kanban;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.model.vo.kanban.EquipmentAvailableDo;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.pdm.service.DzEquipmentTimeAnalysisService;
import com.dzics.data.pub.db.dao.DzEquipmentDao;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.service.DzProductionLineService;
import com.dzics.data.pub.service.kanban.TimeAnalysisService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 用时分析接口实现
 */
@Service
@Slf4j
public class TimeAnalysisServiceImpl implements TimeAnalysisService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzProductionLineService lineService;
    @Autowired
    private DzEquipmentDao dzEquipmentDao;
    @Autowired
    private DzEquipmentTimeAnalysisService analysisService;
    /**
     * 根据订单产线号查询所有设备当日用时分析(旧)-不分时段
     *
     * @param noLineNo
     */
    @Override
    public Result getTimeAnalysis(GetOrderNoLineNo noLineNo) {
        String orderNo = noLineNo.getOrderNo();
        String key = noLineNo.getProjectModule()+RedisKey.DEVICE_TIME_ANALYSIS_OLD + orderNo + noLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Object o = redisUtil.get(key);
                if (o != null) {
                    return (Result) o;
                }
            }
            DzProductionLine lineIdByOrderNoLineNo = lineService.getLineIdByOrderNoLineNo(noLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            EquipmentAvailableDo equipmentAvailableDo = new EquipmentAvailableDo();
            List<DzEquipment> yName = getLineIdIsShow(lineIdByOrderNoLineNo.getId(), FinalCode.IS_SHOW);
            List<String> eqName = new ArrayList<>();
            List<String> timeRun = new ArrayList<>();
            List<String> stopTime = new ArrayList<>();
            long todayPastTime = DateUtil.getStartDate();//当日凌晨12点的时间戳
            for (DzEquipment dzEquipment : yName) {
                eqName.add(dzEquipment.getNickName());
                String equipmentNo = dzEquipment.getEquipmentNo();
                Integer equipmentType = dzEquipment.getEquipmentType();
                Long runTimeData = analysisService.getEquipmentAvailable(dzEquipment.getId(),orderNo);
                BigDecimal bigDecimal = new BigDecimal(runTimeData);
//               运行小时
                BigDecimal divide = bigDecimal.divide(new BigDecimal(3600000), 2, BigDecimal.ROUND_HALF_UP);
//                当日已过时间
                BigDecimal dd = new BigDecimal(todayPastTime).divide(new BigDecimal(3600000), 2, BigDecimal.ROUND_HALF_UP);
                timeRun.add(divide.toString());//运行时间
                //当前停机时间等于 当日已过时间-当前运行时间
                BigDecimal subtract = dd.subtract(divide);
                stopTime.add(subtract.toString());//停机时间
            }
            equipmentAvailableDo.setEqName(eqName);
            equipmentAvailableDo.setTimeRun(timeRun);
            equipmentAvailableDo.setStopTime(stopTime);
            Result ok = Result.ok(equipmentAvailableDo);
            redisUtil.set(key, ok, noLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("根据产线查询所有设备当日用时分析(旧)异常:{}", e.getMessage(),e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }

    private synchronized List<DzEquipment> getLineIdIsShow(String id, String isShow) {
        List<DzEquipment> list = redisUtil.lGet(RedisKey.LINE_ID_DEVICE_IS_SHOW + id + isShow, 0, -1);
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        }
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
        wrapper.eq("line_id", id);
        wrapper.eq("is_show", FinalCode.IS_SHOW);
        wrapper.select("equipment_no", "equipment_type", "equipment_name", "nick_name", "id");
        List<DzEquipment> yName = dzEquipmentDao.selectList(wrapper);
        int timeCahce = (int) (Math.random() * 600) + 60;
        redisUtil.del(RedisKey.LINE_ID_DEVICE_IS_SHOW + id + isShow);
        redisUtil.lSet(RedisKey.LINE_ID_DEVICE_IS_SHOW + id + isShow, yName, timeCahce);
        return yName;
    }
}
