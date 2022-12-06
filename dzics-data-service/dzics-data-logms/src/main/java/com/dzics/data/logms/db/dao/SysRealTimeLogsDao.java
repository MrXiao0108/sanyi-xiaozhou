package com.dzics.data.logms.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.logms.model.entity.SysRealTimeLogs;
import com.dzics.data.common.base.dto.log.ReatimLogRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * <p>
 * 设备运行告警日志 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Mapper
public interface SysRealTimeLogsDao extends BaseMapper<SysRealTimeLogs> {

    List<ReatimLogRes> getReatimeLogsType(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo, @Param("logType") String logType, @Param("size") Integer size);

    void delJobExecutionLog(@Param("delDay") Timestamp delDay);

    void delJobStatusTraceLog(@Param("delDay") Timestamp delDay);

    List<ReatimLogRes> getReatimeLog(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo, @Param("deviceType") String deviceType, @Param("deviceCode") String deviceCode, @Param("logType") Integer logType, @Param("size") Integer size);

    List<SysRealTimeLogs> getBackReatimeLog(@Param("orderNo") String orderNo, @Param("deviceCode") String deviceCode,
                                         @Param("message") String message, @Param("beginTime") String beginTime, @Param("endTime") String endTime,@Param("deviceType")String deviceType,
                                         @Param("field") String field, @Param("type") String type);
}
