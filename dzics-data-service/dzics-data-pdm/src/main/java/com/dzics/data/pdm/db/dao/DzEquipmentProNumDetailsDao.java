package com.dzics.data.pdm.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pdm.model.dao.UpValueDevice;
import com.dzics.data.pdm.model.entity.DzEquipmentProNumDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 设备生产数量详情表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Mapper
public interface DzEquipmentProNumDetailsDao extends BaseMapper<DzEquipmentProNumDetails> {

    UpValueDevice getupsaveddnumlinnuty(@Param("lineNum") String lineNum, @Param("deviceNum") String deviceNum, @Param("deviceType") String deviceType, @Param("orderNumber") String orderNumber);

}
