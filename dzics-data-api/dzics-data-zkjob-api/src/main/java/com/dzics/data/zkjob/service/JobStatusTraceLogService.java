package com.dzics.data.zkjob.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.zkjob.model.dao.GetWorkStatusDo;
import com.dzics.data.zkjob.model.dto.GetWorkStatusVo;
import com.dzics.data.zkjob.model.entity.JobStatusTraceLog;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dzics
 * @since 2021-11-22
 */
public interface JobStatusTraceLogService extends IService<JobStatusTraceLog> {
        Result<List<GetWorkStatusDo>> getList(GetWorkStatusVo getWorkStatusVo);
}
