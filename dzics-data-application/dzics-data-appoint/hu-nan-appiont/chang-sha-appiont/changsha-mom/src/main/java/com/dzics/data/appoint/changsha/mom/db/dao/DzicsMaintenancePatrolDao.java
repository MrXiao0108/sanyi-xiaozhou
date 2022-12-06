package com.dzics.data.appoint.changsha.mom.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.appoint.changsha.mom.model.entity.DzicsMaintenancePatrol;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 巡检维修表 Mapper 接口
 * </p>
 *
 * @author xnb
 * @since 2022-11-21
 */
@Mapper
public interface DzicsMaintenancePatrolDao extends BaseMapper<DzicsMaintenancePatrol> {
    /**
     * 查询巡检维修
     *
     * @param type
     * @param message
     * @return List<DzicsMaintenancePatrol>
     */
    List<DzicsMaintenancePatrol>getPatrol(@Param("type")String type,@Param("message")String message,@Param("modelType")String modelType);

}
