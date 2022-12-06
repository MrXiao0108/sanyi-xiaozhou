package com.dzics.data.appoint.changsha.mom.util;

import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.LocalDateTime;

/**
 * @author: van
 * @since: 2022-07-22
 */
public class TimeUtil {

    public static String normalTime() {
        LocalDateTime now = LocalDateTime.now();
        return LocalDateTimeUtil.formatNormal(now);
    }
}
