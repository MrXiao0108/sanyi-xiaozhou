package com.dzics.data.appoint.changsha.mom.model.constant;

/**
 * @Classname TcpType
 * @Description tcp 指令类型订单
 * @Date 2022/7/15 16:06
 * @Created by NeverEnd
 */
public class TcpType {

    /**
     * DNC 获取获取程序号
     */
    public static final String TCP_DNC = "##R001";

    /**
     * 机器人 获取请求获取页面输入的二维码
     */
    public static final String TCP_QR_CODE = "##R002";

    /**
     * 机器人 AGV握手
     */
    public static final String TCP_AGV = "##R003";

    public static final String Packing_AGV = "##R004";
}
