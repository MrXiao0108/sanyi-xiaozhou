package com.dzics.data.appoint.changsha.mom.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.appoint.changsha.mom.model.entity.DzicsInsideLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xnb
 * @since 2022-11-12
 */
@Mapper
public interface DzicsInsideLogDao extends BaseMapper<DzicsInsideLog> {

    List<DzicsInsideLog>getBackInsideLogs(@Param("beginTime")String beginTime,@Param("endTime")String endTime,
                                          @Param("businessType")String businessType,@Param("state")String state,
                                          @Param("msg")String msg,@Param("filed")String filed,@Param("type")String type);

}
