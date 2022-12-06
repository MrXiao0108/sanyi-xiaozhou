package com.dzics.data.kanban.changsha.xiaozhou.cujia.impl.kanban;

import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.service.kanban.CurrentDateService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @Classname CurrentDateServiceImpl
 * @Description 描述
 * @Date 2022/3/17 15:27
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class CurrentDateServiceImpl implements CurrentDateService<Result<Map<String, String>>, GetOrderNoLineNo> {
    private final RedisUtil<Result<Map<String, String>>> redisUtil;

    public CurrentDateServiceImpl(RedisUtil<Result<Map<String, String>>> redisUtil) {
        this.redisUtil = redisUtil;
    }

    /**
     * 获取当前日期数据
     *
     * @param t 订单产线 项目模块
     * @return 当前日期信息
     */
    @Override
    public Result<Map<String, String>> getCurrentDate(GetOrderNoLineNo t) {
        String orderNo = t.getOrderNo();
        String lineNo = t.getLineNo();
        String key = t.getProjectModule() + RedisKey.GET_CURRENT_DATE + orderNo + lineNo;
        try {
            if (redisUtil.hasKey(key)) {
                return redisUtil.get(key);
            }
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH) + 1;
            Map<String, String> map = new HashMap<>();
            map.put("month", month + "");
            map.put("currentDate", LocalDate.now().toString());
            Result<Map<String, String>> ok = Result.ok(map);
            redisUtil.set(key, ok, t.getCacheTime());
            return ok;
        } catch (Throwable e) {
            log.error("根据产线当前日期异常:{}", e.getMessage(), e);
            throw e;
        }
    }
}
