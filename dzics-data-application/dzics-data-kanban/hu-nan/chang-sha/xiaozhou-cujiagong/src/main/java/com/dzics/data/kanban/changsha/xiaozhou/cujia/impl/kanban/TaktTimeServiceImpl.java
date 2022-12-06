package com.dzics.data.kanban.changsha.xiaozhou.cujia.impl.kanban;

import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.pdm.model.entity.DzLineShiftDay;
import com.dzics.data.pdm.service.DayDailyReportService;
import com.dzics.data.pdm.service.DzEquipmentTimeAnalysisService;
import com.dzics.data.pdm.service.DzLineShiftDayService;
import com.dzics.data.pub.service.DzEquipmentService;
import com.dzics.data.pub.service.kanban.TaktTimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @Classname TaktTimeServiceImpl
 * @Description 描述
 * @Date 2022/4/19 10:39
 * @Created by NeverEnd
 */
@Slf4j
@Service
public class TaktTimeServiceImpl implements TaktTimeService<BigDecimal> {
    @Autowired
    private DzEquipmentService dzEquipmentService;
    @Autowired
    private DzLineShiftDayService dzLineShiftDayService;
    @Autowired
    private DayDailyReportService reportService;
    @Autowired
    private DzEquipmentTimeAnalysisService timeAnalysisService;

    /**
     * 获取生产节拍
     *
     * @param orderNoLineNo
     * @return
     */
    @Override
    public BigDecimal getTaktTime(GetOrderNoLineNo orderNoLineNo) {
        String orderNo = orderNoLineNo.getOrderNo();
        String lineNo = orderNoLineNo.getLineNo();
//        获取一个设备作为计算整个产线的节拍
        String deviceId = dzEquipmentService.getDeviceId(orderNo, lineNo, "01", String.valueOf(EquiTypeEnum.JQR.getCode()));
//        获取当前班次
        LocalDate now = LocalDate.now();
        LocalTime time = LocalTime.now();
        DzLineShiftDay lingShifuDay = dzLineShiftDayService.getLingShifuDay(deviceId, lineNo, "01", String.valueOf(EquiTypeEnum.JQR.getCode()), orderNo, now, time);
        if (lingShifuDay == null) {
            log.error("获取生产节拍时 当前班次为空，设备id：{}，产线号：{},订单号：{},设备编号：{},设备类型:{},日期：{},时间：{}", deviceId, lineNo, orderNo, "01", EquiTypeEnum.JQR.getCode(), now, time);
            return BigDecimal.ZERO;
        }
        String dayId = lingShifuDay.getId();
//        获取班次产量信息
        BigDecimal sumNow = reportService.getNowNum(dayId);
        if(0 == sumNow.intValue()){
            return BigDecimal.valueOf(0);
        }
//        设备运行时间  根据设备ID,开始时间是班次开始时间
        BigDecimal runTime = timeAnalysisService.getEquipmentSumRunTime(orderNo,deviceId, lingShifuDay.getWorkData(), lingShifuDay.getStartTime());
        return BigDecimal.valueOf(runTime.intValue() / 1000 / 60 / sumNow.intValue());
    }
}
