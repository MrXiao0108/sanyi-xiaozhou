package com.dzics.data.common.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 设备类型枚举类
 *
 * @author ZhangChengJun
 * Date 2021/1/20.
 * @since
 */
public enum EquiTypeEnum {
    JCSB(1, "检测设备"),
    JC(2, "机床"),
    JQR(3, "机器人"),
    XJ(4, "相机"),
    EQCODE(6, "报工设备"),
    AVG(7, "AGV"),
    CHJ(8, "淬火机"),
    JZJ(9, "校直机"),
    MEN(10, "门"),
    DG(11, "地轨"),
    ACC(119, "ACC");

    EquiTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @EnumValue
    private int code;
    private String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
