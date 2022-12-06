package com.dzics.data.appoint.changsha.mom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:mompath.properties", encoding = "utf-8")
public class MomRequestPath {
    @Value("${mom.ip}")
    public String ip;
    @Value("${mom.port}")
    public String port;
    @Value("${mom.ip.port}")
    public String ipPort;
    @Value("${mom.path}")
    public String path;
    @Value("${mom.ip.port.path}")
    public String ipPortPath;
}