package com.dzics.data.pdm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.model.dao.QualifiedAndOutputDo;
import com.dzics.data.pdm.model.dto.DayReportForm;
import com.dzics.data.pdm.model.entity.DayDailyReport;
import com.dzics.data.pdm.model.vo.DayDailyReportExcel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 日产报表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-23
 */
public interface DayDailyReportService extends IService<DayDailyReport> {

    List<DayDailyReportExcel> getDayDailyReport(String field, String type, LocalDate endTime, LocalDate startTime);
    /**
     * 日产报表是否存在
     * @param now
     * @return
     */
    boolean getWorkDate(LocalDate now);

    /**
     * 保存设备日产数据
     * @param dayReportForms
     * @param month
     * @return
     */
    boolean saveDayDayReport(List<DayReportForm> dayReportForms, String month);

    List<DayDailyReport> getListWorkDate(LocalDate now);


    BigDecimal getNowNum(String dayId);

    QualifiedAndOutputDo getDailyPassRate(String equipmentId, LocalDate now);
}
