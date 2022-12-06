package com.dzics.data.pdm.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.pdm.db.dao.DzLineShiftDayDao;
import com.dzics.data.pdm.model.dto.DayReportForm;
import com.dzics.data.pdm.model.entity.DzLineShiftDay;
import com.dzics.data.pdm.service.DzLineShiftDayService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 设备产线 每日 排班表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-19
 */
@SuppressWarnings("ALL")
@Slf4j
@Service
public class DzLineShiftDayServiceImpl extends ServiceImpl<DzLineShiftDayDao, DzLineShiftDay> implements DzLineShiftDayService {
    @Autowired
    public RedisUtil<DzLineShiftDay> redisUtil;
    @Autowired
    private DzLineShiftDayDao dzLineShiftDayMapper;

    @Override
    public List<DzLineShiftDay> getBc(List<Long> eqId) {
        return dzLineShiftDayMapper.getBc(eqId);
    }

    @Autowired
    public RedissonClient redissonClient;

    @Override
    public List<Long> getNotPb(LocalDate now) {
        return dzLineShiftDayMapper.getNotPb(now);
    }

    /**
     * 排班
     */
    @Override
    public void arrange() {
        RLock lock = redissonClient.getLock(RedisKey.SYS_BUS_TASK_ARRANGE);
        try {
            lock.lock();
//            查询未排班的设备
//            当天班次排班
            LocalDate now = LocalDate.now();
            String substring = now.toString().substring(0, 7);
            int year = now.getYear();
            List<Long> eqId = getNotPb(now);
            if (CollectionUtils.isNotEmpty(eqId)) {
                List<DzLineShiftDay> dzLineShiftDays = getBc(eqId);
                dzLineShiftDays.stream().forEach(dzLineShiftDay -> {
                    dzLineShiftDay.setWorkData(now);
                    dzLineShiftDay.setWorkYear(year);
                    dzLineShiftDay.setWorkMouth(substring);
                });
                saveBatch(dzLineShiftDays);
            }
//            当天班次加+ 1 排班
            LocalDate localDate = LocalDate.now().plusDays(1L);
            String substringAdd = localDate.toString().substring(0, 7);
            int yearAdd = localDate.getYear();
            List<Long> eqNext = getNotPb(localDate);
            if (CollectionUtils.isNotEmpty(eqNext)) {
                List<DzLineShiftDay> dzLineShiftDays = getBc(eqNext);
                dzLineShiftDays.stream().forEach(dzLineShiftDay -> {
                    dzLineShiftDay.setWorkData(localDate);
                    dzLineShiftDay.setWorkMouth(substringAdd);
                    dzLineShiftDay.setWorkYear(yearAdd);
                });
                saveBatch(dzLineShiftDays);
            }
        } catch (Throwable e) {
            log.error("排班时发生错误：{}", e);
        } finally {
            lock.unlock();
        }

    }

    @Override
    public List<DayReportForm> getWorkDate(LocalDate now) {
        return dzLineShiftDayMapper.getWorkDate(now);
    }


    @Override
    public DzLineShiftDay getLingShifuDay(String deviceId, String lineNum, String deviceNum, String deviceType, String orderNumber, LocalDate nowLocalDate, LocalTime localTime) {
        List<DzLineShiftDay> dzLineShiftDays = this.getlingshifudays(deviceId, lineNum, deviceNum, deviceType, orderNumber, nowLocalDate);
        if (CollectionUtils.isNotEmpty(dzLineShiftDays)) {
            for (DzLineShiftDay day : dzLineShiftDays) {
                LocalTime startTime = day.getStartTime();
                LocalTime endTime = day.getEndTime();
                if (localTime.compareTo(startTime) == 0) {
                    return day;
                }
                //判断班次  开始时间和结束时间是否在同一天
                // 结束时间大于开始时间  则为同一天  反之则为跨天
                if (endTime.compareTo(startTime) == 1) {
                    if (localTime.compareTo(startTime) == 1 && localTime.compareTo(endTime) == -1) {
                        return day;
                    }
                } else {
                    if (localTime.compareTo(endTime) == -1 || localTime.compareTo(startTime) == 1) {
                        //当前时间已经属于当前班次的第二天了
                        if (localTime.compareTo(endTime) == -1) {
                            List<DzLineShiftDay> dzLineShiftDays2 = this.getlingshifudays(deviceId, lineNum, deviceNum, deviceType, orderNumber, nowLocalDate.plusDays(-1));
                            for (DzLineShiftDay day2 : dzLineShiftDays2) {
                                if (day2.getEndTime().compareTo(day2.getStartTime()) == -1) {
                                    if (localTime.compareTo(day2.getEndTime()) == -1 || localTime.compareTo(day2.getStartTime()) == 1) {
                                        return day2;
                                    }
                                }
                            }
                        } else {
                            return day;
                        }
                    }
                }
            }
            log.error("查询设备当日排班数据存在,没有在时间范围内：lineNum: {},deviceNum: {},deviceType: {},nowLocalDate:{} ,localTime:{} 失败",
                    lineNum, deviceNum, deviceType, nowLocalDate);
        } else {
            log.error("查询设备当日排班不存在：deviceId:{},orderNumber:{},lineNum: {},deviceNum: {},deviceType: {},nowLocalDate:{} 失败",
                    deviceId, orderNumber, lineNum, deviceNum, deviceType, nowLocalDate);
        }
        return null;
    }


    @Override
    public List<DzLineShiftDay> getDeviceIdShift(String equipmentId, LocalDate nowDate) {
        String of = String.valueOf(nowDate);
        List<DzLineShiftDay> days = redisUtil.lGet(RedisKey.DzLineShiftDayService + equipmentId + of, 0, -1);
        if (CollectionUtils.isNotEmpty(days)) {
            return days;
        } else {
            QueryWrapper<DzLineShiftDay> wpShd = new QueryWrapper<DzLineShiftDay>()
                    .eq("eq_id", equipmentId)
                    .eq("work_data", nowDate)
                    .orderByAsc("sort_no");
            List<DzLineShiftDay> days1 = dzLineShiftDayMapper.selectList(wpShd);
            if (CollectionUtils.isEmpty(days1)) {
                days1 = new ArrayList<>();
            }
            redisUtil.lSet(RedisKey.DzLineShiftDayService + equipmentId + of, days1, 1800);
            return days1;
        }
    }

    private List<DzLineShiftDay> getlingshifudays(String deviceId, String lineNum, String deviceNum, String deviceType, String orderNumber, LocalDate nowLocalDate) {
        QueryWrapper<DzLineShiftDay> wp = new QueryWrapper<>();
        wp.eq("line_no", lineNum);
        wp.eq("equipment_no", deviceNum);
        wp.eq("equipment_type", deviceType);
        wp.eq("work_data", nowLocalDate);
        wp.eq("order_no", orderNumber);
        wp.eq("eq_id", deviceId);
        wp.orderByAsc("sort_no");
        return dzLineShiftDayMapper.selectList(wp);

    }

}
