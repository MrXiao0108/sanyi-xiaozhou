package com.dzics.data.acquisition.server;

/**
 * @author: van
 * @since: 2022-06-28
 */
public enum TcpCodeEnum {

    /*
     * 报工
     */
    BAO_GONG("##P01"),

    /*
     * 报工 - 临时码
     */
    BAO_GONG_TEMP_CODE("##P02");

    private final String val;

    TcpCodeEnum(String val) {
        this.val = val;
    }

    public String val() {
        return val;
    }
}
