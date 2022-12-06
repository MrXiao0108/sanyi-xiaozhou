package com.dzics.data.pdm.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pdm.model.dao.UpValueDeviceSignal;
import com.dzics.data.pdm.model.entity.DzEquipmentProNumDetailsSignal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 设备生产数量详情表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-23
 */
@Mapper
public interface DzEquipmentProNumDetailsSignalDao extends BaseMapper<DzEquipmentProNumDetailsSignal> {

    UpValueDeviceSignal getupsaveddnumlinnuty(@Param("lineNum") String lineNum, @Param("deviceNum") String deviceNum, @Param("deviceType") String deviceType, @Param("orderNumber") String orderNumber, @Param("dayId") Long dayId);

}
