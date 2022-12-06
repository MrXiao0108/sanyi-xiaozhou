package com.dzics.data.common.base.model.constant;

public class DzUdpType {
    /**
     * 处理交换吗
     */
    public static final String udpTypeQrcode = "1";
    /**
     * 处理页面点击来料信号，发送到UDP，当前收到的数据是UDP收到原路返还的回复数据
     */
    public static final String udpType_AGV = "2";
    /**
     * Q,4,orderNo,lineNo,1100,小车,校验结果信息
     */
    public static final String udpLogs = "4";//1100
    /**
     * 设备控制指令
     */
    public static final String udpCmdControl = "5";

    /**
     * 二维码填写动作
     */
    public static final String udpCmd_QR_Code = "7";
    /**
     * 控制指令下发
     */
    public static final String udpCmdControlInner = "1200";
    /**
     * 终止
     */
    public static final String controlstop = "1";
    /**
     * 开始
     */
    public static final String controlStar = "2";
    /**
     * 暂停
     */
    public static final String controlStarStop = "3";
    /**
     * 由暂停变为开始
     */
    public static final String controlStarStopStart = "4";

//    =============================
    /**
     * 控制指令回传状态
     */
    public static final String udpCmdControlUp = "1201";
    public static final String ok = "1";
    public static final String err = "0";
//  ================================

    /**
     * udp 回传数量指令值
     */
    public static final String udpCmdControlSum = "1202";

//  ================================
    /**
     * 号小车来料信号
     */
    public static final String udpTypeAgvSinal = "1001";
    public static final String udpTypeAgvSinalFRID = "1003";

    /**
     * 号小车确认物料信息
     */
    public static final String undpAgvConfirm = "1002";

    //   扫描FRID 指令
    public static final String FRID_JSON = "1004";
    public static final String FRID_OLD = "1005";
    /**
     * 上发需要填写信息指令
     */
    public static final String QR_CODE_INOUT = "1400";
    /**
     * 下发二维码信息指令
     */
    public static final String QR_CODE_RECEIVE_OK = "1401";

    /**
     *  写入结果回复
     */
    public static final String QR_CODE_INOUT_OK = "1402";

    public static final String MA_ER_BIAO = "6";
}
