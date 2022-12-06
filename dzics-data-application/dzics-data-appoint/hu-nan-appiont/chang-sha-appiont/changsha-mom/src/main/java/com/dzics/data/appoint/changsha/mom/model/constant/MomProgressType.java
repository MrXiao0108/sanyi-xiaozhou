package com.dzics.data.appoint.changsha.mom.model.constant;

/**
 * @Classname ProgressType
 * @Description 描述
 * @Date 2022/2/12 11:57
 * @Created by NeverEnd
 */
public enum MomProgressType {
    START("0", "开工"),
    COMPLETE("1", "完工"),
    IN("2", "进入工位"),
    OUT("3", "离开工位"),
    ERR("4", "异常报工"),;

    MomProgressType(String code, String typeDesc) {
        this.code = code;
        this.typeDesc = typeDesc;
    }

    private String typeDesc;

    private String code;

    public String getTypeDesc() {
        return typeDesc;
    }

    public String getCode() {
        return code;
    }
}
