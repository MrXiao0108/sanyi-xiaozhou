package com.dzics.data.common.base.model.constant;

import lombok.Data;

/**
 * 更新料点料框 类型 定义
 *
 * @author ZhangChengJun
 * Date 2021/11/12.
 * @since
 */
@Data
public class PointType {

    /*
     * 上料
     */
    public static final String UP = "SL";

    /*
     * NG
     */
    public static final String NG = "NG";

    /*
     * 退库（未做的料退回仓库）
     */
    public static final String TL = "TL";

    /*
     * 既是上料又是下料
     */
    public static final String SLXL = "SLXL";
}
