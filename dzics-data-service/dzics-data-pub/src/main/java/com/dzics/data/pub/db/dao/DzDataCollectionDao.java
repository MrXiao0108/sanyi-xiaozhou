package com.dzics.data.pub.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pub.model.dao.TimeAnalysisCmd;
import com.dzics.data.pub.model.entity.DzDataCollection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-29
 */
public interface DzDataCollectionDao extends BaseMapper<DzDataCollection> {

    List<TimeAnalysisCmd> getDeviceTypeCmdSingal(@Param("shardingParameter") String shardingParameter);

    TimeAnalysisCmd getDeviceId(@Param("deviceId") String deviceId);
}
