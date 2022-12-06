package com.dzics.data.kanban.changsha.changpaoguang.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.common.base.dto.GetToolInfoDataDo;
import com.dzics.data.common.base.dto.ToolDataDo;
import com.dzics.data.common.base.enums.DeviceSocketSendStatus;
import com.dzics.data.common.base.model.custom.JCEquimentBase;
import com.dzics.data.common.base.model.constant.SocketMessageType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.kanban.changsha.changpaoguang.model.proto.PaoGuangKanBanProtobuf;
import com.dzics.data.kanban.changsha.changpaoguang.vo.ProDetectionPaoGuang;
import com.dzics.data.pms.model.dto.TableColumnDto;
import com.dzics.data.pms.service.DzProductDetectionTemplateService;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.vo.DeviceLogsMsg;
import com.dzics.data.pub.service.CheckService;
import com.dzics.data.pub.service.Impl.SocketPushServiceImpl;
import com.dzics.data.pub.service.SocketPushToolService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
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
 * @Classname PushPaoGuang
 * @Description 描述
 * @Date 2022/3/13 16:29
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class PushPaoGuang extends SocketPushServiceImpl implements CheckService, SocketPushToolService {
    @Autowired
    private DzProductDetectionTemplateService templateService;
    @Autowired
    private RedisUtil redisUtil;

    @Value("${cache.base.name}")
    private String cacheBaseName;

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
            List<TableColumnDto> templates = templateService.listProductNo(productNo, orderNo, lineNo);
            if (CollectionUtils.isEmpty(templates)) {
                templates = templateService.getDefoutDetectionTemp();
            }
            ProDetectionPaoGuang proDetectionPaoGuang = new ProDetectionPaoGuang();
            proDetectionPaoGuang.setTableColumn(templates);
            proDetectionPaoGuang.setTableData(map);
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setData(proDetectionPaoGuang);
            jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_DATA_TREND_SINGLE.getInfo());
            Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
            socketServerTemplate.sendMessage(SocketMessageType.TEST_ITEM_RECORD, eventKey, ok);
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
                PaoGuangKanBanProtobuf.UpdateToolData.Builder builder = PaoGuangKanBanProtobuf.UpdateToolData.newBuilder();
                PaoGuangKanBanProtobuf.ToolData.Builder builder1 = PaoGuangKanBanProtobuf.ToolData.newBuilder();
                builder1.setOrderNo(toolInfoDataDo.getOrderNo());
                builder1.setLineNo(toolInfoDataDo.getLineNo());
                builder1.setEquipmentId(toolInfoDataDo.getEquipmentId());
                builder1.setEquipmentName(toolInfoDataDo.getEquipmentName());
                builder1.setEquipmentNo(toolInfoDataDo.getEquipmentNo());
                List<ToolDataDo> toolDataList = toolInfoDataDo.getToolDataList();
                for (ToolDataDo toolDataDo : toolDataList) {
                    PaoGuangKanBanProtobuf.ToolDataDo.Builder builder2 = PaoGuangKanBanProtobuf.ToolDataDo.newBuilder();
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
    public synchronized void sendStateEquiment(DzEquipment dzEquipment) {
        boolean b = frequencyLimitation(cacheBaseName + ":PushPaoGuang:" + dzEquipment.getId(), 3);
        if (b) {
            String eventKey = getEvent(SocketMessageType.DEVICE_STATUS, dzEquipment.getOrderNo(), dzEquipment.getLineNo());
            boolean isSendEvend = getIsSendEvend(eventKey);
            if (isSendEvend) {
                String currentLocation = dzEquipment.getCurrentLocation();
                PaoGuangKanBanProtobuf.MachiningMessageStatus.Builder status = PaoGuangKanBanProtobuf.MachiningMessageStatus.newBuilder();
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
                PaoGuangKanBanProtobuf.InitDeviceState.Builder builder = PaoGuangKanBanProtobuf.InitDeviceState.newBuilder();
                builder.addStatus(status);
                builder.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_STATE.getInfo());
                byte[] bytes = builder.build().toByteArray();
                socketServerTemplate.sendMessage(SocketMessageType.DEVICE_STATUS, eventKey, bytes);
            }
        }
    }


    @Override
    public void sendReatimLogs(DeviceLogsMsg b) {
        String eventKey = getEvent(SocketMessageType.DEVICE_LOG, b.getOrderCode(), b.getLineNo());
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = dateFormat.format(b.getTimestampTime());
            PaoGuangKanBanProtobuf.InitRealTimeLogRes.Builder builder = PaoGuangKanBanProtobuf.InitRealTimeLogRes.newBuilder();
            PaoGuangKanBanProtobuf.RealTimeLogRes.Builder builder1 = PaoGuangKanBanProtobuf.RealTimeLogRes.newBuilder();
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


}
