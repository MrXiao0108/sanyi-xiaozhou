package com.dzics.data.transfer.server.service.impl;

import com.dzics.common.util.core.time.DateUtil;
import com.dzics.data.transfer.dto.RabbitmqMessage;
import com.dzics.data.transfer.server.service.ProductPositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * @Classname ProductPositionServiceImpl
 * @Description 描述
 * @Date 2022/8/31 8:56
 * @Created by NeverEnd
 */
@Slf4j
@Service
public class ProductPositionServiceImpl implements ProductPositionService {

    @Autowired
    private AcqRabbitmq acqRabbitmq;

    @Override
    public boolean productPositionForRobot(String[] arr) {
        RabbitmqMessage msg = new RabbitmqMessage();
        String messageUUID = UUID.randomUUID().toString();
        msg.setMessageId(messageUUID.replace("-", ""));
        msg.setQueueName("dzics-dev-gather-v1-product-position");
        msg.setClientId("DZROBOT");
        msg.setOrderCode(arr[1]);
        msg.setLineNo(arr[2]);
        msg.setDeviceType(arr[3]);
        msg.setDeviceCode(arr[4]);
        msg.setMessage(arr[5]);
        String dateTime = DateUtil.toStr(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
        msg.setTimestamp(dateTime);
        acqRabbitmq.sendDataCenter("dzics-dev-gather-v1-routing-product-position", "dzics-dev-gather-v1-exchange-product-position", msg);
        log.info("ProductPositionServiceImpl [productPositionForRobot] msg{}", msg);
        return true;
    }
}
