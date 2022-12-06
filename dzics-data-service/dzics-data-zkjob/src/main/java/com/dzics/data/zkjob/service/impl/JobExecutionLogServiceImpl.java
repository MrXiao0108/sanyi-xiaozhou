package com.dzics.data.zkjob.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.zkjob.db.dao.JobExecutionLogDao;
import com.dzics.data.zkjob.model.dao.GetWorkDo;
import com.dzics.data.zkjob.model.dto.GetWorkVo;
import com.dzics.data.zkjob.model.entity.DzJobExecutionLog;
import com.dzics.data.zkjob.service.JobExecutionLogService;
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
public class JobExecutionLogServiceImpl extends ServiceImpl<JobExecutionLogDao, DzJobExecutionLog> implements JobExecutionLogService {

    @Autowired
    private JobExecutionLogDao jobExecutionLogMapper;

    @Override
    public Result<List<GetWorkDo>> getList(GetWorkVo getWorkVo) {
        if(getWorkVo.getPage() != -1){
            PageHelper.startPage(getWorkVo.getPage(), getWorkVo.getLimit());
        }
        List<GetWorkDo> getWorkDos = jobExecutionLogMapper.geiList(getWorkVo);
        PageInfo pageInfo = new PageInfo(getWorkDos);
        return new Result(CustomExceptionType.OK,pageInfo.getList(),pageInfo.getTotal());

    }
}
