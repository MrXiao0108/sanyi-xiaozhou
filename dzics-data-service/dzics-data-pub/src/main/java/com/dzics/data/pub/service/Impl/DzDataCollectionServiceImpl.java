package com.dzics.data.pub.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.db.dao.DzDataCollectionDao;
import com.dzics.data.pub.model.dao.TimeAnalysisCmd;
import com.dzics.data.pub.model.entity.DzDataCollection;
import com.dzics.data.pub.service.DzDataCollectionService;
import com.dzics.data.pub.service.kanban.CurrentDateService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-29
 */
@Service
@Slf4j
public class DzDataCollectionServiceImpl extends ServiceImpl<DzDataCollectionDao, DzDataCollection> implements DzDataCollectionService, CurrentDateService<Result, GetOrderNoLineNo> {

    @Autowired
    public RedisUtil redisUtil;

    @Override
    public List<TimeAnalysisCmd> getDeviceTypeCmdSingal(String shardingParameter) {
        return baseMapper.getDeviceTypeCmdSingal(shardingParameter);
    }

    @Override
    public TimeAnalysisCmd getDeviceId(String deviceId) {
        return baseMapper.getDeviceId(deviceId);
    }

    @Override
    public DzDataCollection cacheDeviceId(String deviceId) {
        QueryWrapper<DzDataCollection> wp = new QueryWrapper<>();
        wp.eq("device_id", deviceId);
        DzDataCollection one = getOne(wp);
        return one;
    }

    @Override
    public DzDataCollection updateDeviceId(DzDataCollection dzDataCollection) {
        QueryWrapper<DzDataCollection> wp = new QueryWrapper<>();
        wp.eq("device_id", dzDataCollection.getDeviceId());
        boolean update = update(dzDataCollection, wp);
        if (update) {
            return dzDataCollection;
        } else {
            return null;
        }
    }

    @Override
    public boolean instert(DzDataCollection dzDataCollection) {
        return save(dzDataCollection);
    }

    @Override
    public TimeAnalysisCmd getIotTableDeviceState(String deviceId) {
        return getDeviceId(deviceId);
    }

    @Override
    public TimeAnalysisCmd updateIotTableDeviceState(TimeAnalysisCmd dzDataCollection) {
        return dzDataCollection;
    }

    @Override
    public Result getCurrentDate(GetOrderNoLineNo orderNoLineNo) {
        String orderNo = orderNoLineNo.getOrderNo();
        String lineNo = orderNoLineNo.getLineNo();
        String key = orderNoLineNo.getProjectModule() + RedisKey.GET_CURRENT_DATE + orderNo + lineNo;
        try {
            if (redisUtil.hasKey(key)) {
                Object o = redisUtil.get(key);
                return (Result) o;
            }
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH) + 1;
            Map<String, Object> map = new HashMap<>();
            map.put("month", month);
            map.put("dayClasses", 0);
            Result ok = Result.ok(map);
            redisUtil.set(key, ok, orderNoLineNo.getCacheTime());
            return ok;
        } catch (Throwable e) {
            log.error("根据产线获取月份标准节拍异常:{}", e.getMessage(),e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }
}
