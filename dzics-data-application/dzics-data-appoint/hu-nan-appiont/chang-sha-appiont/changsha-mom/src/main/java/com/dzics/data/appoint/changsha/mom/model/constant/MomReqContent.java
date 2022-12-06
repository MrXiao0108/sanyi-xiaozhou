package com.dzics.data.appoint.changsha.mom.model.constant;

/**
 * 中控编号
 *
 * @author ZhangChengJun
 * Date 2021/6/16.
 */
public class MomReqContent {
    /**
     * 系统编码 中控系统代号
     */
    public static final String REQ_SYS = "XZCJG02";
    /**
     * 工厂编号
     */
    public static final String FACILITY = "5802";
    /**
     * 顺序号 固定值000000
     */
    public static final String SEQUENCENO = "000000";


    /**
     * mom 请求响应code 为 0 时 表示正常
     */
    public static final String MOM_CODE_OK = "0";

    /**
     * 该订单可叫料数量为0，中控需切换订单再叫料（中兴）
     */
    public static final String MOM_CODE_NEXT = "5";

    /**
     * 等待叫料请求，由人工介入，进行下步处理
     */
    public static final String MOM_CALL_WAIT = "6";


    public static final String ProgressType = "1";
    /**
     * 2:请求移走空料框；
     */
    public static final String CALL_AGV_REQ_TYPE_2 = "2";
    /**
     * 3:请求空料框；
     */
    public static final String CALL_AGV_REQ_TYPE_3 = "3";
    /**
     * 4:请求移走满料框；
     */
    public static final String CALL_AGV_REQ_TYPE_4 = "4";
}
