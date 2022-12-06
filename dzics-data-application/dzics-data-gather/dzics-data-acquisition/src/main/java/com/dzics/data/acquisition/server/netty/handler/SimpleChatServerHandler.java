package com.dzics.data.acquisition.server.netty.handler;

import com.dzics.data.acquisition.server.TcpCodeEnum;
import com.dzics.data.acquisition.server.domain.ChannelRepository;
import com.dzics.data.acquisition.server.domain.User;
import com.dzics.data.acquisition.service.ProductPositionService;
import com.dzics.data.redis.util.RedisUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * event handler to process receiving messages
 *
 * @author Jibeom Jung akka. Manty
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class SimpleChatServerHandler extends ChannelInboundHandlerAdapter {

    private final ChannelRepository channelRepository;
    @Autowired
    private RedisUtil<String> redisUtil;
    @Autowired
    private ProductPositionService productPositionService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Assert.notNull(this.channelRepository, "[Assertion failed] - ChannelRepository is required; it must not be null");
        String socketAddress = ctx.channel().remoteAddress().toString().replace("/", "").split(":")[0];
        if (log.isInfoEnabled()) {
            log.info("有新连接接入:{}", socketAddress);
        }
        String stringMessage = (String) "login " + socketAddress;
        if (log.isDebugEnabled()) {
            log.debug(stringMessage);
        }
        User user = User.of(stringMessage, ctx.channel());
        user.login(channelRepository, ctx.channel());
        ctx.fireChannelActive();
        if (log.isInfoEnabled()) {
            log.info("Bound Channel Count is {}", this.channelRepository.size());
        }
    }

    /**
     * 数据类型
     *
     * @param ctx 客户端
     * @param msg ##R001;1973,A2;@# 获取程序号
     *            ##R002;1973,X;@# 请求获取页面输入的二维码
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            String stringMessage = (String) msg;
            log.info("SimpleChatServerHandler [channelRead] 接收到数据-->:{}", stringMessage);
            if (StringUtils.isEmpty(stringMessage)) {
                return;
            }
            String ipPort = ctx.channel().remoteAddress().toString().replace("/", "");
            String socketAddress = ipPort.substring(0, ipPort.indexOf(":"));
            String[] split = stringMessage.split(";");
            String type = split[0];
            String[] split1 = split[1].split(",");
            if (TcpCodeEnum.BAO_GONG.val().equals(type)) {
                boolean result = productPositionService.productPositionForRobot(split);
                if (result) {
                    String message = "##P01;1;@#";
                    Channel channel = channelRepository.get(socketAddress);
                    channel.writeAndFlush(message);
                    log.info("SimpleChatServerHandler [channelRead] ##P01 result {}", message);
                }
            }
            if (TcpCodeEnum.BAO_GONG_TEMP_CODE.val().equals(type)) {
                boolean result = productPositionService.baoGongTempCode(split);
                if (result) {
                    String message = "##P02;1;@#";
                    Channel channel = channelRepository.get(socketAddress);
                    channel.writeAndFlush(message);
                    log.info("SimpleChatServerHandler [channelRead] ##P02 result {}", message);
                }
            }
        } catch (Throwable throwable) {
            log.error("接收到数据处理异常-->:{}", throwable.getMessage(), throwable);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("发生错误:{}", cause.getMessage(), cause);
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Assert.notNull(this.channelRepository, "[Assertion failed] - ChannelRepository is required; it must not be null");
        Assert.notNull(ctx, "[Assertion failed] - ChannelHandlerContext is required; it must not be null");
        User.current(ctx.channel()).logout(this.channelRepository, ctx.channel());
        if (log.isDebugEnabled()) {
            log.debug("Channel Count is " + this.channelRepository.size());
        }
    }

    /**
     * 字符串转换为Ascii
     *
     * @param value
     * @return
     */
    public static String stringToAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sbu.append((int) chars[i]);
        }
        return sbu.toString();
    }

    /**
     * Ascii转换为字符串
     *
     * @param value
     * @return
     */
    public static String asciiToString(String value) {
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }
}
