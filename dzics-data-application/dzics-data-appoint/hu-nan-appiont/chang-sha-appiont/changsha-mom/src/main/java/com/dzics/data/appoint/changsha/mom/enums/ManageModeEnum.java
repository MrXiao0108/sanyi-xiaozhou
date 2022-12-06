package com.dzics.data.appoint.changsha.mom.enums;

/**
 * @author LiuDongFei
 * @date 2022年09月14日 16:02
 */
public enum ManageModeEnum {


    DNC("3"),

    /*
     * 报工是否发送序列号
     * type
     * 0: 不发；1：发送
     */
    WORK_NO("work_no");

    private final String val;

    ManageModeEnum(String val) {
        this.val = val;
    }

    public String val() {
        return val;
    }
}
