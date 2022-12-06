package com.dzics.data.appoint.changsha.mom.util;

import lombok.Data;

/**
 * 调用 MOM接口  taskType 请求类型
 *
 * @author ZhangChengJun
 * Date 2021/6/11.
 */
@Data
public class MomTaskType {

    /**
     * AGV 搬运信息返回
     */
    public static final String AGV_HANDLING_FEEDBACK = "10";

    /**
     * MOM下发订单
     */
    public static final String MOM_ORDER_TYPE = "1";

    /**
     * MOM下发 订单工序组件下发
     */
    public static final String MOM_ORDER_OPR_SEQUENCE = "17";

    /**
     * 报工请求类型
     */
    public static final String REPORT_WORK = "57";

    /**
     * 查询料框信息类型
     */
    public static final String QUERY_MATERIAL = "47";

    /**
     * 更新投料点信息
     */
    public static final String UPDATE_FEEDING_POINT_INFORMATION = "9";

    /**
     * 工序间配送 满料拉走 送空料框 类型
     */
    @Deprecated
    public static final String INTER_PROCESS_DISTRIBUTION = "46";


    public static final String MOVE_FRAME = "6";

    /**
     * 生产叫料请求类型
     */
    public static final String APPLICATION_MATERIAL = "4";

    /**
     * 查询下工序工序号
     */
    public static final String nextOprSeqNo = "104";

    /**
     * 人员信息同步至中控
     */
    public static final String SYN_USER_MESSAGE = "105";

    /**
     * 序列号查询订单号
     */
    public static final String QUERY_ORDER_NUMBER = "103";

    /**
     * 生产进度反馈
     */
    public static final String MOM_ORDER_REPORT = "3";

    /**
     * 料物流配送
     */
    public static final String CALL_MATERIAL = "46";

    /*
     * 缓存区库存下发
     */
    public static final String STORAGE_ISSUE = "48";

    /**
     * AGV握手
     */
    public static final String AGV_HANDSHAKE = "150";

    /**
     * 装框信息
     */
    public static final String MOM_Packing = "166";
}
