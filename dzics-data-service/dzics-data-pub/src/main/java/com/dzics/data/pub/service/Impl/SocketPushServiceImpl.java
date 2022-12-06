package com.dzics.data.pub.service.Impl;

import com.dzics.data.common.base.dto.log.ReatimLogRes;
import com.dzics.data.common.base.enums.DeviceSocketSendStatus;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.common.base.model.custom.JCEquimentBase;
import com.dzics.data.common.base.model.dto.SocketDowmSum;
import com.dzics.data.common.base.model.constant.ProductDefault;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.model.constant.SocketMessageType;
import com.dzics.data.common.base.model.vo.MachiningMessageStatus;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.cache.SocketCache;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.vo.DeviceLogsMsg;
import com.dzics.data.pub.service.SocketPushService;
import com.dzics.data.pub.tepmplate.SocketServerTemplate;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Matcher;

@Service
@Slf4j
public class SocketPushServiceImpl implements SocketPushService {
    @Autowired
    protected SocketServerTemplate socketServerTemplate;
    @Autowired
    protected RedisUtil<String> redisUtil;

    @Override
    public void dzRefresh(String msg) {
        String eventKey = getEvent(SocketMessageType.GET_VERSION_PUSH_REFRESH, "", "");
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setData(msg);
            jcEquimentBase.setType(DeviceSocketSendStatus.REFRESH.getInfo());
            Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
            socketServerTemplate.sendMessage(SocketMessageType.GET_VERSION_PUSH_REFRESH, eventKey, ok);
        }

    }


    /**
     * 发送停机记录
     *
     * @param dowmSum
     */
    @Override
    public void sendDownDaySum(SocketDowmSum dowmSum) {
        String eventKey = getEvent(SocketMessageType.SHUT_DOWN_TIMES, dowmSum.getOrderNo(), dowmSum.getLineNo());
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            Result sendBase = getSendBaseRunState(dowmSum);
            socketServerTemplate.sendMessage(SocketMessageType.SHUT_DOWN_TIMES, eventKey, sendBase);
        }
    }

    public String getEvent(String event, String orderCode, String lineNo) {
        if (orderCode.equals("-")) {
            orderCode = "";
        }
        if (lineNo.equals("-")) {
            lineNo = "";
        }
        return event + orderCode + lineNo;
    }

    /**
     * 判断socket链接是否存在
     *
     * @param event
     * @return
     */
    public synchronized boolean getIsSendEvend(String event) {
        ConcurrentSkipListSet<UUID> connectType = SocketCache.getConnectType(event);
        if (connectType != null && connectType.size() > 0) {
            return true;
        }
        return false;
    }

    private Result getSendBaseRunState(SocketDowmSum dz) {
        SocketDowmSum dowmSum = new SocketDowmSum();
        dowmSum.setDownSum(dz.getDownSum());
        dowmSum.setEquimentId(dz.getEquimentId());
        JCEquimentBase jcEquimentBase = new JCEquimentBase();
        jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_DOWN_NUMBER.getInfo());
        jcEquimentBase.setData(dowmSum);
        Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
        return ok;
    }


    @Override
    public synchronized void sendStateEquiment(DzEquipment dzEquipment) {
        String eventKey = getEvent(SocketMessageType.DEVICE_STATUS, dzEquipment.getOrderNo(), dzEquipment.getLineNo());
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            String currentLocation = dzEquipment.getCurrentLocation();
            String db527 = dzEquipment.getB527();
            String db526 = dzEquipment.getB526();
            String b527 = "";
            if (!StringUtils.isEmpty(db527)) {
                b527 = org.apache.commons.lang3.StringUtils.strip(db527, "[]");
                b527 = b527.replaceAll(",", ":");
                b527 = b527.replaceAll(Matcher.quoteReplacement("$"), "\\/");
            }
            String b526 = "";
            if (!StringUtils.isEmpty(db526)) {
                b526 = org.apache.commons.lang3.StringUtils.strip(db526, "[]");
                b526 = b526.replaceAll(",", ":");
                b526 = b526.replaceAll(Matcher.quoteReplacement("$"), "\\/");
            }
            MachiningMessageStatus status = new MachiningMessageStatus();
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
            status.setMovementSpeed(dzEquipment.getMovementSpeed());
            status.setWorkpieceSpeed(dzEquipment.getWorkpieceSpeed());
            status.setCoolantTemperature(dzEquipment.getCoolantTemperature());//冷却液温度 ℃
            status.setCoolantPressure(dzEquipment.getCoolantPressure());//冷却液压力 MPa
            status.setCoolantFlow(dzEquipment.getCoolantFlow());//冷却液流量 L/s
            status.setEquimentId(dzEquipment.getId());
            status.setEquipmentNo(deviceNum);
            status.setEquipmentType(deviceType);
            status.setOperatorMode(dzEquipment.getOperatorMode());
            status.setConnectState(dzEquipment.getConnectState());
            Integer runStatusValue = dzEquipment.getRunStatusValue();
            Integer alarmStatusValue = dzEquipment.getAlarmStatusValue();
            Integer connectStateValue = dzEquipment.getConnectStateValue();
            if (EquiTypeEnum.JQR.getCode() == deviceType.intValue() ){
                String a567 = dzEquipment.getA567();
                String runSate = getRunSate(runStatusValue!=null?runStatusValue.toString():"", alarmStatusValue!=null?alarmStatusValue.toString():"", connectStateValue!=null?connectStateValue.toString():"", "1", a567);
                status.setRunStatus(runSate);
            }else {
                String runSate = getRunSate(runStatusValue!=null?runStatusValue.toString():"", alarmStatusValue!=null?alarmStatusValue.toString():"", connectStateValue!=null?connectStateValue.toString():"", "3", "");
                status.setRunStatus(runSate);
            }
            status.setSpeedRatio(dzEquipment.getSpeedRatio());
            status.setMachiningTime(dzEquipment.getMachiningTime());
            String speedOfMainShaft = dzEquipment.getSpeedOfMainShaft();
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(speedOfMainShaft)) {
                BigDecimal bigDecimal = new BigDecimal(speedOfMainShaft);
                status.setSpeedOfMainShaft(bigDecimal.setScale(2, RoundingMode.HALF_UP).toString());
            } else {
                status.setSpeedOfMainShaft(speedOfMainShaft);
            }
            status.setFeedSpeed(dzEquipment.getFeedSpeed());
            status.setAlarmStatus(dzEquipment.getAlarmStatus());
            String orderNo = dzEquipment.getOrderNo();
            String lineNo = dzEquipment.getLineNo();
            String modelNumber = (String) redisUtil.get(RedisKey.CHECK_PRODUCT + orderNo + lineNo);
            if (StringUtils.isEmpty(modelNumber)) {
                modelNumber = ProductDefault.modelNumber;
            }
            status.setProNum(modelNumber);
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setData(status);
            jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_STATE.getInfo());
            Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
            socketServerTemplate.sendMessage(SocketMessageType.DEVICE_STATUS, eventKey, ok);
        }

    }
    String getRunSate(String status, String alarm, String connState, String stateType, String standby) {
        //  1作业 2：待机 3：故障 4：关机
        String workState = "关机";
        if (!StringUtils.isEmpty(status)) {
            if ("1".equals(connState)) {
//                            连机
                if ("1".equals(alarm)) {
//                                报警，设置故障
                    workState = "故障";
                } else {
                    if ("1".equals(standby)) {
                        workState = "待机";
                    } else {
                        if (stateType.equals(status)) {
                            workState = "作业 ";
                        } else {
                            workState = "待机";
                        }
                    }

                }
            }
        }
        return workState;
    }

    @Override
    public void sendReatimLogs(DeviceLogsMsg b) {
        String eventKey = getEvent(SocketMessageType.DEVICE_LOG, b.getOrderCode(), b.getLineNo());
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = dateFormat.format(b.getTimestampTime());
            ReatimLogRes reatimLogRes = new ReatimLogRes();
            reatimLogRes.setMessage(b.getMessage());
            reatimLogRes.setRealTime(format);
            reatimLogRes.setClientId(b.getClientId());
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setData(reatimLogRes);
            if (b.getMessageType() != null && b.getMessageType().intValue() == 1) {
                jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_REAL_TIME_LOG.getInfo());
                Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
                socketServerTemplate.sendMessage(SocketMessageType.DEVICE_LOG, eventKey, ok);
            } else {
                jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_ALARM_LOG.getInfo());
                Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
                socketServerTemplate.sendMessage(SocketMessageType.DEVICE_LOG_WARN, eventKey, ok);
            }
        }

    }

    /**
     * 默认返回true 不同看板
     *
     * @param key  校验key
     * @param time 时间秒为单位
     * @return 返回 true 限制解除 false 限制存在 不进行数据发送更新
     */
    @Override
    public boolean frequencyLimitation(String key, int time) {
        boolean b = redisUtil.hasKey(key);
        if (b) {
            return false;
        }
        if (time <= 0) {
            time = 2;
        }
        redisUtil.set(key, "1", time);
        return true;
    }
}
