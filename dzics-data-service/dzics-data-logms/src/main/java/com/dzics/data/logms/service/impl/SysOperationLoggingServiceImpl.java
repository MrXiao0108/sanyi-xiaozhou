package com.dzics.data.logms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.logms.db.dao.SysOperationLoggingDao;
import com.dzics.data.logms.model.entity.SysOperationLogging;
import com.dzics.data.logms.service.SysOperationLoggingService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 操作日志 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-06
 */
@Service
public class SysOperationLoggingServiceImpl extends ServiceImpl<SysOperationLoggingDao, SysOperationLogging> implements SysOperationLoggingService {

    @Override
    public List<SysOperationLogging> queryOperLog(int startLimit, int limit) {
        return this.baseMapper.queryOperLog(startLimit, limit);
    }

    @Override
    public void delOperationLog(int i) {
        long time = System.currentTimeMillis() - (i * (24 * 3600 * 1000));
        Date date = new Date(time);
        QueryWrapper<SysOperationLogging> wrapper = new QueryWrapper<>();
        wrapper.le("oper_time", date);
        remove(wrapper);
    }
}
