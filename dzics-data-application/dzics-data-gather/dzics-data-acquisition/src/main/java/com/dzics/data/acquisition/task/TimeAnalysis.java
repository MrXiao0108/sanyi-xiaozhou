package com.dzics.data.acquisition.task;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.pdm.model.entity.DzEquipmentTimeAnalysis;
import com.dzics.data.pdm.model.entity.DzTimeChangeHandle;
import com.dzics.data.pdm.service.DzEquipmentTimeAnalysisService;
import com.dzics.data.pdm.service.DzTimeChangeHandleService;
import com.dzics.data.pub.model.dao.TimeAnalysisCmd;
import com.dzics.data.pub.service.DzDataCollectionService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 设备用时分析任务
 *
 * @author ZhangChengJun
 * Date 2021/10/9.
 * @since
 */
@Service
@Slf4j
public class TimeAnalysis implements SimpleJob {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzDataCollectionService dzDataCollectionService;
    @Autowired
    private DzEquipmentTimeAnalysisService timeAnalysisService;
    @Autowired
    private DzTimeChangeHandleService dzTimeChangeHandleService;

    /**
     * 监控机器人设备状态变化
     *
     * @param shardingParameter
     */
    public void robTimeAnalysis(String shardingParameter) {
        // 日历对象
        Calendar c = Calendar.getInstance();
        // 设置时间
        c.setTime(new Date());
        // 设置毫秒值为0
        c.set(Calendar.MILLISECOND, 0);
        Date date = c.getTime();
        LocalDate nowDate = DateToLocalDate(date);
        LocalTime localTime = DateToLocalTime(date);
        int hourNow = localTime.getHour();
//        是否跨小时校验
        Date upLocalDate = timeAnalysisService.robTimeAnalysis(shardingParameter);
        if (upLocalDate != null) {
            LocalDate upLocalRq = DateToLocalDate(upLocalDate);

            int hourAfter = upLocalDate.getHours();
            if (!nowDate.toString().equals(upLocalRq.toString()) || hourAfter != hourNow) {
                String str = (String) redisUtil.get(RedisKey.SPLIT_DATE_DAY_TIME_INS + shardingParameter);
                if (str == null) {
                    splitDateDayTimeIns(nowDate, date, upLocalDate, shardingParameter);
                    String dateStr = DateUtil.getDateStr(date);
                    redisUtil.set(RedisKey.SPLIT_DATE_DAY_TIME_INS + shardingParameter, dateStr, 1800);
                } else {
                    log.warn("1800秒内已经触发过切割记录 shardingParameter：{},上次触发时间：{}", shardingParameter, str);
                }
            }
        }
        List<TimeAnalysisCmd> dzDataCollections = dzDataCollectionService.getDeviceTypeCmdSingal(shardingParameter);
        List<DzTimeChangeHandle> timeChangeHandles = new ArrayList<>();
        for (TimeAnalysisCmd dzDataCollection : dzDataCollections) {
            String deviceId = dzDataCollection.getDeviceId();
            Integer equipmentType = dzDataCollection.getEquipmentType();
            String orderNo = dzDataCollection.getOrderNo();
            String lineNo = dzDataCollection.getLineNo();
            String equipmentNo = dzDataCollection.getEquipmentNo();
            String status;
            String alarm;
            String connState;
            String mode;
            String stateType;
            String standby = "";
            if (EquiTypeEnum.MEN.getCode() == equipmentType.intValue()) {
                int menStatus = 0;
                status = dzDataCollection.getS561();
                if (!StringUtils.isEmpty(status)) {
                    menStatus = Integer.valueOf(status);
                }
                dzDataCollection.setMenStatus(menStatus);
                TimeAnalysisCmd timeAnalysisCmd = dzDataCollectionService.getIotTableDeviceState(deviceId);
                Integer menStatusUp = timeAnalysisCmd.getMenStatus();
                if (menStatus != menStatusUp.intValue()) {
                    DzTimeChangeHandle changeHandle = new DzTimeChangeHandle();
                    changeHandle.setDetectionTime(date);
                    changeHandle.setDetectionDate(nowDate);
                    changeHandle.setDetectionLocalTime(localTime);
                    changeHandle.setEquipmentType(equipmentType);
                    changeHandle.setOrderNo(orderNo);
                    changeHandle.setLineNo(lineNo);
                    changeHandle.setEquipmentNo(equipmentNo);
                    changeHandle.setDeviceId(deviceId);
                    changeHandle.setWorkState(menStatus);
                    changeHandle.setOrgCode("A00");
                    changeHandle.setDelFlag(false);
                    changeHandle.setCreateBy("SYS-A00");
                    timeChangeHandles.add(changeHandle);
                    TimeAnalysisCmd cmd = dzDataCollectionService.updateIotTableDeviceState(dzDataCollection);
                }
            } else {
                if (EquiTypeEnum.CHJ.getCode() == equipmentType.intValue()) {
                    stateType = "3";
                    status = dzDataCollection.getH562();
                    alarm = dzDataCollection.getH565();
                    connState = dzDataCollection.getH561();
                } else if (EquiTypeEnum.JQR.getCode() == equipmentType.intValue()) {
                    status = dzDataCollection.getA563();
                    alarm = dzDataCollection.getA566();
                    connState = dzDataCollection.getA561();
                    mode = dzDataCollection.getA562();
                    standby = dzDataCollection.getA567();
                    stateType = "1";
                } else if (EquiTypeEnum.JZJ.getCode().equals(equipmentType)) {
                    stateType = "3";
                    connState = dzDataCollection.getK561();
                    status = dzDataCollection.getK562();
                    alarm = dzDataCollection.getH565();
                    mode = dzDataCollection.getH565();
                } else {
                    status = dzDataCollection.getB562();
                    alarm = dzDataCollection.getB569();
                    connState = dzDataCollection.getB561();
                    mode = dzDataCollection.getB565();
                    stateType = "3";
                }
                //  1作业 2：待机 3：故障 4：关机
                int workState = 4;
                if (!StringUtils.isEmpty(status)) {
                    if ("1".equals(connState)) {
//                            连机
                        if ("1".equals(alarm)) {
//                                报警，设置故障
                            workState = 3;
                        } else {
                            if ("1".equals(standby)) {
                                workState = 2;
                            } else {
                                if (stateType.equals(status)) {
                                    workState = 1;
                                } else {
                                    workState = 2;
                                }
                            }

                        }
                    }
                }
                dzDataCollection.setWorkState(workState);
                TimeAnalysisCmd timeAnalysisCmd = dzDataCollectionService.getIotTableDeviceState(deviceId);
                Integer workStateUp = timeAnalysisCmd.getWorkState();
                if (workState != workStateUp.intValue()) {
                    DzTimeChangeHandle changeHandle = new DzTimeChangeHandle();
                    changeHandle.setDetectionTime(date);
                    changeHandle.setDetectionDate(nowDate);
                    changeHandle.setDetectionLocalTime(localTime);
                    changeHandle.setEquipmentType(equipmentType);
                    changeHandle.setOrderNo(orderNo);
                    changeHandle.setLineNo(lineNo);
                    changeHandle.setEquipmentNo(equipmentNo);
                    changeHandle.setDeviceId(deviceId);
                    changeHandle.setWorkState(workState);
                    changeHandle.setOrgCode("A00");
                    changeHandle.setDelFlag(false);
                    changeHandle.setCreateBy("SYS-A00");
                    timeChangeHandles.add(changeHandle);
                    TimeAnalysisCmd cmd = dzDataCollectionService.updateIotTableDeviceState(dzDataCollection);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(dzDataCollections)) {
            dzTimeChangeHandleService.saveBatch(timeChangeHandles);
        }
        Date upDateLocalDate = timeAnalysisService.updateRobTimeAnalysis(shardingParameter, date);
    }


    public void splitDateDayTimeIns(LocalDate localDate, Date nowDate, Date upLocalDate, String shardingParameter) {
        List<DzEquipmentTimeAnalysis> analysisList = timeAnalysisService.getRestTimeIsNull(shardingParameter);
        List<DzTimeChangeHandle> handles = new ArrayList<>();
        for (DzEquipmentTimeAnalysis timeAnalysis : analysisList) {
            DzTimeChangeHandle handle = new DzTimeChangeHandle();
            handle.setDetectionTime(nowDate);
            handle.setDetectionDate(localDate);
            handle.setEquipmentType(timeAnalysis.getEquipmentType());
            handle.setOrderNo(timeAnalysis.getOrderNo());
            handle.setLineNo(timeAnalysis.getLineNo());
            handle.setUpLocalDate(upLocalDate);
            handle.setEquipmentNo(timeAnalysis.getEquipmentNo());
            handle.setDeviceId(timeAnalysis.getDeviceId());
            handle.setOrgCode("A00");
            handle.setDelFlag(false);
            handle.setCreateBy("SYS-A00");
            handles.add(handle);
        }
        dzTimeChangeHandleService.saveBatch(handles);
    }


    public Date getPreciseTime(Date date) {
        // 日历对象
        Calendar c = Calendar.getInstance();
        // 设置时间
        c.setTime(date);
        // 设置毫秒值为0
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public LocalDate DateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate;
    }

    public LocalTime DateToLocalTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        LocalTime localTime = localDateTime.toLocalTime();
        return localTime;
    }


    public Date localDateToUdate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        Date date = Date.from(instant);
        return getPreciseTime(date);
    }

    /**
     * @param date
     * @return
     * @description: 获得当天最小时间
     */
    public Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()),
                ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        Date from = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        return getPreciseTime(from);
    }

    /**
     * @param date
     * @return
     * @description: 获得当天最大时间
     */
    public Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()),
                ZoneId.systemDefault());
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        Date from = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
        return getPreciseTime(from);
    }

    @Override
    public synchronized void execute(ShardingContext shardingContext) {
        log.debug("执行用时任务分析:shardingParameter{}", shardingContext);
        String shardingParameter = shardingContext.getShardingParameter();
        if (StringUtils.isEmpty(shardingParameter)) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR76);
        } else {
            robTimeAnalysis(shardingParameter);
        }
    }
}
