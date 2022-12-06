package com.dzics.data.pdm.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pdm.model.dto.DayReportForm;
import com.dzics.data.pdm.model.entity.DzLineShiftDay;
import com.dzics.data.pdm.model.vo.MachineDownExcelVo;
import com.dzics.data.pdm.model.vo.RobotDownExcelVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 设备产线 每日 排班表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-19
 */
@Mapper
public interface DzLineShiftDayDao extends BaseMapper<DzLineShiftDay> {

    List<DzLineShiftDay> getBc(@Param("list") List<Long> list);

    List<Long> getNotPb(@Param("now") LocalDate now);

    List<DayReportForm> getDayReportFormTaskSignal(@Param("now") LocalDate now);

    List<DayReportForm> getWorkDate(@Param("now") LocalDate now);

    List<MachineDownExcelVo>getMachineDownExcel(@Param("departId")String departId,@Param("orgCode")String orgCode,@Param("orderNo")String orderNo, @Param("lineNo")String lineNo,
                                                @Param("equipmentNo")String equipmentNo, @Param("field")String field, @Param("type")String type);

    List<RobotDownExcelVo>getRobotDownExcel(@Param("departId")String departId,@Param("orgCode")String orgCode,@Param("orderNo")String orderNo, @Param("lineNo")String lineNo,
                                                @Param("equipmentNo")String equipmentNo, @Param("field")String field, @Param("type")String type);
}
