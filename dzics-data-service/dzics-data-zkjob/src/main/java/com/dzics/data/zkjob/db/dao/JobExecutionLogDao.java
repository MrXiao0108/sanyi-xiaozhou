package com.dzics.data.zkjob.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.zkjob.model.dao.GetWorkDo;
import com.dzics.data.zkjob.model.dto.GetWorkVo;
import com.dzics.data.zkjob.model.entity.DzJobExecutionLog;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author dzics
 * @since 2021-11-22
 */
public interface JobExecutionLogDao extends BaseMapper<DzJobExecutionLog> {
    List<GetWorkDo> geiList(GetWorkVo getWorkVo);
}
