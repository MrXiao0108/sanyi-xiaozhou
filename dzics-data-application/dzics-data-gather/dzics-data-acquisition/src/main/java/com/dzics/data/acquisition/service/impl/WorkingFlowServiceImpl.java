package com.dzics.data.acquisition.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.acquisition.service.WorkingFlowService;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.common.base.model.custom.EqMentStatus;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.common.base.model.dto.position.SendPosition;
import com.dzics.data.common.base.model.constant.QrCode;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.pub.model.entity.DzWorkStationManagement;
import com.dzics.data.pub.model.vo.OrderIdLineId;
import com.dzics.data.pub.service.DzProductionLineService;
import com.dzics.data.pub.service.DzWorkStationManagementService;
import com.dzics.data.wrp.model.entity.DzWorkingFlow;
import com.dzics.data.wrp.model.entity.DzWorkingFlowBig;
import com.dzics.data.wrp.service.DzWorkingFlowBigService;
import com.dzics.data.wrp.service.DzWorkingFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Date;

/**
 * @Classname WorkingFlowServiceImpl
 * @Description 描述
 * @Date 2022/3/16 10:31
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class WorkingFlowServiceImpl implements WorkingFlowService {
    @Autowired
    private DzProductionLineService lineService;
    @Autowired
    private DzWorkStationManagementService stationManagementService;
    @Autowired
    private DzWorkingFlowService workingFlowService;
    @Autowired
    private DzWorkingFlowBigService dzWorkingFlowBigService;

    /**
     * 处理工件位置数据
     *
     * @param rabbitmqMessage：消息实体
     * @return: 处理结果
     */
    @Override
    public SendPosition processingData(RabbitmqMessage rabbitmqMessage) {
        log.info("WorkingFlowServiceImpl [processingData] rabbitmqMessage{}", JSONObject.toJSONString(rabbitmqMessage));
        String deviceType = rabbitmqMessage.getDeviceType();
        String message = rabbitmqMessage.getMessage();
        String orderCode = rabbitmqMessage.getOrderCode();
        String deviceCode = rabbitmqMessage.getDeviceCode();
        String timestamp = rabbitmqMessage.getTimestamp();
        String lineNo = rabbitmqMessage.getLineNo();
        String[] check = checkParmsDeviceTypeMessage(message, deviceType);
        if (check == null) {
            log.error("WorkingFlowServiceImpl [processingData] 校验指令信息：失败");
            return null;
        }
        OrderIdLineId orderNoLineNoId = lineService.getOrderNoLineNoId(orderCode, lineNo);
        if (orderNoLineNoId == null) {
            log.error("WorkingFlowServiceImpl [processingData] orderNoLineNoId == null orderCode{}, lineNo{}",
                    orderCode, lineNo);
            return null;
        }
        String orderId = orderNoLineNoId.getOrderId();
        String lineId = orderNoLineNoId.getLineId();
        DzWorkStationManagement workStation = getWorkStation(deviceCode, orderId, lineId);
        if (workStation == null) {
            log.error("WorkingFlowServiceImpl [processingData] workStation == null deviceCode{}, orderId{}, lineId{}",
                    deviceCode, orderId, lineId);
            return null;
        }
        DzWorkStationManagement workStationSpare = null;
        if (!StringUtils.isEmpty(workStation.getMergeCode())) {
            workStationSpare = getWorkStation(orderId, lineId, deviceCode, workStation.getMergeCode(), orderCode, lineNo);
        }
        String outInputType = check[0];
        String qrcode = check[1];
        try {
            if (qrcode.contains("_")) {
                String[] s = qrcode.split("_");
                if (s.length == 2) {
                    qrcode = s[1];
                } else {
                    log.error("WorkingFlowServiceImpl [processingData] 解析二维码错误 订单：{},产线:{} ,code: {}", orderCode, lineNo, qrcode);
                }
            }
        } catch (Throwable throwable) {
            log.error("WorkingFlowServiceImpl [processingData] 处理报工数据处理二维码错误：{}", throwable.getMessage(), throwable);
        }
        return workFlow(orderNoLineNoId, workStation, timestamp, qrcode, outInputType, rabbitmqMessage.getDeviceCode(), workStationSpare);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public SendPosition workFlow(OrderIdLineId orderIdLineId,
                                 DzWorkStationManagement saMt,
                                 String timestamp,
                                 String qrCode,
                                 String outInputType,
                                 String deviceCode,
                                 DzWorkStationManagement workStationSpare) {
        String stationId = saMt.getStationId();
        String stationCode = saMt.getStationCode();
        String orderId = orderIdLineId.getOrderId();
        String lineId = orderIdLineId.getLineId();
        String orderNo = orderIdLineId.getOrderNo();
        String lienNo = orderIdLineId.getLienNo();
        DzWorkingFlow dzWorkingFlow = workingFlowService.getQrCodeStationCode(stationId, qrCode, orderId, lineId);
        Date date = DateUtil.stringDateToformatDate(timestamp);
        LocalDate nowLocalDate = DateUtil.dataToLocalDate(date);
        String workingProcedureId = saMt.getWorkingProcedureId();
        if (dzWorkingFlow == null) {
            // 插入工件当前位置
            dzWorkingFlow = new DzWorkingFlow();
            dzWorkingFlow.setLineId(lineId);
            dzWorkingFlow.setOrderId(orderId);
            // 获取工序id
            dzWorkingFlow.setWorkingProcedureId(workingProcedureId);
            dzWorkingFlow.setStationId(stationId);
            dzWorkingFlow.setQrCode(qrCode);
            dzWorkingFlow.setWorkDate(nowLocalDate);
            if (outInputType.equals(QrCode.QR_CODE_OUT)) {
                // 设置结束时间
                dzWorkingFlow.setCompleteTime(date);
                dzWorkingFlow.setRemarks(QrCode.RRAMARKS);
                workingFlowService.save(dzWorkingFlow);
                saveBig(qrCode, nowLocalDate, date, orderId, lineId);
                return getSendPosition(orderId, lineId, qrCode, orderNo, lienNo, outInputType, date, saMt, workStationSpare);
            } else if (outInputType.equals(QrCode.QR_CODE_IN)) {
                // 设置开始时间
                dzWorkingFlow.setStartTime(date);
                workingFlowService.save(dzWorkingFlow);
                saveBig(qrCode, nowLocalDate, date, orderId, lineId);
                return getSendPosition(orderId, lineId, qrCode, orderNo, lienNo, outInputType, date, saMt, workStationSpare);
            } else {
                log.warn("订单:{}, 产线: {} ,重置后工位: {},原始工位:{} 处理工件位置队列数据,获取到的进出指令类型错误：outInputType：{},应该是：1=去工位放料  2=去工位取料 ", orderNo, lienNo, stationCode, outInputType, deviceCode);
                return null;
            }
        } else {
            // 更新工件出去时间
            if (outInputType.equals(QrCode.QR_CODE_OUT)) {
                if (dzWorkingFlow.getCompleteTime() == null) {
                    dzWorkingFlow.setCompleteTime(date);
                    workingFlowService.updateByIdQrcode(dzWorkingFlow);
                    return getSendPosition(orderId, lineId, qrCode, orderNo, lienNo, outInputType, date, saMt, workStationSpare);
                } else {
                    log.warn("已存在该二维码:{} 的出去工位记录，不处理,订单: {},产线: {} ,重置后工位: {},原始工位:{} ", qrCode, orderNo, lienNo, stationCode, deviceCode);
                    return null;
                }
                // 设置结束时间
            } else if (outInputType.equals(QrCode.QR_CODE_IN)) {
                if (dzWorkingFlow.getStartTime() == null) {
                    dzWorkingFlow.setStartTime(date);
                    workingFlowService.updateByIdQrcode(dzWorkingFlow);
                    log.warn("已存在该二维码:{} 的进入工位记录，但是没有收到开始时间，先收到结束时间，后续才收到开始时间，处理, 订单: {} ,产线: {} ,重置后工位: {},原始工位:{} ", qrCode, orderNo, lienNo, stationCode, deviceCode);
                    return getSendPosition(orderId, lineId, qrCode, orderNo, lienNo, outInputType, date, saMt, workStationSpare);
                } else {
                    log.warn("已存在该二维码:{} 的进入工位记录，该信号重复，不做处理,订单: {}, 产线: {} ,重置后工位: {},原始工位:{}  ", qrCode, orderNo, lienNo, stationCode, deviceCode);
                }
                return null;
            } else {
                log.warn("订单:{}, 产线: {} ,重置后工位: {} ,原始工位:{} 处理工件位置队列数据,获取到的进出指令类型错误：outInputType：{},应该是：1=去工位放料  2=去工位取料 ", orderNo, lienNo, stationCode, outInputType, deviceCode);
                return null;
            }
        }
    }

    private SendPosition getSendPosition(String orderId, String lineId, String qrCode, String orderNo, String lienNo, String outInputType, Date date, DzWorkStationManagement saMt, DzWorkStationManagement workStationSpare) {
        SendPosition position = new SendPosition();
        position.setOrderId(orderId);
        position.setLineId(lineId);
        position.setQrCode(qrCode);
        position.setOrderNo(orderNo);
        position.setLineNo(lienNo);
        position.setOutInputType(outInputType);
        position.setProductionTime(date);
        position.setStationJsonA(JSONObject.toJSONString(saMt));
        position.setStationJsonB(JSONObject.toJSONString(workStationSpare));
        return position;
    }


    /**
     * 不存在该二维码则插入
     *
     * @param qrCode
     * @param nowLocalDate
     * @param workTime
     * @param orderId
     * @param lineId
     */
    @Override
    public void saveBig(String qrCode, LocalDate nowLocalDate, Date workTime, String orderId, String lineId) {
        QueryWrapper<DzWorkingFlowBig> wp = new QueryWrapper<>();
        wp.eq("qr_code", qrCode);
        wp.eq("order_id", orderId);
        wp.eq("line_id", lineId);
        DzWorkingFlowBig one = dzWorkingFlowBigService.getOne(wp);
        if (one != null) {
            return;
        }
        DzWorkingFlowBig big = new DzWorkingFlowBig();
        big.setLineId(lineId);
        big.setOrderId(orderId);
        big.setQrCode(qrCode);
        big.setWorkDate(nowLocalDate);
        big.setWorkTime(workTime);
        dzWorkingFlowBigService.save(big);
    }


    /**
     * 校验指令信息，设备类型是否正确
     *
     * @param message
     * @param deviceType
     * @return 返回指令处理后信息
     */
    private String[] checkParmsDeviceTypeMessage(String message, String deviceType) {
        if (!StringUtils.isEmpty(message)) {
            String[] split = message.split("\\|");
            if (split.length < 2) {
                return null;
            } else {
                String cmd = split[0];
                if (cmd.equals(EqMentStatus.CMD_ROB_QRCODE_TRACE)) {
                    String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(split[1], "[");
                    deviceItemValue = deviceItemValue.substring(0, deviceItemValue.length() - 1);
                    String[] msg = deviceItemValue.split(",");
                    if (msg.length < 2) {
                        if (msg.length == 1) {
                            String[] msgx = new String[2];
                            msgx[0] = msg[0];
                            msgx[1] = "";
                            return msgx;
                        }
                        log.warn("处理工件位置队列数据,指令信息错误：根据，号分号后长度应该>=2，device_item_value：{}", deviceItemValue);
                        return null;
                    } else {
                        if (deviceType.equals(String.valueOf(EquiTypeEnum.EQCODE.getCode()))) {
                            return msg;
                        } else {
                            log.warn("设备类型错误：{} 不是检测工件位置的设备，设备类型应该是：6", deviceType);
                            return null;
                        }
                    }
                } else {
                    log.warn("处理工件位置队列数据，数据中指令错误：CMD：{}", cmd);
                    return null;
                }
            }
        } else {
            log.error("处理工件位置队列数据，数据错误没有指令数据内容：message：{}", message);
            return null;
        }
    }

    /**
     * 校验工位信息是否存在
     *
     * @param deviceCode
     * @param orderId
     * @param lineId
     * @return
     */
    private DzWorkStationManagement getWorkStation(String deviceCode, String orderId, String lineId) {
        return stationManagementService.getWorkStationCode(deviceCode, orderId, lineId);
    }

    private DzWorkStationManagement getWorkStation(String orderId, String lineId, String deviceCode, String mergeCode, String orderCode, String lineNo) {
        try {
            return stationManagementService.getStationIdMergeCode(orderId, lineId, deviceCode, mergeCode);
        } catch (Throwable throwable) {
            log.error("查找备用工位失败订单: {},产线: {} 工位:{},合并标识：{},错误信息：{}", orderCode, lineNo, deviceCode, mergeCode, throwable.getMessage(), throwable);
        }
        return null;
    }

}
