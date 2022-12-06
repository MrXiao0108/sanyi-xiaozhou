package com.dzics.data.acquisition.task;

import com.dzics.data.pdm.db.dao.DzEquipmentProNumSignalDao;
import com.dzics.data.pdm.db.dao.DzLineShiftDayDao;
import com.dzics.data.pdm.db.model.dao.SumSignalDao;
import com.dzics.data.pdm.model.dto.DayReportForm;
import com.dzics.data.pdm.model.entity.DayDailyReport;
import com.dzics.data.pdm.service.DayDailyReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * 日报表
 *
 * @author ZhangChengJun
 * Date 2021/6/22.
 * @since
 */
@Service
@Slf4j
public class ReportDayFormTask implements SimpleJob {

    @Autowired
    private DzLineShiftDayDao dzLineShiftDayMapper;

    @Autowired
    private DayDailyReportService accDayDailyServcie;

    @Autowired
    private DzEquipmentProNumSignalDao proNumSignalDao;

    /**
     * 日产报表任务计算当天 每分钟 30秒计算一次
     */
    public void dayReportFormTask() {
        LocalDate now = LocalDate.now();
        String month = now.toString().substring(0, 7);
        boolean exist = accDayDailyServcie.getWorkDate(now);
        if (exist) {
            List<DayReportForm> dayReportForms = dzLineShiftDayMapper.getDayReportFormTaskSignal(now);
            if (CollectionUtils.isEmpty(dayReportForms)) {
                log.warn("任务执行插入日产：{},统计数据：dayReportForms：{} 无数据", now, dayReportForms);
                return;
            }
            for (DayReportForm dayReportForm : dayReportForms) {
                String equimentId = dayReportForm.getEquimentId();
                String dayId = dayReportForm.getDayId();
                String orderNo = dayReportForm.getOrderNo();
                SumSignalDao sumSignalDao = proNumSignalDao.getEqIdDayId(equimentId, dayId, orderNo);
                if (sumSignalDao != null) {
                    dayReportForm.setNowNum(sumSignalDao.getNowNum());
                    dayReportForm.setRoughNum(sumSignalDao.getRoughNum());
                    dayReportForm.setQualifiedNum(sumSignalDao.getQualifiedNum());
                    dayReportForm.setBadnessNum(sumSignalDao.getBadnessNum());
                }
            }
            boolean tr = accDayDailyServcie.saveDayDayReport(dayReportForms, month);
        } else {
            List<DayDailyReport> reportForms = accDayDailyServcie.getListWorkDate(now);
            for (DayDailyReport reportForm : reportForms) {
                String equimentId = reportForm.getEquimentid();
                String dayId = reportForm.getDayId();
                String orderNo = reportForm.getOrderNo();
                SumSignalDao sumSignalDao = proNumSignalDao.getEqIdDayId(equimentId, dayId,orderNo);
                if (sumSignalDao != null) {
                    reportForm.setNownum(sumSignalDao.getNowNum());
                    reportForm.setRoughnum(sumSignalDao.getRoughNum());
                    reportForm.setQualifiednum(sumSignalDao.getQualifiedNum());
                    reportForm.setBadnessnum(sumSignalDao.getBadnessNum());
                }
            }
            accDayDailyServcie.updateBatchById(reportForms);
        }
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        log.debug("生成日产报表任务执行日期：{}", LocalDate.now());
        dayReportFormTask();
    }
}
