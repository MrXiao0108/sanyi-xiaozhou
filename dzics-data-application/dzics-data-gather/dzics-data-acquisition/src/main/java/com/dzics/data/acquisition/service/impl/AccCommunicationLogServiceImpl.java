package com.dzics.data.acquisition.service.impl;

import com.dzics.data.common.base.model.dto.CmdTcp;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.acquisition.service.AccCommunicationLogService;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.logms.model.entity.SysCommunicationLog;
import com.dzics.data.logms.service.SysCommunicationLogService;
import com.dzics.data.pub.model.entity.DzDataCollection;
import com.dzics.data.pub.service.DzDataCollectionService;
import com.dzics.data.pub.service.DzEquipmentService;
import com.dzics.data.pub.util.TcpStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZhangChengJun
 * Date 2021/3/8.
 * @since
 */
@Slf4j
@Service
public class AccCommunicationLogServiceImpl implements AccCommunicationLogService {
    @Autowired
    private SysCommunicationLogService sysCommunicationLogService;

    @Autowired
    private DzDataCollectionService dataCollectionService;

    @Autowired
    private DzEquipmentService dzEquipmentService;

    @Autowired
    private TcpStringUtil tcpStringUtil;

    @Async("asyncTaskExecutor")
    @Override
    public void saveRabbitmqMessage(RabbitmqMessage rabbitmqMessage, boolean refTable, boolean isSavelog) {
        Date date = DateUtil.stringDateToformatDate(rabbitmqMessage.getTimestamp());
        if (isSavelog){
            try {
                SysCommunicationLog communicationLog = new SysCommunicationLog();
                communicationLog.setMessageId(rabbitmqMessage.getMessageId());
                communicationLog.setQueueName(rabbitmqMessage.getQueueName());
                communicationLog.setClientId(rabbitmqMessage.getClientId());
                communicationLog.setOrderCode(rabbitmqMessage.getOrderCode());
                communicationLog.setLineNo(rabbitmqMessage.getLineNo());
                communicationLog.setDeviceType(rabbitmqMessage.getDeviceType());
                communicationLog.setDeviceCode(rabbitmqMessage.getDeviceCode());
                communicationLog.setMessage(rabbitmqMessage.getMessage());
                communicationLog.setOkCheck(rabbitmqMessage.getCheck());
                communicationLog.setSendTimestamp(date);
                sysCommunicationLogService.save(communicationLog);
            } catch (Throwable throwable) {
                log.error("保存上发指令数据错误：{}", throwable.getMessage(), throwable);
            }
        }

        if (refTable){
            try {
//       更新采集数据指令数据
                String deviceType = rabbitmqMessage.getDeviceType();
                String lineNo = rabbitmqMessage.getLineNo();
                String orderCode = rabbitmqMessage.getOrderCode();
                String deviceCode = rabbitmqMessage.getDeviceCode();
                String deviceId = dzEquipmentService.getDeviceId(orderCode, lineNo, deviceCode, deviceType);
                if (deviceId != null) {
                    DzDataCollection dzDataCollection = dataCollectionService.cacheDeviceId(deviceId);
                    String message = rabbitmqMessage.getMessage();
                    long time = date.getTime();
                    List<CmdTcp> cmdTcp = tcpStringUtil.getCmdTcp(message);
                    Map<String, Object> cmdMap = cmdTcpCurMap(cmdTcp, time);
                    DzDataCollection detectorItem = new DzDataCollection();
                    BeanUtils.populate(detectorItem, cmdMap);
                    if (dzDataCollection != null) {
                        detectorItem.setDeviceId(dzDataCollection.getDeviceId());
                        DzDataCollection b = dataCollectionService.updateDeviceId(detectorItem);
                    } else {
                        detectorItem.setDeviceId(deviceId);
                        detectorItem.setDelFlag(false);
                        boolean instert = dataCollectionService.instert(detectorItem);
                        log.warn("设备不存在指令数据,插入 deviceId: {},:detectorItem :{}", deviceId, detectorItem);
                    }
                } else {
                    log.warn("根据订单号：{},产线序号:{},设备编号: {},设备类型: {} 无法获取到设备ID : {}", orderCode, lineNo, deviceCode, deviceType);
                }

            } catch (Throwable e) {
                log.error("更新设备指令数据异常:{}", e.getMessage(), e);
            }
        }
    }


    private Map<String, Object> cmdTcpCurMap(List<CmdTcp> cmdTcp, long time) {
        Map<String, Object> map = new HashMap<>();
        for (CmdTcp tcp : cmdTcp) {
            String tcpValue = tcp.getTcpValue();
            String mpK = tcpValue.substring(0, 1).toLowerCase() + tcpValue.substring(1);
            map.put(mpK, tcp.getDeviceItemValue());
            map.put(MpKCmdBase.basK + mpK, time);
        }
        return map;
    }

    private class MpKCmdBase {
        public static final String basK = "d";
    }
}
