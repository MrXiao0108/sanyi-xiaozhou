package com.dzics.data.appoint.changsha.mom.service.impl;

import com.dzics.data.appoint.changsha.mom.service.CleanRedisCache;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author LiuDongFei
 * @date 2022年07月11日 14:30
 */
@Service
@Slf4j
public class CleanRedisCacheImpl implements CleanRedisCache {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Result cleanPosition(String orderNo, String lineNo, String inIslandCode) {
        redisUtil.del(RedisKey.Rob_Call_Material + orderNo + lineNo + inIslandCode);
        return Result.ok();
    }
}
