package com.dzics.data.pdm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.pdm.db.dao.DzDayShutDownTimesDao;
import com.dzics.data.pdm.model.entity.DzDayShutDownTimes;
import com.dzics.data.pdm.service.DzDayShutDownTimesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 设备每日停机次数 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-23
 */
@Service
@Slf4j
public class DzDayShutDownTimesServiceImpl extends ServiceImpl<DzDayShutDownTimesDao, DzDayShutDownTimes> implements DzDayShutDownTimesService {
    @Override
    public DzDayShutDownTimes getDayShoutDownTime(String lineNum, String deviceNum, Integer deviceType, String orderNumber, LocalDate nowLocalDate) {
        QueryWrapper<DzDayShutDownTimes> wp = new QueryWrapper<>();
        wp.eq("work_date", nowLocalDate);
        wp.eq("order_no", orderNumber);
        wp.eq("line_no", lineNum);
        wp.eq("equipment_no", deviceNum);
        wp.eq("equipment_type", deviceType);
        wp.select("id","down_sum","work_date","order_no","line_no","equipment_no","equipment_type");
        List<DzDayShutDownTimes> list = list(wp);
        if (list == null || list.size() == 0) {
            return null;
        }
        if (list.size() > 1) {
            log.warn("相同设备一天的停机次数记录存在多次：lineNum:{},deviceNum:{},deviceType:{}, orderNumber:{}, nowLocalDate:{}",
                    lineNum, deviceNum, deviceType, orderNumber, nowLocalDate);
            return list.get(0);
        } else {
            return list.get(0);
        }
    }

    @Override
    public boolean saveDzDayShutDownTimes(DzDayShutDownTimes dzDayShutDownTimes) {
        return save(dzDayShutDownTimes);
    }

    @Override
    public DzDayShutDownTimes updateByIdDzDayShutDownTimes(DzDayShutDownTimes dzDayShutDownTimes) {
        boolean updateById = updateById(dzDayShutDownTimes);
        if (updateById) {
            return dzDayShutDownTimes;
        }
        return null;
    }
}
