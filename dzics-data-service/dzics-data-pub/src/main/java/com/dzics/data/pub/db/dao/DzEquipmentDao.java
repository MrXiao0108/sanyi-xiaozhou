package com.dzics.data.pub.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.pub.model.dao.DeviceStateDo;
import com.dzics.data.pub.model.dto.SelectEquipmentVo;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 设备表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Mapper
@Repository
public interface DzEquipmentDao extends BaseMapper<DzEquipment> {
    @Deprecated
    List<DzDataCollectionDo> getMachiningMessageStatus(@Param("lineNo") String lineNo, @Param("orderNum") String orderNum, @Param("now") LocalDate now);

    List<DeviceStateDo> getMachiningMessageStatusTwo(@Param("lineNo") String lineNo, @Param("orderNum") String orderNum, @Param("now") LocalDate now);

    List<DeviceStateDo> getMachiningMessageStatusDownSum(@Param("lineNo") String lineNo, @Param("orderNum") String orderNum, @Param("now") LocalDate now);

    List<DeviceMessage> getDevcieLineId(String lineId);

    List<EquipmentListDo> list(SelectEquipmentVo data);

    List<EquipmentDo> equipmentList(SelectEquipmentVo data);

    EquipmentDetis getById(@Param("useOrgCode") String useOrgCode, @Param("id") String id);

    List<String> listLingIdEquimentName(@Param("lineId") Long lineId);

    List<JCEquiment> listjcjqr(@Param("localDate") LocalDate localDate);

    DzEquipment listjcjqrdeviceid(@Param("deviceId") Long deviceId, @Param("localDate") LocalDate localDate);

    List<EquipmentStateDo> getEquipmentState(String lineId);

    Boolean putEquipmentDataState(PutEquipmentDataStateVo stateVo);


    @Cacheable(cacheNames = "dzEquipmentDao.getEquimentType",key = "#orderNo+#code",unless = "#result == null")
    default List<String> getEquimentType(String orderNo, int code) {
        QueryWrapper<DzEquipment> wp = new QueryWrapper<>();
        wp.eq("equipment_type", code);
        wp.eq("order_no", orderNo);
        List<DzEquipment> dzEquipments = selectList(wp);
        if (CollectionUtils.isEmpty(dzEquipments)){
            return new ArrayList<>();
        }
        return dzEquipments.stream().map(DzEquipment::getEquipmentNo).collect(Collectors.toList());
    }

    @CacheEvict(cacheNames = "dzEquipmentDao.getEquimentType", key = "#dzEquipment.orderNo + #dzEquipment.equipmentType")
    default int insertClearCache(DzEquipment dzEquipment) {
        return insert(dzEquipment);
    }

    @CacheEvict(cacheNames = "dzEquipmentDao.getEquimentType",allEntries = true)
    default int updateByIdClearCache(DzEquipment dzEquipment){
        return updateById(dzEquipment);
    }

    @CacheEvict(cacheNames = "dzEquipmentDao.getEquimentType",allEntries = true)
    default int deleteByIdClearCache(Long id){
        return deleteById(id);
    }
}
