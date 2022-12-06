package com.dzics.data.acquisition.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.acquisition.config.datareceive.PositionConfig;
import com.dzics.data.acquisition.service.ProductPositionService;
import com.dzics.data.common.base.model.constant.QrCode;
import com.dzics.data.common.base.model.constant.redis.lock.RedisLockKey;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.common.base.model.dto.position.SendPosition;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

/**
 * @Classname ProductPositionServiceImpl
 * @Description 描述
 * @Date 2022/8/24 14:22
 * @Created by NeverEnd
 */
@Slf4j
@Service
public class ProductPositionServiceImpl implements ProductPositionService {

    @Autowired
    private AcqRabbitmq acqRabbitmq;
    @Value("${accq.product.position.routing}")
    private String positionRouting;
    @Value("${accq.product.position.exchange}")
    private String positionExchange;
    @Autowired
    private RedisUtil<String> redisUtil;

    @Override
    public boolean productPositionForRobot(String[] arr) {
        RabbitmqMessage msg = new RabbitmqMessage();
        String messageUUID = UUID.randomUUID().toString();
        msg.setMessageId(messageUUID.replace("-", ""));
        msg.setQueueName("dzics-dev-gather-v1-product-position");
        msg.setClientId("DZROBOT");
        msg.setOrderCode("DZ-" + arr[1]);
        msg.setLineNo(arr[2]);
        msg.setDeviceType(arr[3]);
        msg.setDeviceCode(arr[4]);
        msg.setMessage(arr[5]);
        String dateTime = DateUtil.toStr(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
        msg.setTimestamp(dateTime);
        acqRabbitmq.sendDataCenter(positionRouting, positionExchange, msg);
        log.info("ProductPositionServiceImpl [productPositionForRobot] msg{}", msg);
        return true;
    }

    @Override
    public boolean baoGongTempCode(String[] arr) {
        RabbitmqMessage msg = new RabbitmqMessage();
        String messageUUID = UUID.randomUUID().toString();
        msg.setMessageId(messageUUID.replace("-", ""));
        msg.setQueueName(PositionConfig.BAO_GONG_TEMP_CODE_QUEUE);
        msg.setClientId("DZROBOT");
        msg.setOrderCode("DZ-" + arr[1]);
        msg.setLineNo(arr[2]);
        msg.setDeviceType(arr[3]);
        msg.setDeviceCode(arr[4]);
        msg.setMessage(arr[5]);
        String dateTime = DateUtil.toStr(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
        msg.setTimestamp(dateTime);
        acqRabbitmq.sendDataCenter(PositionConfig.BAO_GONG_TEMP_CODE_ROUTING, positionExchange, msg);
        log.info("ProductPositionServiceImpl [baoGongTempCode] msg{}", msg);
        return true;
    }

    @Override
    public void baoGongTempCodeWrapper(RabbitmqMessage message) {
        String msg = message.getMessage();
        if (!StringUtils.hasText(msg)) {
            return;
        }
        message.setQueueName("dzics-dev-gather-v1-product-position");
        String type = "";
        String code = "";
        String tempCode = "";
        try {
            String str = msg.substring(6, msg.length() - 1);
            String[] split = str.split(",");
            type = split[0];
            code = split[1];
            tempCode = split[2];
            String key = "dzics:acquisition:" + tempCode;
            if (QrCode.QR_CODE_IN.equals(type)) {
                redisUtil.set(key, JSONObject.toJSONString(message));
            }
            if (QrCode.QR_CODE_OUT.equals(type)) {
                String tempMessageJsonStr = redisUtil.get(key);
                RabbitmqMessage tempMessage = JSONObject.parseObject(tempMessageJsonStr, RabbitmqMessage.class);
                String inMsg = "A815|[1," + code + "]";
                tempMessage.setMessage(inMsg);
                boolean inResult = acqRabbitmq.sendDataCenter(positionRouting, positionExchange, tempMessage);
                if (!inResult) {
                    log.info("ProductPositionServiceImpl [baoGongTempCodeWrapper] !inResult");
                }
                String outMsg = "A815|[2," + code + "]";
                message.setMessage(outMsg);
                boolean outResult = acqRabbitmq.sendDataCenter(positionRouting, positionExchange, message);
                if (!outResult) {
                    log.info("ProductPositionServiceImpl [baoGongTempCodeWrapper] !outResult");
                }
            }
        } catch (Exception e) {
            log.error("ProductPositionServiceImpl [baoGongTempCodeWrapper] fail e{}", e.getMessage());
        }
    }
}
