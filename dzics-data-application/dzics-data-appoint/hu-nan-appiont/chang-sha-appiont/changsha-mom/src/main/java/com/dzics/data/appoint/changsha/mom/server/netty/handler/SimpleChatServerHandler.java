package com.dzics.data.appoint.changsha.mom.server.netty.handler;

import com.dzics.data.appoint.changsha.mom.model.constant.ChangShaRedisKey;
import com.dzics.data.appoint.changsha.mom.model.constant.MomConstant;
import com.dzics.data.appoint.changsha.mom.model.constant.TcpType;
import com.dzics.data.appoint.changsha.mom.model.entity.DncProgram;
import com.dzics.data.appoint.changsha.mom.model.vo.MomQrCodeVo;
import com.dzics.data.appoint.changsha.mom.server.domain.ChannelRepository;
import com.dzics.data.appoint.changsha.mom.server.domain.User;
import com.dzics.data.appoint.changsha.mom.service.DncProgramService;
import com.dzics.data.appoint.changsha.mom.service.MOMAGVService;
import com.dzics.data.appoint.changsha.mom.service.MomPackingPointMaterialsService;
import com.dzics.data.common.base.model.constant.DzUdpType;
import com.dzics.data.redis.util.RedisUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Value("${order.code}")
    private String orderCode;
    @Autowired
    private DncProgramService dncProgramService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MOMAGVService momagvService;
    @Autowired
    private MomPackingPointMaterialsService momPackingPointMaterialsService;

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
            if (TcpType.TCP_DNC.equals(type)) {
                Map<String, String> map = new HashMap<>();
                map.put("ip", socketAddress);
                map.put("orderNo", split1[0]);
                map.put("equipmentNo", split1[1]);
                DncProgram dncProgram = dncProgramService.dncProgramHandel(map);
                Channel channel = channelRepository.get(socketAddress);
                String message = "##R001;" + dncProgram.getRunProgramname() + ";@#";
                log.info("响应的数据-->:{}", message);
                channel.writeAndFlush(message);
                return;
            }
            if (TcpType.TCP_QR_CODE.equals(type)) {
                String orderNo = split1[0];
                String tempCode = split1[1];
                String qrcode = "";
                boolean flg = true;
//                设置缓存请求标识
                String reqKey = ChangShaRedisKey.TCP_REQUEST_QR_CODE + "DZ-" + orderNo;
                String readKey = ChangShaRedisKey.TCP_READ_QR_CODE + "DZ-" + orderNo;
                redisUtil.set(reqKey,tempCode);
                redisUtil.del(readKey);
                while (flg) {
                    qrcode = (String) redisUtil.get(readKey);
                    if (!StringUtils.isEmpty(qrcode)) {
                        flg = false;
                        redisUtil.del(readKey, reqKey);
                    } else {
                        Thread.sleep(500);
                    }
                }
                String message = "##R002;" + orderNo + "," + qrcode + ";@#";
                Channel channel = channelRepository.get(socketAddress);
                channel.writeAndFlush(message);
                log.info("响应的数据-->:{}", message);
                return;
            }
            if (TcpType.TCP_AGV.equals(type)) {
                String message = momagvService.handshakeTcpHandle(stringMessage);
                Channel channel = channelRepository.get(socketAddress);
                channel.writeAndFlush(message);
                log.info("响应的数据-->:{}", message);
                return;
            }
            //粗车 装框socket机器人交互
            //##R004;XZJC25;0;W61B-3-220611-0063;860007076820
            if(TcpType.Packing_AGV.equals(type)){
                if(MomConstant.ORDER_DZ_1972.equals(orderCode) || MomConstant.ORDER_DZ_1973.equals(orderCode)){
                    String point = split[1];
                    String statue = split[2];
                    if(StringUtils.isEmpty(point) || StringUtils.isEmpty(statue)){
                        String message = "##R004数据解析异常;料点编码或者状态编码为空";
                        Channel channel = channelRepository.get(socketAddress);
                        channel.writeAndFlush(message);
                        log.info("响应的数据-->:{}", message);
                        return;
                    }
                    List<MomQrCodeVo> list = new ArrayList<>();
                    if(redisUtil.hasKey(orderCode + type + point)){
                        list = redisUtil.lGet(orderCode + type + point,0,-1);
                    }
                    //如果是0，新增当前料框缓存信息
                    if(DzUdpType.err.equals(statue)){
                        String qrCode = split[3];
                        String momOrder = split[4];
                        if(StringUtils.isEmpty(qrCode) || StringUtils.isEmpty(momOrder)){
                            String message = "##R004数据解析异常;Mom订单号或者二维码为空";
                            Channel channel = channelRepository.get(socketAddress);
                            channel.writeAndFlush(message);
                            log.info("响应的数据-->:{}", message);
                            return;
                        }
                        MomQrCodeVo momQrCodeVo = new MomQrCodeVo();
                        momQrCodeVo.setQrCode(qrCode);
                        momQrCodeVo.setWorkOrderNo(momOrder);
                        momQrCodeVo.setOrderQty("1");
                        list.add(momQrCodeVo);
                        //清除原有缓存
                        redisUtil.del(orderCode + type + point);
                        redisUtil.lSet(orderCode + type + point,list);
                        return;
                    }
                    //如果是1，当前料框缓存保存数据库，清空当前料框信息缓存
                    if(DzUdpType.ok.equals(statue)){
                        log.info("SimpleChatServerHandler [channelRead] 料框{}装框完成，最终物料信息：{}，缓存信息重置-------------",point,list);
                        //redis copy一份，为了后续使用
                        redisUtil.lSet(orderCode + type + point + "copy",list);
                        //存储当前缓存的二维码集合信息到数据库，然后清除
                        redisUtil.del(orderCode + type + point);
                        momPackingPointMaterialsService.handlePackingData(point);
                    }
                }
            }
        } catch (Throwable throwable) {
            log.error("接收到数据处理异常-->:{}", throwable.getMessage(), throwable);
        }
    }

    /**
     * 数据校验方法,预留
     */
    public void msgCheck() {

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
