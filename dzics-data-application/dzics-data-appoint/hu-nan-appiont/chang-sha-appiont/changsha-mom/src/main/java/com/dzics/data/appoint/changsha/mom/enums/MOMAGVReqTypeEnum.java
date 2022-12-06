package com.dzics.data.appoint.changsha.mom.enums;

/**
 * 工序间配送 reqType
 *
 * @author: van
 * @since: 2022-06-28
 */
public enum MOMAGVReqTypeEnum {

    /*
     * 请求空料框
     */
    IN_EMPTY_CONTAINER("0"),

    /*
     * 拉走满料框
     */
    OUT_FULL_CONTAINER("1"),

    /*
     * 拉走空料框
     */
    OUT_EMPTY_CONTAINER("3");

    private final String val;

    MOMAGVReqTypeEnum(String val) {
        this.val = val;
    }

    public String val() {
        return val;
    }
}
