package com.dzics.common.util.core.time;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
public class DateUtil {

    /**
     * 底层发送来的数据中，时间格式化方法
     *
     * @param dataStr
     * @return
     */
    public synchronized static Date stringDateToformatDate(String dataStr) {
        try {
            String[] split = dataStr.split("\\.");
            String senc = split[1];
            String time = split[0];
            if (senc.length() == 4) {
                String divide = time + "." + (new BigDecimal(senc).divide(new BigDecimal("10.0")).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                dataStr = divide;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return dateFormat.parse(dataStr);
        } catch (Throwable e) {
            log.error("日期格式化错误直接返回当前时间:{}", e);
            return new Date();
        }
    }

    public static Date stringDateToformatDateYmdHms(String dataStr) {
        try {
            if (StringUtils.isEmpty(dataStr)) {
                return null;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.parse(dataStr);
        } catch (ParseException e) {
            log.error("日期格式化错误直接返回当前时间:{}", e);
            return null;
        }
    }

    public static LocalDate dataToLocalDate(Date nowDate) {
        Instant instant = nowDate.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate;
    }

    public static String getDateTime(Date date) {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static LocalTime dataToLocalTime(Date nowDate) {
        Instant instant = nowDate.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        LocalTime localTime = localDateTime.toLocalTime();
        return localTime;
    }

    /**
     * 获取当天日期（yyyy-MM-dd）
     *
     * @return 当天日期
     */
    public static String getDate() {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }

    /**
     * data 格式化转为 字符串
     *
     * @param date
     * @return
     */
    public static String dateFormatToStingYmdHms(Date date) {
        if (date == null) {
            date = new Date();
        }
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static Date dayjiaday(Date stareDate, int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(stareDate);
        c.add(Calendar.DAY_OF_MONTH, day);
        return c.getTime();
    }

    //获取本月第一天
    public static String firstDay() {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//1:本月第一天
        String day1 = dateFormat.format(c.getTime());
        return day1;
    }

    //获取本月最后一天
    public static String lastDay() {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        String day2 = dateFormat.format(ca.getTime());
        return day2;
    }

    //获取当前所有月份
    public static List<String> getAllMonth() {
        List<String> month = new ArrayList<>();
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        for (int i = 1; i < 13; i++) {
            if (i < 10) {
                month.add(year + "-0" + i);
            } else {
                month.add(year + "-" + i);
            }

        }
        return month;
    }

    //Date转string  yyyy-MM-dd hh:ss:mm
    public static String getDateStr(Date date) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sd.format(date);
    }

    public static String toStr(Date date, String format) {
        SimpleDateFormat sd = new SimpleDateFormat(format);
        return sd.format(date);
    }

    /**
     * 获取当天12点的时间戳
     *
     * @return 当天日期
     */
    public static Long getStartDate() throws ParseException {
        Date date = new Date();
        String year = String.format("%tY", date);
        String mon = String.format("%tm", date);
        String day = String.format("%td", date);
        String dateStr = year + "-" + mon + "-" + day + " 00:00:00";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parse = simpleDateFormat.parse(dateStr);
        long l = System.currentTimeMillis() - parse.getTime();
        return l;

    }

    /**
     * 获取当天12点的时间戳
     *
     * @return 当天日期
     */
    public static Date getNowDate() throws ParseException {
        Date date = new Date();
        String year = String.format("%tY", date);
        String mon = String.format("%tm", date);
        String day = String.format("%td", date);
        String dateStr = year + "-" + mon + "-" + day + " 00:00:00";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parse = simpleDateFormat.parse(dateStr);

        return parse;

    }

    public static String dateFormatToStingYmdHmsMom(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
        String requireTime = format.format(date);
        return requireTime;
    }

    public static Date stringDateToformatDateYmdHmsMom(String requireTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
        try {
            return format.parse(requireTime);
        } catch (ParseException e) {
            log.error("日期格式化错误直接返回当前时间:{}", e);
            return new Date();
        }

    }

    public static Date getNowDate(LocalDate localDate, String time) throws ParseException {
        String dateStr = localDate.toString() + " " + time;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parse = simpleDateFormat.parse(dateStr);

        return parse;

    }

    /**
     * 获取当前月份
     * 例:2021-08
     *
     * @return
     */
    public static String getNowMonth() {
        return LocalDate.now().toString().substring(0, 7);
    }
}
