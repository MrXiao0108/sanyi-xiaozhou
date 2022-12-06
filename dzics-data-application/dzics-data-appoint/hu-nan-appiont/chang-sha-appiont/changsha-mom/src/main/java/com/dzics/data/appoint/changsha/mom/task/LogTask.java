package com.dzics.data.appoint.changsha.mom.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.appoint.changsha.mom.model.entity.DzicsInsideLog;
import com.dzics.data.appoint.changsha.mom.model.entity.MomCommunicationLog;
import com.dzics.data.appoint.changsha.mom.service.DzicsInsideLogService;
import com.dzics.data.appoint.changsha.mom.service.MomCommunicationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author xnb
 * @date 2022/11/15 0015 11:33
 */
@Component
@Slf4j
public class LogTask {

    @Autowired
    private DzicsInsideLogService insideLogService;
    @Autowired
    private MomCommunicationLogService momCommunicationLogService;

    /**
     * 每天上午10:15触发  清除ds6数据库定制日志
     * */
    @Scheduled(cron = "0 15 10 ? * *")
    public void delAppointLog(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //保留一个月
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE,-30);
        String delTime = simpleDateFormat.format(calendar.getTime());

        log.info("LogTask [delAppointLog] 开始清除内部日志---------------");
        insideLogService.remove(new QueryWrapper<DzicsInsideLog>().le("create_time",delTime));
        log.info("LogTask [delAppointLog] 清除内部日志结束---------------");



        log.info("LogTask [delAppointLog] 开始清除Mom通讯日志---------------");
        momCommunicationLogService.remove(new QueryWrapper<MomCommunicationLog>().le("create_time",delTime));
        log.info("LogTask [delAppointLog] 清除Mom通讯日志结束---------------");
    }
}
