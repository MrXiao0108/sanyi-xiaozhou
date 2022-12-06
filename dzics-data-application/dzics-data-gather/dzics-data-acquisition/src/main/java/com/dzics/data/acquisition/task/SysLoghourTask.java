package com.dzics.data.acquisition.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dzics.data.logms.service.SysCommunicationLogService;
import com.dzics.data.pub.db.dao.DzEquipmentDao;
import com.dzics.data.pub.model.entity.DzEquipment;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
public class SysLoghourTask implements SimpleJob {
    @Autowired
    private SysCommunicationLogService busCommunicationLogService;
    @Autowired
    private DzEquipmentDao equipmentDao;

    @Value("${del.communication.log.day}")
    private Integer delCommunicationLog;


    /**
     * 每小时执行一次
     */
    public void delCommunicationLog() {
//        order_code,line_no,device_type,device_code
        List<DzEquipment> list = equipmentDao.selectList(Wrappers.emptyWrapper());
//        清理通讯日志
        log.info("开始 清楚指令日志。。。。。。。。");
        for (DzEquipment dzEquipment : list) {
            String orderNo = dzEquipment.getOrderNo();
            String lineNo = dzEquipment.getLineNo();
            String device_type = dzEquipment.getEquipmentType().toString();
            String device_code = dzEquipment.getEquipmentNo();
            busCommunicationLogService.delCommunicationLog(delCommunicationLog,orderNo,lineNo,device_type,device_code);
        }
        log.info("结束 清楚指令日志。。。。。。。。");
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("开始 每小时执行清除日志操作。。。。。。。。");
        delCommunicationLog();
        log.info("结束 每小时执行清除日志操作。。。。。。。。");
    }
}
