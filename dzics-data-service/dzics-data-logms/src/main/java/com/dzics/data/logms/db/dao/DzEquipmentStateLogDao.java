package com.dzics.data.logms.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.logms.model.entity.DzEquipmentStateLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 设备运行状态记录表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-20
 */
@Mapper
public interface DzEquipmentStateLogDao extends BaseMapper<DzEquipmentStateLog> {

}
