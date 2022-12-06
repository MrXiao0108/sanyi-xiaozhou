package com.dzics.data.pdm.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.common.base.model.custom.LineNumberTotal;
import com.dzics.data.common.base.model.custom.MachiningNumTotal;
import com.dzics.data.common.base.model.dao.QualifiedAndOutputDo;
import com.dzics.data.common.base.model.dao.WorkNumberName;
import com.dzics.data.pdm.db.model.dao.*;
import com.dzics.data.pdm.db.model.dto.SelectEquipmentDataVo;
import com.dzics.data.pdm.db.model.dto.SelectEquipmentProductionVo;
import com.dzics.data.pdm.db.model.dto.SelectProductionDetailsVo;
import com.dzics.data.pdm.model.entity.DzEquipmentProNum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备生产数量表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Mapper
public interface DzEquipmentProNumDao extends BaseMapper<DzEquipmentProNum> {

    /**
     * 多条件查询设备数据
     *
     * @param selectEquipmentDataVo
     * @return
     */
    List<EquipmentDataDo> listEquipmentData(SelectEquipmentDataVo selectEquipmentDataVo);

    List<EquipmentDataDo> listEquipmentDataV2(SelectEquipmentDataVo selectEquipmentDataVo);

    List<PlanRecordDetailsListDo> detailsList(@Param("detectorTime") String detectorTime,
                                              @Param("tableKey") String tableKey,
                                              @Param("statisticsEquimentId") String statisticsEquimentId,
                                              @Param("orderNo") String orderNo);

    /**
     * 查询产品生产明细
     *
     * @param selectProductionDetailsVo
     * @return
     */
    List<SelectProductionDetailsDo> getlistPro(SelectProductionDetailsVo selectProductionDetailsVo);

    long getlistPro_COUNT(SelectProductionDetailsVo selectProductionDetailsVo);

    List<EquipmentDataDetailsDo> getEquipmentDataDetails(@Param("equimentId") Long equimentId,
                                                         @Param("startTime") Date startTime,
                                                         @Param("endTime") Date endTime,
                                                         @Param("tableKey") String tableKey);


    /**
     * 设备生产数量明细列表
     *
     * @param vo
     * @return
     */
    List<SelectEquipmentProductionDo> getDeviceProdoctSum(SelectEquipmentProductionVo vo);

    long getDeviceProdoctSum_COUNT(SelectEquipmentProductionVo vo);

    /**
     * 设备生产数量详情列表
     *
     * @param equimentId
     * @param workDate
     * @param tableKey
     * @param orderNo
     * @return
     */
    List<SelectEquipmentProductionDetailsDo> listProductionEquipmentDetails(@Param("equimentId") String equimentId, @Param("workDate") String workDate, @Param("tableKey") String tableKey, @Param("orderNo") String orderNo);

    List<DayDataResultDo> dayData(@Param("tableKey") String tableKey,
                                  @Param("first") String first,
                                  @Param("last") String last,
                                  @Param("equimentId") Long equimentId);

    DayDataResultDo monthData(@Param("month") String month,
                              @Param("tableKey") String tableKey,
                              @Param("equimentId") Long equimentId);


    List<DayDataResultDo> dayDataByLine(@Param("tableKey") String tableKey,
                                        @Param("first") String first,
                                        @Param("last") String last,
                                        @Param("lineId") Long lineId);

    DayDataResultDo monthDataByLine(@Param("month") String month,
                                    @Param("orderNo") String orderNo,
                                    @Param("equipmentId") String equipmentId);

    /**
     * 查询当日生产相关数据
     *
     * @param lineId               产线名
     * @param nowDate              当天日期
     * @param orderNo              订单编号
     * @param statisticsEquimentId 设备ID
     * @return
     */
    QualifiedAndOutputDo outputCapacity(
            @Param("lineId") String lineId, @Param("nowDate") String nowDate,
            @Param("orderNo") String orderNo, @Param("statisticsEquimentId") String statisticsEquimentId);

    /**
     * 根据设备id查询设备五日内生产量
     *
     * @param eqId
     * @param tableKey
     * @param localDate
     * @param orderNo
     * @return
     */
    Long getOutputByEqId(@Param("eqId") String eqId, @Param("tableKey") String tableKey, @Param("localDate") String localDate, @Param("orderNo") String orderNo);


    List<MachiningNumTotal> getEqIdData(@Param("now") LocalDate now, @Param("list") List<String> list, @Param("tableKey") String tableKey, @Param("orderNo") String orderNo);

    List<MachiningNumTotal> getEqIdDataWorkShift(@Param("now") LocalDate now, @Param("list") List<String> list, @Param("tableKey") String tableKey);

    List<WorkNumberName> getProductName(@Param("tableKey") String tableKey, @Param("id") String id);

    List<GetMonthlyCapacityDo> getMonthlyCapacity(@Param("tableKey") String tableKey, @Param("eqId") Long eqId);

    List<MachiningNumTotal> getEqIdDataTotal(@Param("list") List<String> list, @Param("tableKey") String tableKey, @Param("orderNo") String orderNo);


    LineNumberTotal getLineSumQuantityWorkShitf(@Param("now") LocalDate now, @Param("eqId") Long eqId, @Param("tableKey") String tableKey);

    List<SocketProQuantity> getInputOutputDefectiveProducts(@Param("tableKey") String tableKey, @Param("deviceIds") List<Long> deviceIds, @Param("now") LocalDate now);

    Long getSumData(@Param("tableKey") String tableKey, @Param("eqID") String eqID);

    List<Map<String, Object>> getWorkShift(@Param("orderNo") String orderNo, @Param("equimentId") String equimentId, @Param("mouth")String mouth);

    List<Map<String, Object>> getWork(@Param("orderNo") String orderNo, @Param("equimentId") String equimentId, @Param("mouthDate") List<String> mouthDate);

    List<Map<String, Object>> getWorkShiftNowNum(@Param("orderNo") String orderNo, @Param("equimentId") String equimentId, @Param("mouth") String mouth);

    List<Map<String, Object>> workNowLocalDateSignalIds(@Param("now") LocalDate now, @Param("list") List<Long> list);

    List<com.dzics.data.pdm.model.dao.MachiningNumTotal> getEqIdDataTotalZhiHuang(@Param("list") List<String> list, @Param("tableKey") String tableKey, @Param("orderNo") String orderNo);

    List<com.dzics.data.pdm.model.dao.MachiningNumTotal> getEqIdDataZhiHuang(@Param("now") LocalDate now, @Param("list") List<String> list, @Param("tableKey") String tableKey, @Param("orderNo") String orderNo);

    /**
     * 根据设备ID 查询这天这个设备对应班次的产量
     * @param eqId
     * @param tableKey
     * @param localDate
     * @param orderNo
     * @param workName
     * @return
     */
    Long getOutputByEqIdAndWorkNameJoinShiftDay(@Param("tableKey") String tableKey,@Param("localDate") String localDate,@Param("orderNo") String orderNo,@Param("workName") String workName);

    Long getBadOutputByEqIdAndWorkNameJoinShiftDay(@Param("tableKey") String tableKey,@Param("localDate") String localDate,@Param("orderNo") String orderNo,@Param("workName") String workName);
}
