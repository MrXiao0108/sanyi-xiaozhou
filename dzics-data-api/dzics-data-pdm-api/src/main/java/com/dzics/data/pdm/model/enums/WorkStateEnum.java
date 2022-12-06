package com.dzics.data.pdm.model.enums;

/**
 * @author: van
 * @since: 2022-06-28
 */
public enum WorkStateEnum {

    /*
     * 作业
     */
    ZUO_YE("1"),

    /*
     * 待机
     */
    DAI_JI("2"),

    /*
     * 故障
     */
    GU_ZHANG("3"),

    /*
     * 关机
     */
    GUAN_JI("4");

    private final String val;

    WorkStateEnum(String val) {
        this.val = val;
    }

    public String val() {
        return val;
    }
}
