package com.dzics.data.acquisition.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.common.base.enums.PushEnumType;
import com.dzics.data.common.base.enums.WorkState;
import com.dzics.data.common.base.model.vo.PushKanbanBase;
import com.dzics.data.common.base.model.dto.SocketDowmSum;
import com.dzics.data.acquisition.service.impl.AcqRabbitmq;
import com.dzics.data.common.util.SnowflakeUtil;
import com.dzics.data.pdm.model.entity.DzDayShutDownTimes;
import com.dzics.data.pdm.model.entity.DzEquipmentTimeAnalysis;
import com.dzics.data.pdm.model.entity.DzTimeChangeHandle;
import com.dzics.data.pdm.service.DzDayShutDownTimesService;
import com.dzics.data.pdm.service.DzEquipmentTimeAnalysisService;
import com.dzics.data.pdm.service.DzTimeChangeHandleService;
import com.dzics.data.pub.model.dto.EqIdOrgCode;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.service.DzEquipmentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ZhangChengJun
 * Date 2021/11/17.
 * @since
 */
@Slf4j
@Service
public class TimeAnalysisHandle implements DataflowJob<DzTimeChangeHandle> {
    @Value("${center.device.downsum.routingkey}")
    private String routingKey;
    @Value("${center.device.downsum.topicexchange}")
    private String topicexchange;
    @Autowired
    private DzTimeChangeHandleService dzTimeChangeHandleService;
    @Autowired
    private DzEquipmentTimeAnalysisService timeAnalysisService;
    @Autowired
    private SnowflakeUtil snowflakeUtil;
    @Autowired
    private DzDayShutDownTimesService dayShutDownTimesService;
    @Autowired
    private DzEquipmentService equipmentService;
    @Autowired
    private AcqRabbitmq acqRabbitmq;

    @Override
    public List<DzTimeChangeHandle> fetchData(ShardingContext shardingContext) {
        log.debug("执行设备状态变更记录数据拉去:shardingParameter{}", shardingContext);
        PageHelper.startPage(1, 10);
        QueryWrapper<DzTimeChangeHandle> wp = new QueryWrapper<>();
        wp.eq("equipment_no", shardingContext.getShardingParameter());
        wp.orderByAsc("key_sort");
        List<DzTimeChangeHandle> handles = dzTimeChangeHandleService.list(wp);
        PageInfo<DzTimeChangeHandle> dzTimeChangeHandlePageInfo = new PageInfo<>(handles);
        List<DzTimeChangeHandle> changeHandles = dzTimeChangeHandlePageInfo.getList();
        List<String> collect = changeHandles.stream().map(s -> s.getChangeId()).collect(Collectors.toList());
        dzTimeChangeHandleService.removeByIds(collect);
        return changeHandles;
    }

    @Override
    public synchronized void processData(ShardingContext shardingContext, List<DzTimeChangeHandle> list) {
        for (DzTimeChangeHandle changeHandle : list) {
            Integer workState = changeHandle.getWorkState();
            Date upLocalDate = changeHandle.getUpLocalDate();
            LocalDate localDate = changeHandle.getDetectionDate();
            Date nowDate = changeHandle.getDetectionTime();
            String deviceId = changeHandle.getDeviceId();
//           START 切割记录
            if (workState == null) {
                splitDateDayTimeIns(localDate, nowDate, upLocalDate, deviceId);
                continue;
            }
//           END 切割记录
            LocalTime localTime = changeHandle.getDetectionLocalTime();
            Integer equipmentType = changeHandle.getEquipmentType();
            String equipmentNo = changeHandle.getEquipmentNo();
            String orderNo = changeHandle.getOrderNo();
            String lineNo = changeHandle.getLineNo();
//          START 设置上次状态结束时间，和插入新的状态开始时间
            DzEquipmentTimeAnalysis timeAnalysis = timeAnalysisService.getResetTimeIsNull(deviceId);
            long duration = 0;
            if (timeAnalysis == null) {
//                    插入记录
                DzEquipmentTimeAnalysis dzEquipmentTimeAnalysis = saveTimeAnalysis(localDate, localTime, nowDate, deviceId, orderNo, lineNo, equipmentNo, equipmentType, workState);
                timeAnalysisService.saveTimeAnlysis(dzEquipmentTimeAnalysis);
            } else {
//                    更新 并且 插入记录  状态变化，结束上次状态，生成当前开始状态
                long stopTime = timeAnalysis.getStopTime().getTime();
                long restTime = nowDate.getTime();
                duration = restTime - stopTime;
                timeAnalysis.setDuration(duration);
                timeAnalysis.setResetTime(nowDate);
                timeAnalysis.setResetDayTime(localTime);
                timeAnalysis.setResetHour(localTime.getHour());
                timeAnalysisService.updateByIdTimeAnalysis(timeAnalysis);
                DzEquipmentTimeAnalysis dzEquipmentTimeAnalysis = saveTimeAnalysis(localDate, localTime, nowDate, deviceId, orderNo, lineNo, equipmentNo, equipmentType, workState);
                timeAnalysisService.saveTimeAnlysis(dzEquipmentTimeAnalysis);
            }
//          END 设置上次状态结束时间，和插入新的状态开始时间

//          START 更新每日停机记录次数，设备总停机时长，总停机次数
            if (EquiTypeEnum.MEN.getCode() == Integer.valueOf(equipmentType)) {
                if (WorkState.OPEN == workState.intValue()) {
//                     设备类型是 10 安全门 ，状态为1 时为开门，统计开门次数
                    boolean b = updateDeviceStopSum(orderNo, lineNo, equipmentNo, equipmentType, localDate, deviceId);
                }
                if (timeAnalysis != null && timeAnalysis.getWorkState().intValue() == WorkState.OPEN) {
                    boolean b = updateTimeSum(orderNo, lineNo, equipmentNo, equipmentType, duration, deviceId);
                }
            } else {
                if (WorkState.SHUTDOWN == workState.intValue()) {
                    boolean b = updateDeviceStopSum(orderNo, lineNo, equipmentNo, equipmentType, localDate, deviceId);
                }
                if (timeAnalysis != null && timeAnalysis.getWorkState().intValue() == WorkState.SHUTDOWN) {
                    boolean b = updateTimeSum(orderNo, lineNo, equipmentNo, equipmentType, duration, deviceId);
                }
            }

//          END 更新每日停机记录次数，设备总停机时长，总停机次数
        }

    }

    /**
     * 更新次数
     *
     * @param orderNo  订单
     * @param lineNo   产线
     * @param eqNo     设备编号
     * @param eqType   设备类型
     * @param noLoDate 监控变化的日期
     * @param deviceId 设备ID
     * @return
     */
    public boolean updateDeviceStopSum(String orderNo, String lineNo, String eqNo, Integer eqType, LocalDate noLoDate, String deviceId) {
        String eqTyStr = String.valueOf(eqType);
        Long downSum = equipmentService.upDownSum(lineNo, eqNo, eqTyStr, orderNo);
        if (downSum == null) {
            log.warn("设备用时分析停机次数NULL：orderNo：{},lineNum:{},deviceNum:{},deviceType:{}", orderNo, lineNo, eqNo, eqTyStr);
            return false;
        }
        DzDayShutDownTimes dayDown = dayShutDownTimesService.getDayShoutDownTime(lineNo, eqNo, eqType, orderNo, noLoDate);
        if (dayDown == null) {
            EqIdOrgCode eqIdOrgCode = equipmentService.getDeviceOrgCode(lineNo, eqNo, eqTyStr, orderNo);
            String orgCode = eqIdOrgCode.getOrgCode();
            dayDown = new DzDayShutDownTimes();
            dayDown.setWorkDate(noLoDate);
            dayDown.setOrderNo(orderNo);
            dayDown.setLineNo(lineNo);
            dayDown.setEquipmentNo(eqNo);
            dayDown.setEquipmentType(eqType);
            dayDown.setDownSum(1L);
            dayDown.setOrgCode(orgCode);
            dayDown.setDelFlag(false);
            dayDown.setCreateBy("");
            dayDown.setCreateTime(new Date());
            dayShutDownTimesService.saveDzDayShutDownTimes(dayDown);
        } else {
            dayDown.setDownSum(dayDown.getDownSum().longValue() + 1);
            dayShutDownTimesService.updateByIdDzDayShutDownTimes(dayDown);
        }
        downSum = downSum + 1;
        DzEquipment dzE = new DzEquipment();
        dzE.setId(deviceId);
        dzE.setEquipmentNo(eqNo);
        dzE.setLineNo(lineNo);
        dzE.setEquipmentType(eqType);
        dzE.setOrderNo(orderNo);
        dzE.setDownSum(downSum);
        dzE.setDayDownSum(dayDown.getDownSum());
        int up = equipmentService.updateByLineNoAndEqNoDownTime(dzE);
        Long upDateDownSum = equipmentService.upDateDownSum(lineNo, eqNo, eqTyStr, orderNo, downSum);
//        发送到当日停机次数到MQ
        extracted(orderNo, lineNo, deviceId, dayDown);
        return true;
    }

    /**
     * 发送停机次数到队列
     *
     * @param orderNo
     * @param lineNo
     * @param deviceId
     * @param dayDown
     */
    private void extracted(String orderNo, String lineNo, String deviceId, DzDayShutDownTimes dayDown) {
        SocketDowmSum dowmSum = new SocketDowmSum();
        dowmSum.setDownSum(dayDown.getDownSum());
        dowmSum.setEquimentId(deviceId);
        dowmSum.setLineNo(lineNo);
        dowmSum.setOrderNo(orderNo);
        PushKanbanBase<SocketDowmSum> kanbanBase = new PushKanbanBase();
        kanbanBase.setData(dowmSum);
        kanbanBase.setType(PushEnumType.DOWN_SUM);
        acqRabbitmq.sendDataCenter(routingKey, topicexchange, kanbanBase);
    }

    /**
     * 更新停机  开门时长
     *
     * @param orderNo
     * @param lineNo
     * @param eqNo
     * @param eqType
     * @param duration
     * @param deviceId
     * @return
     */
    public boolean updateTimeSum(String orderNo, String lineNo, String eqNo, Integer eqType, long duration, String deviceId) {
        String eqTyStr = String.valueOf(eqType);
        Long downSumTime = equipmentService.upDownSumTime(lineNo, eqNo, eqTyStr, orderNo);
        if (downSumTime == null) {
            log.warn("设备用时分析停机时间为NULL：orderNo：{},lineNum:{},deviceNum:{},deviceType:{}", orderNo, lineNo, eqNo, eqTyStr);
            return false;
        }
        downSumTime = downSumTime + duration;
        DzEquipment dzE = new DzEquipment();
        dzE.setId(deviceId);
        dzE.setEquipmentNo(eqNo);
        dzE.setLineNo(lineNo);
        dzE.setEquipmentType(eqType);
        dzE.setOrderNo(orderNo);
        dzE.setDownTime(downSumTime);
        int up = equipmentService.updateByLineNoAndEqNoDownTime(dzE);
        Long upDateDownTime = equipmentService.upDateDownTime(lineNo, eqNo, eqTyStr, orderNo, downSumTime);
        return true;
    }

    public void splitDateDayTimeIns(LocalDate localDate, Date nowDate, Date upLocalDate, String deviceId) {
//        设置截止时间
        int hoursNow = nowDate.getHours();
        int hoursUp = upLocalDate.getHours();
        Calendar instanceEnd = Calendar.getInstance();
        if (hoursNow != (hoursUp + 1)) {
            instanceEnd.setTime(nowDate);
        } else {
            instanceEnd.setTime(upLocalDate);
        }
        instanceEnd.set(Calendar.MINUTE, 00);
        instanceEnd.set(Calendar.SECOND, 00);
        instanceEnd.set(Calendar.MILLISECOND, 0);
        instanceEnd.add(Calendar.HOUR_OF_DAY, 1);
        Date endOfDay = instanceEnd.getTime();
        LocalTime resetDayTime = DateToLocalTime(endOfDay);

        Calendar instanceStart = Calendar.getInstance();
        instanceStart.setTime(nowDate);
        instanceStart.set(Calendar.MINUTE, 00);
        instanceStart.set(Calendar.SECOND, 00);
        instanceStart.set(Calendar.MILLISECOND, 0);
        Date startOfDay = instanceStart.getTime();
        LocalTime localTime = DateToLocalTime(startOfDay);
//        一小时切割，切割记录插入
        List<DzEquipmentTimeAnalysis> analysisList = timeAnalysisService.getRestTimeIsNullDeviceId(deviceId);
        for (DzEquipmentTimeAnalysis timeAnalysis : analysisList) {
            long endOfDayTime = endOfDay.getTime();
            long stopTime = timeAnalysis.getStopTime().getTime();
            if (timeAnalysis.getGroupId() == null) {
                timeAnalysis.setGroupId(snowflakeUtil.nextId());
            }
            long sumDown = endOfDayTime - stopTime;
            timeAnalysis.setDuration(sumDown);
            timeAnalysis.setResetTime(endOfDay);
            timeAnalysis.setResetDayTime(resetDayTime);
            timeAnalysis.setResetHour(resetDayTime.getHour());
//        更新停机总时长
            if (timeAnalysis.getWorkState().intValue() == WorkState.SHUTDOWN) {
                DzEquipment byId = equipmentService.getById(deviceId);
                long updateTime = byId.getDownTime() + sumDown;
                byId.setDownTime(updateTime);
                equipmentService.updateById(byId);
                Long upDateDownTime = equipmentService.upDateDownTime(byId.getLineNo(), byId.getEquipmentNo(), String.valueOf(byId.getEquipmentType()), byId.getOrderNo(), updateTime);
            }
        }
        timeAnalysisService.updateByIdList(analysisList);
        List<DzEquipmentTimeAnalysis> inst = analysisList.stream().collect(Collectors.toList());
        for (DzEquipmentTimeAnalysis timeAnalysis : inst) {
            timeAnalysis.setId(null);
            timeAnalysis.setResetTime(null);
            timeAnalysis.setResetDayTime(null);
            timeAnalysis.setResetHour(null);
            timeAnalysis.setDuration(0L);
            timeAnalysis.setCreateTime(null);
            timeAnalysis.setUpdateTime(null);
            timeAnalysis.setStopTime(startOfDay);
            timeAnalysis.setStopDayTime(localTime);
            timeAnalysis.setStopHour(localTime.getHour());
            timeAnalysis.setStopData(localDate);
        }
        timeAnalysisService.insTimeAnalysis(inst);
    }


    public LocalTime DateToLocalTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        LocalTime localTime = localDateTime.toLocalTime();
        return localTime;
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

    public Date localDateToUdate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        Date date = Date.from(instant);
        return getPreciseTime(date);
    }

    public DzEquipmentTimeAnalysis saveTimeAnalysis(LocalDate now, LocalTime localTime, Date date, String deviceId, String orderNo, String lineNo, String equipmentNo, Integer equipmentType, int workState) {
//        更新设备表停机次数
        DzEquipmentTimeAnalysis analysis = new DzEquipmentTimeAnalysis();
        analysis.setDeviceId(deviceId);
        analysis.setOrderNo(orderNo);
        analysis.setLineNo(lineNo);
        analysis.setEquipmentNo(equipmentNo);
        analysis.setEquipmentType(equipmentType);
        analysis.setWorkState(workState);
        analysis.setGroupId(snowflakeUtil.nextId());
        analysis.setStopTime(date);
        analysis.setStopData(now);
        analysis.setStopDayTime(localTime);
        analysis.setStopHour(localTime.getHour());
        analysis.setOrgCode("A00");
        analysis.setDelFlag(false);
        analysis.setCreateBy("SYS-A00");
        return analysis;
    }
}
