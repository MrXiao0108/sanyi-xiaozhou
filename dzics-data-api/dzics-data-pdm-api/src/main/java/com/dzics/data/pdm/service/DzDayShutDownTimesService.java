package com.dzics.data.pdm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.pdm.model.entity.DzDayShutDownTimes;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;

/**
 * <p>
 * 设备每日停机次数 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-23
 */
public interface DzDayShutDownTimesService extends IService<DzDayShutDownTimes> {
    /**
     * 查询是否存在每日停机次数记录
     *
     * @param lineNum
     * @param deviceNum
     * @param deviceType
     * @param orderNumber
     * @param nowLocalDate
     * @return
     */
    @Cacheable(cacheNames = "cacheService.getDayShoutDownTime", key = "#lineNum+#deviceNum+#deviceType+#orderNumber+#nowLocalDate", unless = "#result == null")
    DzDayShutDownTimes getDayShoutDownTime(String lineNum, String deviceNum, Integer deviceType, String orderNumber, LocalDate nowLocalDate);

    /**
     * 保存当天的停机次数
     * @param dzDayShutDownTimes
     * @return
     */
    boolean saveDzDayShutDownTimes(DzDayShutDownTimes dzDayShutDownTimes);

    /**
     * 更新每日停机次数
     *
     * @param dzDayShutDownTimes
     * @return
     */
    @CachePut(cacheNames = "cacheService.getDayShoutDownTime", key = "#dzDayShutDownTimes.lineNo+#dzDayShutDownTimes.equipmentNo" +
            "+#dzDayShutDownTimes.equipmentType+#dzDayShutDownTimes.orderNo+#dzDayShutDownTimes.workDate")
    DzDayShutDownTimes updateByIdDzDayShutDownTimes(DzDayShutDownTimes dzDayShutDownTimes);
}
