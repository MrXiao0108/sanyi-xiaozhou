package com.dzics.data.pdm.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.common.base.model.dto.DzEquipmentWorkShiftDo;
import com.dzics.data.pdm.model.entity.DzEquipmentWorkShift;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 设备工作班次表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Mapper
public interface DzEquipmentWorkShiftDao extends BaseMapper<DzEquipmentWorkShift> {


    void del(@Param("id") String id, @Param("useOrgCode") String useOrgCode);

    /**
     * 查詢起始时间和结束时间在同一天的班次 （例如:8:00:00--23:00:00）
     * @param orgCode
     * @param id
     * @param dd
     * @return
     */
    List<DzEquipmentWorkShift> getPresentWorkShift(@Param("orgCode") String orgCode, @Param("id") String id, @Param("dd") String dd);
    /**
     * 查詢起始时间和结束时间跨天的班次 （例如:22:00:00--05:00:00）
     * @param orgCode
     * @param id
     * @param dd
     * @return
     */
    List<DzEquipmentWorkShift> getPresentWorkShift2(@Param("orgCode") String orgCode, @Param("id") String id, @Param("dd") String dd);

    List<DzEquipmentWorkShiftDo> getListByLineId(@Param("lineId") String lineId, @Param("useOrgCode") String useOrgCode);
}
