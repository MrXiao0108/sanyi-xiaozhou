package com.dzics.data.appoint.changsha.mom.enums;

/**
 * MOM AGV握手 Result
 *
 * @author: van
 * @since: 2022-06-28
 */
public enum MOMAGVHandshakeResultEnum {

    /*
     * 允许进入
     */
    IN("2"),

    /*
     * 配送完成状态已收到
     */
    OK("3");

    private final String val;

    MOMAGVHandshakeResultEnum(String val) {
        this.val = val;
    }

    public String val() {
        return val;
    }
}
