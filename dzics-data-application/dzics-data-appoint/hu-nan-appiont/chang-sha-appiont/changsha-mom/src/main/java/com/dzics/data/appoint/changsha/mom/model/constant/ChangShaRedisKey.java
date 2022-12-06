package com.dzics.data.appoint.changsha.mom.model.constant;

import com.dzics.data.common.base.model.constant.RedisKey;

/**
 * @Classname ChangShaRedisKey
 * @Description 描述
 * @Date 2022/7/11 17:47
 * @Created by NeverEnd
 */
public class ChangShaRedisKey extends RedisKey {
    public static final String MATERIAL_POINT_STATUS = "material:point:status:";
    public static final String MOM_HTTP_REQUEST_SERVICE_GET_MY_REQ_TYPE_ID = "momHttpRequestService:getMyReqTypeId:";

    /**
     * 机器人请求输入的二维码缓存中的标识
     */
    public static final String TCP_REQUEST_QR_CODE = "tcp:request:qr:code:";
    /**
     * 用户输入的二维码存储在redis中的key
     */
    public static final String TCP_READ_QR_CODE = "tcp:read:qr:code:";

    /*
     * MOM AGV握手处理
     */
    public static final String MOM_AVG_HANDSHAKE = "mom:agv:handshake:";

    /*
     * MOM AGV握手 机器人正在上料中
     */
    public static final String MOM_AVG_HANDSHAKE_ING = "mom:agv:handshake:ing";

    /*
     * 向MOM报工 开工请求id缓存
     */
    public static final String MOM_BAO_GONG_REQ_ID = "mom:bao-gong:req-id:";
}
