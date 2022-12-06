package com.dzics.data.appoint.changsha.mom.util;

import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.util.SnowflakeUtil;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

/**
 * redis id逐渐
 *
 * @author ZhangChengJun
 * Date 2021/6/17.
 * @since
 */
@Component
@Slf4j
public class RedisUniqueID {
    @Autowired
    private RedisUtil redisUtil;
    private String systemCoding = "DZICS-";
    @Value("${reqid.type}")
    private String reqidType;
    @Autowired
    private SnowflakeUtil snowflakeUtil;
    @Value("${order.code}")
    private String orderCode;

    public synchronized String getkey() {
        try {
            String now = LocalDate.now().toString();
            Object o = redisUtil.get(RedisKey.MOM_REDIS_KEY + now);
            if (o == null) {
                redisUtil.set(RedisKey.MOM_REDIS_KEY + now, 0, (25 * 3600));
            }
            long incr = redisUtil.incr(RedisKey.MOM_REDIS_KEY + now, 1);
            log.debug("redis中自增的值：{}", incr);
            String reId = String.valueOf(incr);
            int length = reId.length();
            if (length == 1) {
                reId = "00000" + reId;
            } else if (length == 2) {
                reId = "0000" + reId;
            } else if (length == 3) {
                reId = "000" + reId;
            } else if (length == 4) {
                reId = "00" + reId;
            } else if (length == 5) {
                reId = "0" + reId;
            }
            String reqId = systemCoding + orderCode + "-" + now.replaceAll("-", "") + reId;
            return reqId;
        } catch (Throwable e) {
            log.error("获取请求ID错误：{}", e.getMessage(), e);
            return snowflakeUtil.nextId() + "";
        }
    }


    public synchronized String getGroupId() {
        try {
            String now = LocalDate.now().toString();
            Object o = redisUtil.get(RedisKey.MOM_REDIS_KEY_GROUPID + now);
            if (o == null) {
                redisUtil.set(RedisKey.MOM_REDIS_KEY_GROUPID + now, 0, (25 * 3600));
            }
            long incr = redisUtil.incr(RedisKey.MOM_REDIS_KEY_GROUPID + now, 1);
            String reId = String.valueOf(incr);
            String reqId = systemCoding + reqidType + "-" + reId;
            return reqId;
        } catch (Throwable e) {
            log.error("获取请求ID错误：{}", e.getMessage(), e);
            return snowflakeUtil.nextId() + "";
        }
    }

    public String getUUID() {
        String s = UUID.randomUUID().toString().replaceAll("-", "");
        return s;
    }


    public Object getToken() {
        return null;
    }
}
