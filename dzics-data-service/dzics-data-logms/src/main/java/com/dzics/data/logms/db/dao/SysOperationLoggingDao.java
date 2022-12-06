package com.dzics.data.logms.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.logms.model.entity.SysOperationLogging;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 操作日志 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-06
 */
@Mapper
public interface SysOperationLoggingDao extends BaseMapper<SysOperationLogging> {

    List<SysOperationLogging> queryOperLog(@Param("startLimit") int startLimit, @Param("limit") int limit);
}
