package com.dzics.data.appoint.changsha.mom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 读取dnc请求下载程序接口地址配置
 * @author LiuDongFei
 * @date 2022年06月22日 11:42
 */
@Component
@PropertySource(value = "classpath:dncpath.properties",encoding = "utf-8")
public class DncRequestPath {
    @Value("${dnc.ip}")
    public String ip;

    @Value("${dnc.port}")
    public String port;

    @Value("${dnc.ip.port}")
    public String ipPort;

    @Value("${dnc.path}")
    public String path;

    @Value("${dnc.ip.port.path}")
    public String ipPortPath;


    public Object getRequestPath() {

        return ipPortPath;
    }
}
