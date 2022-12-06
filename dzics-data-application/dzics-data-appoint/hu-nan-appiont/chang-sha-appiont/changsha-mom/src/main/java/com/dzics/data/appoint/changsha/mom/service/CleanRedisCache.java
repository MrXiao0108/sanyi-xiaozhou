package com.dzics.data.appoint.changsha.mom.service;


import com.dzics.data.common.base.vo.Result;
import org.springframework.stereotype.Component;

/**
 *
 * @author dzics
 */
@Component
public interface CleanRedisCache {
    /**
     * 清楚缓存
     * @param orderNo
     * @param lineNo
     * @param inIslandCode
     * @return
     */
    Result cleanPosition(String orderNo, String lineNo,String inIslandCode);
}
