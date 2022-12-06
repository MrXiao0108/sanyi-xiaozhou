package com.dzics.data.kanban.changsha.changpaoguang.impl.kanban;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.common.base.enums.WorkState;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.kanban.changsha.changpaoguang.vo.DeviceParseData;
import com.dzics.data.kanban.changsha.changpaoguang.vo.DeviceParseDataBase;
import com.dzics.data.kanban.changsha.changpaoguang.vo.DeviceStateDetailsData;
import com.dzics.data.kanban.changsha.changpaoguang.vo.DeviceStateDetailsResp;
import com.dzics.data.pdm.model.vo.DeviceStateDetails;
import com.dzics.data.pdm.service.DzEquipmentTimeAnalysisService;
import com.dzics.data.pub.db.dao.DzEquipmentDao;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.vo.DeviceMessage;
import com.dzics.data.pub.service.DzProductionLineService;
import com.dzics.data.pub.service.kanban.TimeAnalysisService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用时分析接口实现
 */
@Service
@Slf4j
public class TimeAnalysisServiceImpl implements TimeAnalysisService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzProductionLineService lineService;
    @Autowired
    private DzEquipmentDao dzEquipmentDao;
    @Autowired
    private DzEquipmentTimeAnalysisService analysisService;

    /**
     * 根据订单产线号查询所有设备当日用时分析(旧)-不分时段
     *
     * @param orLineNo
     */
    @Override
    public Result getTimeAnalysis(GetOrderNoLineNo orLineNo) {
        DeviceStateDetailsResp resp = new DeviceStateDetailsResp();
        String key = orLineNo.getProjectModule() + RedisKey.DEVICE_TIME_ANALYSIS_NEW + orLineNo.getOrderNo() + orLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Object o = redisUtil.get(key);
                if (o != null) {
                    return (Result) o;
                }
            }
            Calendar instance = Calendar.getInstance();
//            如果当前时间在 早上 08:00 之前 设置开始时间是前一天 08:00 之后
//            如果当前时间在 早上 08:00 之后 设置时间是当前日期 08:00 之后
            Calendar calenda = Calendar.getInstance();
            calenda.setTime(new Date());
            if (LocalTime.now().getHour() < 8) {
                calenda.add(Calendar.DATE, -1);
            }
            calenda.set(Calendar.HOUR_OF_DAY, 8);
            calenda.set(Calendar.MINUTE, 0);
            calenda.set(Calendar.MILLISECOND, 0);
            calenda.set(Calendar.SECOND, 0);
            Date time = calenda.getTime();
            DzProductionLine lineNo = lineService.getLineIdByOrderNoLineNo(orLineNo);
            if (lineNo == null) {
                log.error("设备用时分析包含 作业 待机 故障 关机 错误,未查询到产线，orderNoLineNo：{}", orLineNo);
                return Result.ok();
            }
            List<DeviceMessage> devcieLineId = dzEquipmentDao.getDevcieLineId(lineNo.getId());
            String orderNo = lineNo.getOrderNo();
            List<DeviceStateDetails> deviceStateDetails = new ArrayList<>();
            for (DeviceMessage deviceMessage : devcieLineId) {
                String deviceId = deviceMessage.getDeviceId();
                List<DeviceStateDetails> deviceStateDetailsStopTime = analysisService.getDeviceStateDetailsStopTime(time, deviceId, orderNo);
                if (CollectionUtils.isNotEmpty(deviceStateDetailsStopTime)) {
                    deviceStateDetails.addAll(deviceStateDetailsStopTime);
                }
            }
            List<DeviceStateDetailsData> respListX = new ArrayList<>();
            List<String> yx = new ArrayList<>();
            List<DeviceParseData> deviceParseDataX = new ArrayList<>();
            LinkedHashMap<String, Integer> mapX = new LinkedHashMap<>();
            List<DeviceStateDetails> detailsDoorNoHand = new ArrayList<>();
            List<DeviceStateDetails> deviceStateDetailsDef = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(deviceStateDetails)) {
                for (DeviceStateDetails deviceStateDetail : deviceStateDetails) {
                    String deviceType = deviceStateDetail.getDeviceType();
                    if (EquiTypeEnum.MEN.getCode() == Integer.valueOf(deviceType)) {
                        detailsDoorNoHand.add(deviceStateDetail);
                        continue;
                    } else {
                        deviceStateDetailsDef.add(deviceStateDetail);
                        continue;
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(deviceStateDetailsDef)) {
                LinkedHashMap<String, DeviceStateDetails> mxDef = new LinkedHashMap<>();
                for (DeviceStateDetails details : deviceStateDetailsDef) {
                    String groupId = details.getGroupId();
                    if (mxDef.containsKey(groupId)) {
                        DeviceStateDetails dx = mxDef.get(groupId);
                        long timeK = dx.getStopTime().getTime();
                        long timeKn = details.getStopTime().getTime();
                        if (timeKn < timeK) {
                            dx.setStopTime(details.getStopTime());
                        } else if (timeKn > timeK) {
                            dx.setResetTime(details.getResetTime());
                        }
                        dx.setDuration(dx.getDuration() + details.getDuration());
                    } else {
                        mxDef.put(groupId, details);
                    }
                }
                List<DeviceStateDetails> detailsDoorDef = mxDef.values().stream().collect(Collectors.toList());
                for (int i = 0; i < detailsDoorDef.size(); i++) {
                    DeviceStateDetails details = detailsDoorDef.get(i);
                    String equipmentName = details.getEquipmentName();
                    String equipmentNo = details.getEquipmentNo();
                    String deviceType = details.getDeviceType();
                    String doorCode = details.getDoorCode();
                    Integer index = mapX.get(equipmentNo + "|" + doorCode);
                    if (index == null) {
                        int size = yx.size();
                        mapX.put(equipmentNo + "|" + doorCode, size);
                        index = size;
                        yx.add(index, equipmentName);
                        DeviceParseData parseData = new DeviceParseData();
                        deviceParseDataX.add(parseData);
                    }
                    DeviceStateDetailsData detailsData = new DeviceStateDetailsData();
                    String workState = details.getWorkState();
//                1作业 2：待机 3：故障 4：关机
                    String name = "";
                    if ("1".equals(workState)) {
                        name = "作业";
                    }
                    if ("2".equals(workState)) {
                        name = "待机";
                    }
                    if ("3".equals(workState)) {
                        name = "故障";
                    }
                    if ("4".equals(workState)) {
                        name = "关机";
                    }
                    detailsData.setName(name);
                    List<Object> x = new ArrayList<>(3);
                    x.add(index);
                    x.add(DateUtil.dateFormatToStingYmdHms(details.getStopTime()));
                    String resetTime = details.getResetTime();
                    Long duration = details.getDuration();
                    if (resetTime == null) {
                        Calendar calendaX = Calendar.getInstance();
                        calendaX.setTime(new Date());
                        calendaX.set(Calendar.MILLISECOND, 0);
                        Date nowDate = calendaX.getTime();
                        String dateStr = DateUtil.getDateStr(nowDate);
                        long stopTimeLong = details.getStopTime().getTime();
                        duration = nowDate.getTime() - stopTimeLong;
                        detailsData.setDuration(duration);
                        x.add(dateStr);
                    } else {
                        detailsData.setDuration(duration);
                        x.add(resetTime);
                    }
                    detailsData.setValue(x);
                    DeviceParseData parseData = deviceParseDataX.get(index);
                    parseData.setDeviceName(equipmentName);
                    parseData.setDeviceType(deviceType);
                    parseData.setDoorCode(doorCode);
                    parseData.setEquipmentNo(equipmentNo);
                    if ("1".equals(workState)) {
                        parseData.setOperationDuration(parseData.getOperationDuration() + duration);
                    }
                    if ("2".equals(workState)) {
                        parseData.setStandbyDuration(parseData.getStandbyDuration() + duration);
                    }
                    if ("3".equals(workState)) {
                        parseData.setFaultDuration(parseData.getFaultDuration() + duration);
                    }
                    if ("4".equals(workState)) {
                        parseData.setShutdownDuration(parseData.getShutdownDuration() + duration);
                    }
                    BigDecimal ch = new BigDecimal(parseData.getOperationDuration());
                    BigDecimal bh = new BigDecimal(parseData.getOperationDuration() + parseData.getStandbyDuration() + parseData.getFaultDuration());
                    BigDecimal divide = (bh.compareTo(new BigDecimal(0)) == 0 ? new BigDecimal(0) : ch.divide(bh, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100))).setScale(2, BigDecimal.ROUND_HALF_UP);
                    parseData.setOperationRate(divide.toString());
                    respListX.add(detailsData);
                }
            }
//           安全门数据
            List<DeviceStateDetailsData> respListDoor = new ArrayList<>();
            List<DeviceParseDataBase> deviceParseDataDoor = new ArrayList<>();
            LinkedHashMap<String, Integer> mapDoor = new LinkedHashMap<>();

            if (CollectionUtils.isNotEmpty(detailsDoorNoHand)) {
//                放置最小的开始时间 和最大的结束时间
                LinkedHashMap<String, DeviceStateDetails> mx = new LinkedHashMap<>();
                for (DeviceStateDetails details : detailsDoorNoHand) {
                    String groupId = details.getGroupId();
                    if (mx.containsKey(groupId)) {
                        DeviceStateDetails dx = mx.get(groupId);
                        long timeK = dx.getStopTime().getTime();
                        long timeKn = details.getStopTime().getTime();
                        if (timeKn < timeK) {
                            dx.setStopTime(details.getStopTime());
                        } else if (timeKn > timeK) {
                            dx.setResetTime(details.getResetTime());
                        }
                        dx.setDuration(dx.getDuration() + details.getDuration());
                    } else {
                        mx.put(groupId, details);
                    }
                }
                List<DeviceStateDetails> detailsDoor = mx.values().stream().collect(Collectors.toList());
                for (int i = 0; i < detailsDoor.size(); i++) {
                    DeviceStateDetails details = detailsDoor.get(i);
                    String equipmentName = details.getEquipmentName();
                    String equipmentNo = details.getEquipmentNo();
                    String deviceType = details.getDeviceType();
                    String doorCode = details.getDoorCode();
                    Integer index = mapDoor.get(equipmentName);
                    if (index == null) {
                        index = getIndex(mapX, equipmentNo);
                        if (index == null) {
                            continue;
                        }
                        mapDoor.put(equipmentName, index);
                        DeviceParseDataBase parseData = new DeviceParseDataBase();
                        parseData.setDeviceName(equipmentName);
                        parseData.setDeviceType(deviceType);
                        parseData.setDoorCode(doorCode);
                        parseData.setEquipmentNo(equipmentNo);
                        deviceParseDataDoor.add(parseData);
                    }
                    DeviceStateDetailsData detailsData = new DeviceStateDetailsData();
                    String workState = details.getWorkState();
//                1作业 2：待机 3：故障 4：关机
                    String name = "";
                    if (WorkState.OPEN == Integer.valueOf(workState)) {
                        name = "开门";
                    }
                    if (WorkState.CLOSE == Integer.valueOf(workState)) {
                        name = "关门";
                    }
                    detailsData.setName(name);
                    List<Object> x = new ArrayList<>(3);
                    x.add(index);
                    x.add(details.getStopTime());
                    String resetTime = details.getResetTime();
                    Long duration = details.getDuration();
                    if (resetTime == null) {
                        Calendar calendaX = Calendar.getInstance();
                        calendaX.setTime(new Date());
                        calendaX.set(Calendar.MILLISECOND, 0);
                        Date nowDate = calendaX.getTime();
                        String dateStr = DateUtil.getDateStr(nowDate);
                        long stopTimeLong = details.getStopTime().getTime();
                        duration = nowDate.getTime() - stopTimeLong;
                        detailsData.setDuration(duration);
                        x.add(dateStr);
                    } else {
                        detailsData.setDuration(duration);
                        x.add(resetTime);
                    }
                    detailsData.setValue(x);
                    respListDoor.add(detailsData);
                }
            }
            resp.setData(respListX);
            resp.setYAxis(yx);
            resp.setDeviceBaseData(deviceParseDataX);
            resp.setDataDoor(respListDoor);
            resp.setDeviceBaseDataDoor(deviceParseDataDoor);
            Result<DeviceStateDetailsResp> ok = Result.OK(resp);
            redisUtil.set(key, ok, orLineNo.getCacheTime());
            return ok;
        } catch (Throwable throwable) {
            log.error("设备用时分析包含 作业 待机 故障 关机 错误：{}", throwable.getMessage(), throwable);
            return Result.OK(resp);
        }

    }

    private Integer getIndex(LinkedHashMap<String, Integer> mapX, String doorCode) {
        for (Map.Entry<String, Integer> map : mapX.entrySet()) {
            String key = map.getKey();
            String[] split = key.split("\\|");
            if (split.length == 2) {
                if (split[1].equals(doorCode)) {
                    return map.getValue();
                }
            }
        }
        return null;
    }


}
