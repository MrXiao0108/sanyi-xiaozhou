package com.dzics.data.acquisition.task;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.pdm.model.entity.DzLineShiftDay;
import com.dzics.data.pdm.service.DzLineShiftDayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_DATE;

/**
 * 系统任务
 *
 * @author ZhangChengJun
 * Date 2021/1/19.
 * @since
 */
@Service
@Slf4j
public class SysBusTask implements SimpleJob {
    @Autowired
    private DzLineShiftDayService dzLineShiftDayService;

    @Autowired
    public RedissonClient redissonClient;

    @PostConstruct
    public void arrangeInit() {
        log.info("开支执行排班任务。。。。。。。。。。");
        RLock lock = redissonClient.getLock(RedisKey.SYS_BUS_TASK_ARRANGE);
        try {
            lock.lock();
            LocalDate now = LocalDate.now();
            getLocalDate(now);
            LocalDate plusDays = LocalDate.now().plusDays(1L);
            getLocalDate(plusDays);
        } catch (Throwable e) {
            log.error("排班时发生错误：{}", e.getMessage(), e);
        } finally {
            lock.unlock();
        }
        log.info("执行排班任务结束。。。。。。。。。。");
    }

    /**
     * 定时设备排班
     */
    public void arrange( LocalDate localDate) {
        log.info("开支执行排班任务。。。。。。。。。。");
        RLock lock = redissonClient.getLock(RedisKey.SYS_BUS_TASK_ARRANGE);
        try {
            lock.lock();
//            查询未排班的设备
//            当天班次排班
            if (localDate == null) {
                LocalDate now = LocalDate.now();
                getLocalDate(now);
                LocalDate localDate1 = LocalDate.now().plusDays(1L);
                getLocalDate(localDate1);
            } else {
                getLocalDate(localDate);
            }
        } catch (Throwable e) {
            log.error("排班时发生错误：{}", e.getMessage(), e);
        } finally {
            lock.unlock();
        }
        log.info("执行排班任务结束。。。。。。。。。。");
    }

    private void getLocalDate(LocalDate localDate) {
        String substring = localDate.toString().substring(0, 7);
        int year = localDate.getYear();
        List<Long> eqId = dzLineShiftDayService.getNotPb(localDate);
        if (CollectionUtils.isNotEmpty(eqId)) {
            List<DzLineShiftDay> dzLineShiftDays = dzLineShiftDayService.getBc(eqId);
            dzLineShiftDays.stream().forEach(dzLineShiftDay -> {
                dzLineShiftDay.setWorkData(localDate);
                dzLineShiftDay.setWorkYear(year);
                dzLineShiftDay.setWorkMouth(substring);
            });
            dzLineShiftDayService.saveBatch(dzLineShiftDays);
        } else {
            log.warn("日期：{} 已排班", localDate);
        }
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("排班任务执行开始。。。。。。。。。。。。");
        String jobParameter = shardingContext.getJobParameter();
        if (StringUtils.isEmpty(jobParameter)){
            arrange(null);
        }else {
            LocalDate parse = LocalDate.parse(jobParameter, ISO_DATE);
            arrange(parse);
        }
        log.info("排班任务执行结束。。。。。。。。。。。。");
    }
}
