package com.dzics.data.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateRangeUtil {
    /**
     * 获取最近几天的日期
     *
     * @param days 多少天
     * @return 例如 [2022-04-15, 2022-04-14]
     */
    public static List<String> getRecentDaysYMD(int days) {
        days = days - 1;
        List<String> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = days; i >= 0; i--) {
            dates.add(today.minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        return dates;
    }
    /**
     * 获取最近几天的日期
     *
     * @param days 多少天
     * @return 例如 [04-15,04-14]
     */
    public static List<String> getRecentDaysMD(int days) {
        days = days - 1;
        List<String> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = days; i >= 0; i--) {
            dates.add(today.minusDays(i).format(DateTimeFormatter.ofPattern("MM-dd")));
        }
        return dates;
    }

}
