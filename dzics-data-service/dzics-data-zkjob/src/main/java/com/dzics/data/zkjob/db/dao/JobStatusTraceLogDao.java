package com.dzics.data.zkjob.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.zkjob.model.dao.GetWorkStatusDo;
import com.dzics.data.zkjob.model.dto.GetWorkStatusVo;
import com.dzics.data.zkjob.model.entity.JobStatusTraceLog;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author dzics
 * @since 2021-11-22
 */
public interface JobStatusTraceLogDao extends BaseMapper<JobStatusTraceLog> {
    List<GetWorkStatusDo> getList(GetWorkStatusVo getWorkStatusVo);
}
