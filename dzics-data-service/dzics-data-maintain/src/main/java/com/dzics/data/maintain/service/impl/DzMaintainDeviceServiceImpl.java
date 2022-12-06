package com.dzics.data.maintain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.vo.BaseTimeLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.maintain.db.dao.DzMaintainDeviceDao;
import com.dzics.data.maintain.model.dto.MaintainDetailsParms;
import com.dzics.data.maintain.model.dto.MaintainDeviceParms;
import com.dzics.data.maintain.model.dto.MaintainRecordParms;
import com.dzics.data.maintain.model.entity.DzMaintainDevice;
import com.dzics.data.maintain.model.entity.DzMaintainDeviceHistory;
import com.dzics.data.maintain.model.entity.DzMaintainDeviceHistoryDetails;
import com.dzics.data.maintain.model.vo.MaintainDevice;
import com.dzics.data.maintain.model.vo.MaintainRecord;
import com.dzics.data.maintain.model.vo.MaintainRecordDetails;
import com.dzics.data.maintain.service.DzMaintainDeviceHistoryDetailsService;
import com.dzics.data.maintain.service.DzMaintainDeviceHistoryService;
import com.dzics.data.maintain.service.DzMaintainDeviceService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 保养设备配置 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
@Service
public class DzMaintainDeviceServiceImpl extends ServiceImpl<DzMaintainDeviceDao, DzMaintainDevice> implements DzMaintainDeviceService {

    @Autowired
    private DzMaintainDeviceService dzMaintainDevice;
    @Autowired
    private DzMaintainDeviceHistoryService deviceHistoryService;
    @Autowired
    private DzMaintainDeviceHistoryDetailsService deviceHistoryDetailsService;


    @Override
    public List<MaintainDevice> getMaintainListWait(String lineId, String equipmentNo, String states, LocalDate startTime, LocalDate endTime, String field, String type, LocalDate now) {
        return baseMapper.getMaintainListWait(lineId, equipmentNo, states, startTime, endTime, field, type, now);
    }

    @Override
    public List<MaintainDevice> getMaintainListOver(String lineId, String equipmentNo, String states, LocalDate startTime, LocalDate endTime, String field, String type, LocalDate now) {
        return baseMapper.getMaintainListOver(lineId, equipmentNo, states, startTime, endTime, field, type, now);
    }

    @Override
    public List<MaintainRecord> getMaintainRecord(BaseTimeLimit pageLimit, MaintainRecordParms parmsReq) {
        List<MaintainRecord> maintainRecord = baseMapper.getMaintainRecord(pageLimit.getStartTime(), pageLimit.getEndTime(), pageLimit.getField(), pageLimit.getType(), parmsReq.getCreateBy(), parmsReq.getMaintainId());
        return maintainRecord;
    }

    @Override
    public List<MaintainRecordDetails> getMaintainRecordDetails(String maintainHistoryId) {
        return baseMapper.getMaintainRecordDetails(maintainHistoryId);
    }

    @Override
    public Result getMaintainRecord(String sub, BaseTimeLimit pageLimit, MaintainRecordParms parmsReq) {
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        List<MaintainRecord> maintainRecords = dzMaintainDevice.getMaintainRecord(pageLimit, parmsReq);
        PageInfo<MaintainRecord> info = new PageInfo<>(maintainRecords);
        return Result.ok(info.getList(), info.getTotal());
    }

    @Override
    public Result getMaintainRecordDetails(String sub, MaintainDetailsParms parmsReq) {
        List<MaintainRecordDetails> maintainRecordDetails = dzMaintainDevice.getMaintainRecordDetails(parmsReq.getMaintainHistoryId());
        return Result.OK(maintainRecordDetails);
    }

    @Override
    public Result<List<MaintainDevice>> getMaintainList(String sub, BaseTimeLimit pageLimit, MaintainDeviceParms parmsReq, String useOrgCode) {
        LocalDate startTime = pageLimit.getStartTime();
        LocalDate endTime = pageLimit.getEndTime();
        String field = pageLimit.getField();
        String type = pageLimit.getType();
        String equipmentNo = parmsReq.getEquipmentNo();
        String lineId = parmsReq.getLineId();
        String states = parmsReq.getStates();
        LocalDate now = LocalDate.now();
        if (pageLimit.getPage() != -1) {
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        List<MaintainDevice> maintainDevices = null;
        if (!StringUtils.isEmpty(states)) {
            if ("1".equals(states)) {
                maintainDevices = dzMaintainDevice.getMaintainListWait(lineId, equipmentNo, states, startTime, endTime, field, type, now);
//            搜索等待下一次保养
            } else if ("2".equals(states)) {
//            搜索过期为保养
                maintainDevices = dzMaintainDevice.getMaintainListOver(lineId, equipmentNo, states, startTime, endTime, field, type, now);
            }
        } else {
            maintainDevices = baseMapper.getMaintainList(lineId, equipmentNo, states, startTime, endTime, field, type, useOrgCode);
        }
        PageInfo<MaintainDevice> info = new PageInfo<>(maintainDevices);
        List<MaintainDevice> list = info.getList();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(s -> s.setConcatUnit(s.getMultiple() + s.getUnit() + "/次"));
        }
        setSateMaintainRecord(list, now);
        return Result.ok(maintainDevices, info.getTotal());
    }

    private void setSateMaintainRecord(List<MaintainDevice> list, LocalDate now) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (MaintainDevice maintainDevice : list) {
            LocalDate localDate = LocalDate.parse(maintainDevice.getMaintainDateAfter(), df);
            if (localDate.compareTo(now) > 0) {
                maintainDevice.setStates("等待下一次保养");
            } else {
                maintainDevice.setStates("保养时间超时");
            }

        }
    }

    @Override
    public Result delMaintainDevice(String sub, String maintainId) {
        dzMaintainDevice.removeById(maintainId);
        QueryWrapper<DzMaintainDeviceHistory> wp = new QueryWrapper<>();
        wp.eq("maintain_id", maintainId);
        List<DzMaintainDeviceHistory> list = deviceHistoryService.list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> collect = list.stream().map(s -> s.getMaintainHistoryId()).collect(Collectors.toList());
            QueryWrapper<DzMaintainDeviceHistoryDetails> wpDetails = new QueryWrapper<>();
            wpDetails.in("maintain_history_id", collect);
            deviceHistoryDetailsService.remove(wpDetails);
        }
        deviceHistoryService.remove(wp);
        return Result.ok();
    }

    @Override
    public DzMaintainDevice getByDeviceId(String deviceId) {
        QueryWrapper<DzMaintainDevice> wp = new QueryWrapper<>();
        wp.eq("device_id", deviceId);
        DzMaintainDevice one = getOne(wp);
        return one;
    }


}
