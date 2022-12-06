package com.dzics.data.appoint.changsha.mom.enums;

/**
 * MOM AGV握手 WaitType
 *
 * @author: van
 * @since: 2022-06-28
 */
public enum MOMAGVHandshakeWaitTypeEnum {

    /*
     * AGV已到等待点请求进入
     */
    IN("0"),

    /*
     * 配送已完成
     */
    OK("1");

    private final String val;

    MOMAGVHandshakeWaitTypeEnum(String val) {
        this.val = val;
    }

    public String val() {
        return val;
    }
}
