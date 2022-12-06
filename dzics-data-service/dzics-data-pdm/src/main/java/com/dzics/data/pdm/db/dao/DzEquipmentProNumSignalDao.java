package com.dzics.data.pdm.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pdm.db.model.dao.HourToday;
import com.dzics.data.pdm.db.model.dao.SumSignalDao;
import com.dzics.data.pdm.model.entity.DzEquipmentProNumSignal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 班次生产记录表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-23
 */
@Mapper
public interface DzEquipmentProNumSignalDao extends BaseMapper<DzEquipmentProNumSignal> {

    List<HourToday> selectTodayByHour(@Param("tableKey") String tableKey, @Param("nowDate") String date, @Param("list") List<Long> list);

    Long getEquimentIdDayProNum(@Param("id") Long id, @Param("nowDay") LocalDate nowDay, @Param("tableKey") String tableKey);

    List<Map<String, Object>> getMonthData(@Param("year") int year, @Param("orderNo") String orderNo, @Param("eqId") String eqId);

    List<Map<String, Object>> getMonthDataNowNum(@Param("year") int year, @Param("orderNo") String orderNo, @Param("eqId") String eqId);

    Long shiftProductionDetails(@Param("tableKey") String tableKey, @Param("id") Long id);

    List<Long> productionDailyReport(Long equipmentId);

    List<Map<String, Object>> getMonthDataShift(@Param("tableKey") String tableKey, @Param("eqId") Long eqId, @Param("year") Integer year);

    SumSignalDao getEqIdDayId(@Param("equimentId") String equimentId, @Param("dayId") String dayId, @Param("orderNo") String orderNo);

    List<Map<String, Object>> getMonthDataByOrderNo(int year, String orderNo);

    /**
     * 获取某个设备的当班的总合格生产数量
     * @Parme dayId：日排班ID
     * @Parme equipmentId：设备ID
     * @Parme orderNo：订单编号
     * @Parme localDate：当前时间
     * @return Long
     * */
    Long getSumQuaNum(@Param("dayId")String dayId,@Param("equipmentId")String equipmentId,@Param("orderNo")String orderNo);
}
