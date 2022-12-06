package com.dzics.data.zkjob.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.zkjob.db.dao.JobStatusTraceLogDao;
import com.dzics.data.zkjob.model.dao.GetWorkStatusDo;
import com.dzics.data.zkjob.model.dto.GetWorkStatusVo;
import com.dzics.data.zkjob.model.entity.JobStatusTraceLog;
import com.dzics.data.zkjob.service.JobStatusTraceLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dzics
 * @since 2021-11-22
 */
@Service
public class JobStatusTraceLogServiceImpl extends ServiceImpl<JobStatusTraceLogDao, JobStatusTraceLog> implements JobStatusTraceLogService {
    @Autowired
    private JobStatusTraceLogDao jobStatusTraceLogMapper;

    @Override
    public Result<List<GetWorkStatusDo>> getList(GetWorkStatusVo getWorkStatusVo) {
        if (getWorkStatusVo.getPage() != -1){
            PageHelper.startPage(getWorkStatusVo.getPage(), getWorkStatusVo.getLimit());
        }
        List <GetWorkStatusDo> list = jobStatusTraceLogMapper.getList(getWorkStatusVo);
        PageInfo pageInfo = new PageInfo(list);
        return new Result(CustomExceptionType.OK,pageInfo.getList(),pageInfo.getTotal());
    }
}
