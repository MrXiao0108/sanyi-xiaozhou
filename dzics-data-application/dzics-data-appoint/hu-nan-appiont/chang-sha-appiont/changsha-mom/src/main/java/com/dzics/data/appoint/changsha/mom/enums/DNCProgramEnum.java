package com.dzics.data.appoint.changsha.mom.enums;

/**
 * @author: van
 * @since: 2022-06-28
 */
public enum DNCProgramEnum {

    /*
     * 请求失败
     */
    REQUEST_FAIL("1"),

    /*
     * 请求成功，未反馈
     */
    REQUEST_SUCCESS("2"),

    /*
     * 切换成功
     */
    CHANGE_SUCCESS("3"),

    /*
     * 切换失败
     */
    CHANGE_FAIL("4"),

    /*
     * 人工干预
     */
    MANUAL("5");

    private final String val;

    DNCProgramEnum(String val) {
        this.val = val;
    }

    public String val() {
        return val;
    }
}
