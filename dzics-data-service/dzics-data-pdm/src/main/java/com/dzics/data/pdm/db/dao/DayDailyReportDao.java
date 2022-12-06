package com.dzics.data.pdm.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.common.base.model.dao.QualifiedAndOutputDo;
import com.dzics.data.pdm.model.entity.DayDailyReport;
import com.dzics.data.pdm.model.vo.DayDailyReportExcel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 日产报表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-23
 */
@Mapper
public interface DayDailyReportDao extends BaseMapper<DayDailyReport> {

    List<DayDailyReportExcel> getDayDailyReport(@Param("field") String field, @Param("type") String type, @Param("endTime") LocalDate endTime, @Param("startTime") LocalDate startTime);

    BigDecimal getNowNum(@Param("dayId") String dayId);

    QualifiedAndOutputDo getDailyPassRate(@Param("deviId") String deviId, @Param("now") LocalDate now);
}
