package com.dzics.data.pdm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.common.base.model.dao.QualifiedAndOutputDo;
import com.dzics.data.pdm.db.dao.DayDailyReportDao;
import com.dzics.data.pdm.model.dto.DayReportForm;
import com.dzics.data.pdm.model.entity.DayDailyReport;
import com.dzics.data.pdm.model.vo.DayDailyReportExcel;
import com.dzics.data.pdm.service.DayDailyReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 日产报表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-23
 */
@Service
public class DayDailyReportServiceImpl extends ServiceImpl<DayDailyReportDao, DayDailyReport> implements DayDailyReportService {

    @Override
    public List<DayDailyReportExcel> getDayDailyReport(String field, String type, LocalDate endTime, LocalDate startTime) {
        if (endTime != null) {
            endTime = endTime.plusDays(1L);
        }
        return this.baseMapper.getDayDailyReport(field, type, endTime, startTime);
    }

    @Override
    public boolean getWorkDate(LocalDate now) {
        QueryWrapper<DayDailyReport> wp = new QueryWrapper<>();
        wp.eq("workData", now);
        int count = count(wp);
        return count <= 0;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean saveDayDayReport(List<DayReportForm> dayReportForms, String month) {
        List<DayDailyReport> dayDailyReports = new ArrayList<>();
        for (DayReportForm reportForm : dayReportForms) {
            Long nowNum = reportForm.getNowNum() != null ? reportForm.getNowNum() : 0L;
            Long qualifiedNum = reportForm.getQualifiedNum() != null ? reportForm.getQualifiedNum() : 0L;
            Long roughNum = reportForm.getRoughNum() != null ? reportForm.getRoughNum() : 0L;
            Long badnessNum = reportForm.getBadnessNum() != null ? reportForm.getBadnessNum() : 0L;
            DayDailyReport dayDailyReport = new DayDailyReport();
            dayDailyReport.setDayId(reportForm.getDayId());
            dayDailyReport.setLinename(reportForm.getLineName());
            int i = reportForm.getEquipmentType().intValue();
            if (i == EquiTypeEnum.JC.getCode()) {
                dayDailyReport.setEquipmentType(EquiTypeEnum.JC);
            } else if (i == EquiTypeEnum.JQR.getCode()) {
                dayDailyReport.setEquipmentType(EquiTypeEnum.JQR);
            } else if (i == EquiTypeEnum.XJ.getCode()) {
                dayDailyReport.setEquipmentType(EquiTypeEnum.XJ);
            } else if (i == EquiTypeEnum.JCSB.getCode()) {
                dayDailyReport.setEquipmentType(EquiTypeEnum.JCSB);
            } else if (i == EquiTypeEnum.EQCODE.getCode()) {
                dayDailyReport.setEquipmentType(EquiTypeEnum.EQCODE);
            }
            dayDailyReport.setEquipmentcode(reportForm.getEquipmentCode());
            dayDailyReport.setEquipmentname(reportForm.getEquipmentName());
            dayDailyReport.setWorkname(reportForm.getWorkName());
            dayDailyReport.setTimeRange(reportForm.getStartTime() + "-" + reportForm.getEndTime());
            dayDailyReport.setWorkdata(reportForm.getWorkData());
            dayDailyReport.setNownum(nowNum);
            dayDailyReport.setRoughnum(roughNum);
            dayDailyReport.setQualifiednum(qualifiedNum);
            dayDailyReport.setBadnessnum(badnessNum);
//            生产数量
            BigDecimal nowBigDeci = new BigDecimal(nowNum);
//            毛坯
            BigDecimal roughBigDeci = new BigDecimal(roughNum);
//            产出率 = 生产数量/毛坯
            BigDecimal divide = new BigDecimal(0);
            if (roughBigDeci.compareTo(new BigDecimal(0)) != 0) {
                divide = nowBigDeci.divide(roughBigDeci, 2, BigDecimal.ROUND_HALF_UP);
            }
            dayDailyReport.setOutputRate(divide);
//            合格
            BigDecimal qualiDecimal = new BigDecimal(qualifiedNum);
//            合格率 = 合格/ 产出
            BigDecimal passRate = new BigDecimal(0);
            if (nowBigDeci.compareTo(new BigDecimal(0)) != 0) {
                passRate = qualiDecimal.divide(nowBigDeci, 2, BigDecimal.ROUND_HALF_UP);
            }
            dayDailyReport.setPassRate(passRate);
            dayDailyReport.setEquimentid(reportForm.getEquimentId());
            dayDailyReport.setLineid(reportForm.getLineId());
            dayDailyReport.setLineno(reportForm.getLineNo());
            dayDailyReport.setOrderNo(reportForm.getOrderNo());
            dayDailyReport.setOrgCode(reportForm.getOrgCode());
            dayDailyReport.setDelFlag(false);
            dayDailyReport.setWorkMouth(month);
            dayDailyReport.setCreateBy("system");
            dayDailyReports.add(dayDailyReport);
        }
        saveBatch(dayDailyReports);
        return false;
    }

    @Override
    public List<DayDailyReport> getListWorkDate(LocalDate now) {
        QueryWrapper<DayDailyReport> wp = new QueryWrapper<>();
        wp.eq("workData", now);
        return list(wp);
    }

    @Override
    public BigDecimal getNowNum(String dayId) {
        return this.baseMapper.getNowNum(dayId);
    }

    @Override
    public QualifiedAndOutputDo getDailyPassRate(String deviId, LocalDate now) {
        return this.baseMapper.getDailyPassRate(deviId,now);
    }


}
