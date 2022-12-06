package com.dzics.data.kanban.changsha.xiaozhou.jingjia.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.common.base.dto.GetToolInfoDataDo;
import com.dzics.data.common.base.dto.ToolDataDo;
import com.dzics.data.common.base.enums.DeviceSocketSendStatus;
import com.dzics.data.common.base.model.constant.SocketMessageType;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.kanban.changsha.xiaozhou.jingjia.model.proto.XiaoZhouJingKanBanProtobuf;
import com.dzics.data.pdm.model.dao.DzWorkpieceDataDo;
import com.dzics.data.pms.model.dto.TableColumnDto;
import com.dzics.data.pms.service.DzProductDetectionTemplateService;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.vo.DeviceLogsMsg;
import com.dzics.data.pub.service.CheckService;
import com.dzics.data.pub.service.Impl.SocketPushServiceImpl;
import com.dzics.data.pub.service.SocketPushToolService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname PushAnren
 * @Description 描述
 * @Date 2022/3/13 16:29
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class PushXiaoZhouJingJia extends SocketPushServiceImpl implements CheckService, SocketPushToolService {
    @Value("${cache.base.name}")
    private String cacheBaseName;
    @Autowired
    private DzProductDetectionTemplateService templateService;

    @Override
    public boolean sendWorkpieceData(Map dzWorkpieceData) {
        String orderNo = (String) dzWorkpieceData.get("orderNo");
        String lineNo = (String) dzWorkpieceData.get("lineNo");
        String eventKey = getEvent(SocketMessageType.TEST_ITEM_RECORD, orderNo, lineNo);
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            Map<String, Object> map = new HashMap<>();
            map.put("onlyKey", dzWorkpieceData.get("id"));
            map.put("name", dzWorkpieceData.get("name"));
            map.put("producBarcode", dzWorkpieceData.get("producBarcode"));
            map.put("outOk", dzWorkpieceData.get("outOk"));
            String format = DateUtil.dateFormatToStingYmdHms(new Date((Long) dzWorkpieceData.get("detectorTime")));
            map.put("detectorTime", format);
            String mrz = "9999.999";
            for (int i = 1; i < 29; i++) {
                String key = i < 10 ? "0" + i : i + "";
                Object val = dzWorkpieceData.get("detect" + key);
                if (StringUtils.isEmpty(val)) {
                    map.put("detect" + key, mrz);
                } else {
                    Integer okVal = (Integer) dzWorkpieceData.get("outOk" + key);
                    if (okVal != null && okVal.intValue() == 0) {
                        val = val + "::";
                    }
                    map.put("detect" + key, val);
                }
            }
            String productNo = (String) dzWorkpieceData.get("productNo");

            DzWorkpieceDataDo tableData = new DzWorkpieceDataDo();
            try {
                BeanUtils.populate(tableData, map);
            } catch (Throwable throwable) {
                log.error("类型转换错误:{}", throwable.getMessage(), throwable);
            }
            List<TableColumnDto> tableColumn = templateService.listProductNo(productNo, orderNo, lineNo);
            if (CollectionUtils.isEmpty(tableColumn)) {
                tableColumn = templateService.getDefoutDetectionTemp();
            }
            XiaoZhouJingKanBanProtobuf.CheckProductBase.Builder builder = XiaoZhouJingKanBanProtobuf.CheckProductBase.newBuilder();
            builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_DATA_TREND_SINGLE.getInfo());
            XiaoZhouJingKanBanProtobuf.ProDetectionAnren.Builder builder1 = XiaoZhouJingKanBanProtobuf.ProDetectionAnren.newBuilder();
            XiaoZhouJingKanBanProtobuf.TableData.Builder builder2 = XiaoZhouJingKanBanProtobuf.TableData.newBuilder();
            builder2.setDetect01(tableData.getDetect01());
            builder2.setDetect02(tableData.getDetect02());
            builder2.setDetect03(tableData.getDetect03());
            builder2.setDetect04(tableData.getDetect04());
            builder2.setDetect05(tableData.getDetect05());
            builder2.setDetect06(tableData.getDetect06());
            builder2.setDetect07(tableData.getDetect07());
            builder2.setDetect08(tableData.getDetect08());
            builder2.setDetect09(tableData.getDetect09());
            builder2.setDetect10(tableData.getDetect10());
            builder2.setDetect11(tableData.getDetect11());
            builder2.setDetect12(tableData.getDetect12());
            builder2.setDetect13(tableData.getDetect13());
            builder2.setDetect14(tableData.getDetect14());
            builder2.setDetect15(tableData.getDetect15());
            builder2.setDetect16(tableData.getDetect16());
            builder2.setDetect17(tableData.getDetect17());
            builder2.setDetect18(tableData.getDetect18());
            builder2.setDetect19(tableData.getDetect19());
            builder2.setDetect20(tableData.getDetect20());
            builder2.setDetect21(tableData.getDetect21());
            builder2.setDetect22(tableData.getDetect22());
            builder2.setDetect23(tableData.getDetect23());
            builder2.setDetect24(tableData.getDetect24());
            builder2.setDetect25(tableData.getDetect25());
            builder2.setDetect26(tableData.getDetect26());
            builder2.setDetect27(tableData.getDetect27());
            builder2.setDetect28(tableData.getDetect28());
            builder2.setProducBarcode(tableData.getProducBarcode());
            builder2.setName(tableData.getName());
            builder2.setDetectorTime(tableData.getDetectorTime());
            builder1.addTableData(builder2);
            for (TableColumnDto tableColumnDto : tableColumn) {
                XiaoZhouJingKanBanProtobuf.TableColumn.Builder builder3 = XiaoZhouJingKanBanProtobuf.TableColumn.newBuilder();
                builder3.setColData(tableColumnDto.getColData());
                builder3.setColName(tableColumnDto.getColName());
                builder3.setExhibition(tableColumnDto.getExhibition());
                builder1.addTableColumn(builder3);
            }
            builder.setData(builder1);
            byte[] bytes = builder.build().toByteArray();
            socketServerTemplate.sendMessage(SocketMessageType.TEST_ITEM_RECORD, eventKey, bytes);
        }
        return false;
    }

    @Override
    public boolean sendToolDetection(String msg) {
        GetToolInfoDataDo toolInfoDataDo = JSONObject.parseObject(msg, GetToolInfoDataDo.class);
        String orderNo = toolInfoDataDo.getOrderNo();
        String lineNo = toolInfoDataDo.getLineNo();
        try {
            String eventKey = getEvent(SocketMessageType.TOOL_TEST_DATA, orderNo, lineNo);
            boolean isSendEvend = getIsSendEvend(eventKey);
            if (isSendEvend) {
                XiaoZhouJingKanBanProtobuf.UpdateToolData.Builder builder = XiaoZhouJingKanBanProtobuf.UpdateToolData.newBuilder();
                XiaoZhouJingKanBanProtobuf.ToolData.Builder builder1 = XiaoZhouJingKanBanProtobuf.ToolData.newBuilder();
                builder1.setOrderNo(toolInfoDataDo.getOrderNo());
                builder1.setLineNo(toolInfoDataDo.getLineNo());
                builder1.setEquipmentId(toolInfoDataDo.getEquipmentId());
                builder1.setEquipmentName(toolInfoDataDo.getEquipmentName());
                builder1.setEquipmentNo(toolInfoDataDo.getEquipmentNo());
                List<ToolDataDo> toolDataList = toolInfoDataDo.getToolDataList();
                for (ToolDataDo toolDataDo : toolDataList) {
                    XiaoZhouJingKanBanProtobuf.ToolDataDo.Builder builder2 = XiaoZhouJingKanBanProtobuf.ToolDataDo.newBuilder();
                    builder2.setToolNo(toolDataDo.getToolNo());
                    builder2.setToolLifeCounter(toolDataDo.getToolLifeCounter());
                    builder2.setToolLife(toolDataDo.getToolLife());
                    builder1.addToolDataList(builder2);
                }
                builder.setToolData(builder1);
                builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_MACHINE_TOOL_INFORMATION_UPDATE.getInfo());
                byte[] bytes = builder.build().toByteArray();
                socketServerTemplate.sendMessage(SocketMessageType.TOOL_TEST_DATA, eventKey, bytes);
            }
            return true;
        } catch (Throwable e) {
            log.error("刀具发送到看板异常,msg:{}", msg, e);
            return false;
        }
    }

    @Override
    public void sendReatimLogs(DeviceLogsMsg b) {
        String eventKey = getEvent(SocketMessageType.DEVICE_LOG_WARN, b.getOrderCode(), b.getLineNo());
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = dateFormat.format(b.getTimestampTime());
            XiaoZhouJingKanBanProtobuf.InitRealTimeLogRes.Builder builder = XiaoZhouJingKanBanProtobuf.InitRealTimeLogRes.newBuilder();
            XiaoZhouJingKanBanProtobuf.RealTimeLogRes.Builder builder1 = XiaoZhouJingKanBanProtobuf.RealTimeLogRes.newBuilder();
            builder1.setClientId(b.getClientId());
            builder1.setMessage(b.getMessage());
            builder1.setRealTime(format);
            builder.addData(builder1);
            if (b.getMessageType() != null && b.getMessageType().intValue() == 1) {
                builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_REAL_TIME_LOG.getInfo());
                byte[] bytes = builder.build().toByteArray();
                socketServerTemplate.sendMessage(SocketMessageType.DEVICE_LOG, eventKey, bytes);
            } else {
                builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_ALARM_LOG.getInfo());
                byte[] bytes = builder.build().toByteArray();
                socketServerTemplate.sendMessage(SocketMessageType.DEVICE_LOG_WARN, eventKey, bytes);
            }

        }
    }

    @Override
    public synchronized void sendStateEquiment(DzEquipment dzEquipment) {
        boolean b = frequencyLimitation(cacheBaseName + ":PushZhanHui:" + dzEquipment.getId(), 3);
        if (b){
            String eventKey = getEvent(SocketMessageType.DEVICE_STATUS, dzEquipment.getOrderNo(), dzEquipment.getLineNo());
            boolean isSendEvend = getIsSendEvend(eventKey);
            if (isSendEvend) {
                String currentLocation = dzEquipment.getCurrentLocation();
                XiaoZhouJingKanBanProtobuf.MachiningMessageStatus.Builder status = XiaoZhouJingKanBanProtobuf.MachiningMessageStatus.newBuilder();
                if (!StringUtils.isEmpty(currentLocation)) {
                    String[] split = currentLocation.split(",");
                    if (split.length >= 3) {
                        String x = split[0];
                        if (!"0.000".equals(x)) {
                            status.setX(x);
                        }
                        String y = split[1];
                        if (!"0.000".equals(y)) {
                            status.setY(y);
                        }
                        String z = split[2];
                        if (!"0.000".equals(z)) {
                            status.setZ(z);
                        }
                    }
                }
                String deviceNum = dzEquipment.getEquipmentNo();
                Integer deviceType = dzEquipment.getEquipmentType();
                status.setEquimentId(dzEquipment.getId());
                status.setEquipmentNo(deviceNum);
                status.setEquipmentType(deviceType);
                status.setOperatorMode(dzEquipment.getOperatorMode());
                status.setConnectState(dzEquipment.getConnectState());
                status.setRunStatus(dzEquipment.getRunStatus());
                status.setSpeedRatio(dzEquipment.getSpeedRatio());
                status.setMachiningTime(dzEquipment.getMachiningTime());
                status.setAlarmStatus(dzEquipment.getAlarmStatus());
                status.setSpeedOfMainShaft(dzEquipment.getSpeedOfMainShaft());
                status.setSpeedRatio(dzEquipment.getSpeedRatio());
                status.setFeedSpeed(dzEquipment.getFeedSpeed());
                status.setSpindleLoad(dzEquipment.getSpindleLoad());
                XiaoZhouJingKanBanProtobuf.InitDeviceState.Builder builder = XiaoZhouJingKanBanProtobuf.InitDeviceState.newBuilder();
                builder.addStatus(status);
                builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_STATE.getInfo());
                byte[] bytes = builder.build().toByteArray();
                socketServerTemplate.sendMessage(SocketMessageType.DEVICE_STATUS, eventKey, bytes);
            }
        }
    }

}
