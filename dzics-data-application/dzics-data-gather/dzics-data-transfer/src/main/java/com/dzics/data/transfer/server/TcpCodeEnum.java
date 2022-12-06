package com.dzics.data.transfer.server;

/**
 * @author: van
 * @since: 2022-06-28
 */
public enum TcpCodeEnum {

    /*
     * 报工
     */
    BAO_GONG("##P01");

    private final String val;

    TcpCodeEnum(String val) {
        this.val = val;
    }

    public String val() {
        return val;
    }
}
