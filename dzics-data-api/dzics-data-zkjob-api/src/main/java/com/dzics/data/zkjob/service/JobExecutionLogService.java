package com.dzics.data.zkjob.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.zkjob.model.dao.GetWorkDo;
import com.dzics.data.zkjob.model.dto.GetWorkVo;
import com.dzics.data.zkjob.model.entity.DzJobExecutionLog;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dzics
 * @since 2021-11-22
 */
public interface JobExecutionLogService extends IService<DzJobExecutionLog> {
    Result<List<GetWorkDo>> getList(GetWorkVo getWorkVo);
}
