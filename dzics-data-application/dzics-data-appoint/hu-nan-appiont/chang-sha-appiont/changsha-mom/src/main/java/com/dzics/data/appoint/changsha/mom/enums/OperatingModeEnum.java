package com.dzics.data.appoint.changsha.mom.enums;

/**
 * @author LiuDongFei
 * @date 2022年09月14日 16:02
 */
public enum OperatingModeEnum {

    /*
     * 手动模式
     */
    MANUAL_MODE("1"),

    /*
     * 自动模式
     */
    AUTOMATIC_MODE("2"),

    /*
    * 编号
    * */
    NUMBER("3");

    private final String val;

    OperatingModeEnum(String val) {
        this.val = val;
    }

    public String val() {
        return val;
    }
}
