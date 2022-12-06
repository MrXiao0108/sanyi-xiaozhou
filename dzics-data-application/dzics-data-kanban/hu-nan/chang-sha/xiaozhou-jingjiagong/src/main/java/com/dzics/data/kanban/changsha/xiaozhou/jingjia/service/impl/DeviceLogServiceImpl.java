package com.dzics.data.kanban.changsha.xiaozhou.jingjia.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.common.base.dto.log.ReatimLogRes;
import com.dzics.data.common.base.enums.DeviceSocketSendStatus;
import com.dzics.data.common.base.model.custom.JCEquimentBase;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.logms.service.SysRealTimeLogsService;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.service.DeviceLogService;
import com.dzics.data.pub.service.DzEquipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class DeviceLogServiceImpl implements DeviceLogService {
    @Autowired
    private SysRealTimeLogsService realTimeLogsService;
    @Autowired
    private DzEquipmentService dzEquipmentService;

    @Override
    public List<ReatimLogRes> getReatimLogRes(String orderNo, String lineNo, Integer logType) {
        List<ReatimLogRes> ls = new ArrayList<>();
        List<DzEquipment> equipments = dzEquipmentService.getDeviceOrderNoLineNo(orderNo, lineNo);
        if (CollectionUtils.isNotEmpty(equipments)) {
            Integer size = 5;
            for (DzEquipment equipment : equipments) {
                Integer deType = equipment.getEquipmentType();
                String deCode = equipment.getEquipmentNo();
                List<ReatimLogRes> logResList = realTimeLogsService.getReatimeLog(orderNo, lineNo, String.valueOf(deType), deCode, logType, size);
                ls.addAll(logResList);
            }
        }
        ls.sort(Comparator.comparing(ReatimLogRes::getRealTime).reversed());
        return ls;
    }

    @Override
    public Result getRealTimeLogsResp(String orderNo, String lineNo, Integer logType) {
        List<ReatimLogRes> list = getReatimLogRes(orderNo, lineNo, logType);
        JCEquimentBase jcEquimentBase = new JCEquimentBase();
        jcEquimentBase.setData(list);
        jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_REAL_TIME_LOG.getInfo());
        Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
        return ok;
    }

    @Override
    public Result getRealTimeLogWarn(String orderNo, String lineNo, Integer deviceType) {
        List<ReatimLogRes> list = getReatimLogRes(orderNo, lineNo, deviceType);
        JCEquimentBase jcEquimentBase = new JCEquimentBase();
        jcEquimentBase.setData(list);
        jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_ALARM_LOG.getInfo());
        Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
        return ok;
    }
}
