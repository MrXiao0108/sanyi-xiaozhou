package com.dzics.data.appoint.changsha.mom.config.param;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "momreqsys")
public class MOMReqSys {

    /**
     * 订单对应的reqSys
     */
    private Map<String, String> maps;
}
