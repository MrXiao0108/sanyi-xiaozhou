package com.dzics.data.logms.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.logms.model.entity.SysLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 登陆日志 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-06
 */
@Mapper
public interface SysLoginLogDao extends BaseMapper<SysLoginLog> {

}
