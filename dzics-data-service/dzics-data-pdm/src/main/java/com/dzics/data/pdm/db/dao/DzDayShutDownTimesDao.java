package com.dzics.data.pdm.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pdm.model.entity.DzDayShutDownTimes;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 设备每日停机次数 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-23
 */
@Mapper
public interface DzDayShutDownTimesDao extends BaseMapper<DzDayShutDownTimes> {

}
