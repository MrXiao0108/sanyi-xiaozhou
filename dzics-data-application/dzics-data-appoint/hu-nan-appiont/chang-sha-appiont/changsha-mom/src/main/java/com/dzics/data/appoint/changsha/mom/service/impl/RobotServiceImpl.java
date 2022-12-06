package com.dzics.data.appoint.changsha.mom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.appoint.changsha.mom.service.MomOrderService;
import com.dzics.data.appoint.changsha.mom.service.RobotService;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.DzUdpType;
import com.dzics.data.pms.model.entity.DzProduct;
import com.dzics.data.pms.service.DzProductService;
import com.dzics.data.pub.model.entity.DzOrder;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.service.DzOrderService;
import com.dzics.data.pub.service.DzProductionLineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author: van
 * @since: 2022-07-01
 */
@Slf4j
@Service
public class RobotServiceImpl implements RobotService {

    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private DzProductService dzProductService;
    @Autowired
    private DzOrderService dzOrderService;
    @Autowired
    private DzProductionLineService dzProductionLineService;
    /*@Autowired
    private MomMaterialPointService momMaterialPointService;*/

    @Override
    public void sendControCmdRob(String proTaskOrderId, String typeContro) {
        /*try {
            MomOrder byId = momOrderService.getById(proTaskOrderId);
            DzProduct dzProduct = dzProductService.getById(byId.getProductId());
            if (dzProduct == null) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR48);
            }
            String wiporderno = byId.getWiporderno();
            String productAliasProductionLine = byId.getProductAliasProductionLine();
            DzOrder order = dzOrderService.getById(byId.getOrderId());
            DzProductionLine line = dzProductionLineService.getById(byId.getLineId());
            if (order == null || line == null) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR48);
            }
            String orderId = line.getOrderId();
            String id = line.getId();
            String workStation = pointService.getNextPoint(orderId, id);
            String nextOprSeqNo = "0080";
            String orderNo = order.getOrderNo();
            String lineNo = line.getLineNo();
            try {
                String sequenceNo = waitCallMaterialService.getOprSequenceNo(workStation, byId.getProTaskOrderId());
                SearchDzdcMomSeqenceNo seqenceNo = new SearchDzdcMomSeqenceNo();
                seqenceNo.setWipOrderNo(wiporderno);
                seqenceNo.setOprSequenceNo(sequenceNo);
                seqenceNo.setOrderCode(orderNo);
                seqenceNo.setLineNo(lineNo);
                MomResultSearch sanyMomNextSpecNo = agvService.getSanyMomNextSpecNo(seqenceNo);
                nextOprSeqNo = sanyMomNextSpecNo.getReturnData().getNextOprSeqNo();
            } catch (Throwable throwable) {
                log.error("请求MOM 没有查询岛下个工序的工序号: {}", nextOprSeqNo);
            }
            SysRealTimeLogs timeLogs = new SysRealTimeLogs();
            timeLogs.setMessageId(UUID.randomUUID().toString().replace("-", ""));
            timeLogs.setQueueName(logQuery);
            timeLogs.setClientId(LogClientType.BUS_AGV);
            timeLogs.setOrderCode(orderNo);
            timeLogs.setLineNo(lineNo);
            timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
            timeLogs.setDeviceCode(FinalCode.Device_Code);
            timeLogs.setMessageType(1);
            if (DzUdpType.CONTROL_STAR.equals(typeContro)) {
                timeLogs.setMessage("点击订单：" + wiporderno + " 开始执行");
            }
            if (DzUdpType.CONTROL_STAR_STOP.equals(typeContro)) {
                timeLogs.setMessage("点击订单：" + wiporderno + " 暂停");
            }
            if (DzUdpType.CONTROL_STOP.equals(typeContro)) {
                timeLogs.setMessage("点击订单：" + wiporderno + " 强制关闭");
            }
            if (DzUdpType.CONTROL_STAR_STOP_START.equals(typeContro)) {
                timeLogs.setMessage("点击订单：" + wiporderno + " 恢复执行");
            }
            timeLogs.setTimestampTime(new Date());
            //     发送到日志队列
            boolean rab = rabbitmqService.sendRabbitmqLog(JSONObject.toJSONString(timeLogs));
            //      将信号发送到 mq 触发到 UDP 发送
            Map<String, String> mapIps = mapConfig.getMaps();
            String plcIp = mapIps.get(orderNo + lineNo);
            if (CollectionUtils.isNotEmpty(mapIps) && !StringUtils.isEmpty(plcIp)) {
                SendPlcModel sendPlcModel = new SendPlcModel();
                sendPlcModel.setIp(plcIp);
                sendPlcModel.setPort(plcPort);
                String msx = "Q," + DzUdpType.udpCmdControl + "," + DzUdpType.udpCmdControlInner + "," + typeContro + "," + wiporderno + "," + orderNo + "," + lineNo
                        + "," + productAliasProductionLine + "," + nextOprSeqNo + "," + byId.getProductNo() + "," + byId.getQuantity()
                        + "," + dzProduct.getProductNo();
                sendPlcModel.setMessage(msx);
                log.info("下发订单指令信息:{}", msx);
                rabbitmqService.sendQrCodeMqUdp(JSONObject.toJSONString(sendPlcModel));
                rabbitmqService.sendMsgOrder(JSONObject.toJSONString(byId));
            } else {
                log.error("下发订单指令发送到UDP订单: {} ,产线: {} 配置的IP不存在mapIps: {} ", lineNo, orderNo, mapIps);
            }
        } catch (Throwable throwable) {
            log.error("操作订单状态异常:{} ", throwable.getMessage(), throwable);
            throw throwable;
        }*/
    }
}
