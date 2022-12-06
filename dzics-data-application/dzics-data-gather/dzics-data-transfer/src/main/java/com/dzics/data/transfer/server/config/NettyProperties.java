package com.dzics.data.transfer.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Netty ConfigurationProperties
 *
 * @author Jibeom Jung akka. Manty
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "netty")
public class NettyProperties {

    private int tcpPort;

    private int bossCount;

    private int workerCount;

    private boolean keepAlive;

    private int backlog;
}
