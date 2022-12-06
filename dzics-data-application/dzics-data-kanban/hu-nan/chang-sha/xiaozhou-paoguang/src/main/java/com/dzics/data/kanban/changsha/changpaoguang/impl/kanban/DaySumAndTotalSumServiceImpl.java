package com.dzics.data.kanban.changsha.changpaoguang.impl.kanban;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.custom.MachiningNumTotal;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.service.impl.DzEquipmentProNumServiceImpl;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.vo.RunDataModel;
import com.dzics.data.pub.service.DzEquipmentService;
import com.dzics.data.pub.service.SysConfigService;
import com.dzics.data.pub.service.kanban.DaySumAndTotalSumService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname DaySumAndTotalSumServiceImpl
 * @Description  安仁设备日产总产
 * @Date 2022/2/14 12:33
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class DaySumAndTotalSumServiceImpl implements DaySumAndTotalSumService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private DzEquipmentService equipmentService;
    @Autowired
    public DzEquipmentProNumServiceImpl proNumService;
    @Override
    public Result getDeviceProductionQuantity(GetOrderNoLineNo noLineNo) {
        String key = noLineNo.getProjectModule() +RedisKey.DAY_SUM_AND_TOTAL_SUM + noLineNo.getOrderNo() + noLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Object o = redisUtil.get(key);
                return (Result) o;
            }
            LocalDate now = LocalDate.now();
            String orderNo = noLineNo.getOrderNo();
            List<DzEquipment> dzEquipments = equipmentService.getDeviceOrderNoLineNo(orderNo, noLineNo.getLineNo());
            if (CollectionUtils.isNotEmpty(dzEquipments)) {
                List<String> collect = dzEquipments.stream().map(s -> s.getId().toString()).collect(Collectors.toList());
                List<MachiningNumTotal> machiningNumTotalsX = machiningNumTotals(now, collect,orderNo);
                Result<List<MachiningNumTotal>> ok = Result.ok(machiningNumTotalsX);
                redisUtil.set(key, ok, noLineNo.getCacheTime());
                return ok;
            }
            return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
        } catch (Exception e) {
            log.error("根据订单和产线序号查询设备日产总产异常:{}", e.getMessage(),e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }

    private List<MachiningNumTotal> machiningNumTotals(LocalDate now, List<String> collect, String orderNo) {
        String tableKey = ((RunDataModel) sysConfigService.systemRunModel(null).getData()).getTableName();
        List<MachiningNumTotal> machiningNumTotals = proNumService.getEqIdData(now, collect, tableKey, orderNo);
        List<MachiningNumTotal> machiningNumTotalsX = new ArrayList<>();
        for (MachiningNumTotal machiningNumTotal : machiningNumTotals) {
            if (machiningNumTotal != null) {
                machiningNumTotalsX.add(machiningNumTotal);
            }
        }
        return machiningNumTotalsX;
    }
}
