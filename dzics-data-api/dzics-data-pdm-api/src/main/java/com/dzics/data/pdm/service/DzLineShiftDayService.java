package com.dzics.data.pdm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.pdm.model.dto.DayReportForm;
import com.dzics.data.pdm.model.entity.DzLineShiftDay;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * <p>
 * 设备产线 每日 排班表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-19
 */
public interface DzLineShiftDayService extends IService<DzLineShiftDay> {
    /**
     * @return 班次
     * @param eqId
     */
    List<DzLineShiftDay> getBc(List<Long> eqId);

    /**
     * @return 未排班的设备id
     * @param now
     */
    List<Long> getNotPb(LocalDate now);

    /**
     * 排班
     */
    void arrange();

    List<DayReportForm> getWorkDate(LocalDate now);

    /**
     * 根据当前时间返回当前班次
     *
     *
     * @param deviceId
     * @param lineNum      产线序号
     * @param deviceNum    设备序号
     * @param deviceType   设备类型
     * @param orderNumber
     * @param nowLocalDate 当前日期
     * @param localTime    当前时间
     * @return
     */

    DzLineShiftDay getLingShifuDay(String deviceId, String lineNum, String deviceNum, String deviceType, String orderNumber, LocalDate nowLocalDate, LocalTime localTime);

    List<DzLineShiftDay> getDeviceIdShift(String equipmentId, LocalDate nowDate);
}
