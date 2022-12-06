package com.dzics.data.appoint.changsha.mom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * mom交互请求地址
 *
 * @author ZhangChengJun
 * Date 2021/6/16.
 * @since
 */
@Component
@PropertySource(value = "classpath:wmspath.properties", encoding = "utf-8")
public class WmsRequestPath {
    @Value("${wms.ip}")
    public String ip;
    @Value("${wms.port}")
    public String port;
    @Value("${wms.ip.port}")
    public String ipPort;
    @Value("${wms.path}")
    public String path;
    @Value("${wms.ip.port.path}")
    public String ipPortPath;

    /**
     * 接口名
     */
    //呼叫料框
    @Value("${wms.call_prame}")
    public String CALL_FRAME;
    //机械手放货位置申请
    @Value("${wms.location_request}")
    public String LOCATION_REQUEST;
    //订单完成信号
    @Value("${wms.order_completed}")
    public String ORDER_COMPLETED;
    //放料完成
    @Value("${wms.put_complete}")
    public String PUT_COMPLETED;
}
