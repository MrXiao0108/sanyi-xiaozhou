package com.dzics.common.util.core.id;

/**
 * @Classname IdUtil
 * @Description 描述
 * @Date 2022/8/31 13:21
 * @Created by NeverEnd
 */
public class IdUtil {

    private static final SnowflakeUtil SNOWFLAKE_UTIL = new SnowflakeUtil();

    public static String snowflakeStr() {
        return String.valueOf(SNOWFLAKE_UTIL.nextId());
    }
}
