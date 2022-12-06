package com.dzics.data.logms.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.logms.model.entity.SysCommunicationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 通信日志 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-08
 */
@Mapper
public interface SysCommunicationLogDao extends BaseMapper<SysCommunicationLog> {

}
