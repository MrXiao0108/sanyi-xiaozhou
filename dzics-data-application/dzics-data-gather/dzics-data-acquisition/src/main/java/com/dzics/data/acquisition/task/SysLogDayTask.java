package com.dzics.data.acquisition.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.logms.db.dao.SysRealTimeLogsDao;
import com.dzics.data.logms.model.entity.SysRealTimeLogs;
import com.dzics.data.logms.service.DzEquipmentStateLogService;
import com.dzics.data.logms.service.SysLoginLogService;
import com.dzics.data.logms.service.SysOperationLoggingService;
import com.dzics.data.logms.service.SysRealTimeLogsService;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.service.DzEquipmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Date;
import java.util.List;

/**
 * 系统日志清理任务
 *
 * @author ZhangChengJun
 * Date 2021/3/3.
 * @since
 */
@Service
@Slf4j
public class SysLogDayTask implements SimpleJob {
    @Autowired
    private SysOperationLoggingService operationLoggingService;
    @Autowired
    private SysLoginLogService loginLogService;
    @Autowired
    private DzEquipmentStateLogService businessEquipmentStateLogService;
    @Autowired
    private SysRealTimeLogsService sysRealTimeLogsService;
    @Autowired
    private DzEquipmentService dzEquipment;

    @Value("${del.sys.real.time.logs.day}")
    private Integer delRealday;

    @Value("${del.operation.log.day}")
    private Integer delOperationLog;

    @Value("${del.login.log.day}")
    private Integer delLoginLog;

    @Value("${del.equipment.log.day}")
    private Integer delEquipmentLog;
    @Autowired
    private SysRealTimeLogsDao logsMapper;
    /**
     * 清理系统操作日志
     */
    public void delOperationLog() {
        try {
//        清理系统操作日志
            log.info("开始 清理系统操作日志。。。。。。。。");
            operationLoggingService.delOperationLog(delOperationLog);
            log.info("结束 清理系统操作日志。。。。。。。。");
        } catch (Throwable throwable) {
            log.error("清理系统操作日志 错误", throwable);
        }
        try {
            //        清理系统登录日志.
            log.info("开始 清理系统登录日志。。。。。。。。");
            loginLogService.delLoginLog(delLoginLog);
            log.info("结束 清理系统登录日志。。。。。。。。");
        } catch (Throwable throwable) {
            log.error("清理系统登录日志 错误", throwable);
        }
        try {
            //        清理设备运行日志
            log.info("开始 清理设备运行日志。。。。。。。。");
            businessEquipmentStateLogService.delEquimentLog(delEquipmentLog);
            log.info("结束 清理设备运行日志。。。。。。。。");
        } catch (Throwable throwable) {
            log.error("清理设备运行日志 错误", throwable);
        }
        try {
            log.info("开始 删除设备告警日志 sys_real_time_logs ");
            List<DzEquipment> allOrders = dzEquipment.list();
            sysDelRealday(delRealday, allOrders);
            log.info("结束 删除设备告警日志 sys_real_time_logs ");

        } catch (Throwable throwable) {
            log.error("删除设备告警日志 错误", throwable);
        }

        try {
            log.info("开始 删除任务运行日志 job_execution_log ");
            sysRealTimeLogsService.delJobExecutionLog(15);
            log.info("结束 删除任务运行日志 job_execution_log ");
        } catch (Throwable throwable) {
            log.error("删除任务运行日志 错误", throwable);
        }
        try {
            log.info("开始 删除任务运行日志 job_status_trace_log ");
            sysRealTimeLogsService.delJobStatusTraceLog(15);
            log.info("结束 删除任务运行日志 job_status_trace_log ");
        } catch (Throwable throwable) {
            log.error("删除任务运行日志 错误", throwable);
        }


    }

    public void sysDelRealday(Integer days, List<DzEquipment> odnos) {
        long time = System.currentTimeMillis() - ((long) days * (24 * 3600 * 1000));
        Date date = new Date(time);
        if(!CollectionUtils.isEmpty(odnos)){
            for (DzEquipment eq : odnos) {
//                order_code,line_no,device_type,device_code
                QueryWrapper<SysRealTimeLogs> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("order_code",eq.getOrderNo());
                queryWrapper.eq("line_no",eq.getLineNo());
                queryWrapper.eq("device_type",String.valueOf(eq.getEquipmentType()));
                queryWrapper.eq("device_code", eq.getEquipmentNo());
                queryWrapper.le("timestamp_time", date);
                logsMapper.delete(queryWrapper);
            }
        }
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("开始 每天执行清除日志操作。。。。。。。。");
        delOperationLog();
        log.info("结束 每天执行清除日志操作。。。。。。。。");
    }
}
