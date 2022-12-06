package com.dzics.data.acquisition.task;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.pdm.db.dao.DzProductionPlanDayDao;
import com.dzics.data.pdm.model.entity.PlanDayLineNo;
import com.dzics.data.pdm.service.DzProductionPlanDayService;
import com.dzics.data.pdm.service.DzProductionPlanDaySignalService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 每天执行一次计算昨天的生产率
 *
 * @author ZhangChengJun
 * Date 2021/11/19.
 * @since
 */
@Service
@Slf4j
public class UpdateOnlyDayRateTask implements SimpleJob {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzProductionPlanDayDao dzProductionPlanDayMapper;
    @Autowired
    private DzProductionPlanDayService dayService;
    @Autowired
    private DzProductionPlanDaySignalService daySignalService;


    /**
     * 启动时更新生产率情况
     */
    @PostConstruct
    public void updateCompletionRateUpday() {
        LocalDate now = LocalDate.now().plusDays(-1);
        updateRate(now);
    }

    public void updateRate(LocalDate now) {
        log.info("开始 UpdateOnlyDayRateTask 更新日期：{} 的生产率数据。。。。。。。。。。", now);
        String key = RedisKey.UPDATE_COMPLETION_RATE_ADD + now.toString();
        List<PlanDayLineNo> pl = redisUtil.lGet(key, 0, -1);
        if (CollectionUtils.isEmpty(pl)) {
            pl = dzProductionPlanDayMapper.selDateLinNo(now);
            if (CollectionUtils.isNotEmpty(pl)) {
                pl = pl.stream().filter(s -> s.getStatisticsEquimentId() != null).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(pl)) {
                    redisUtil.lSet(key, pl, 120);
                }
            }
        }
        dayService.updateCompletionRate(now, pl);
        String keySignal = RedisKey.UPDATE_COMPLETION_RATE_SIGNAL + now.toString();
        List<PlanDayLineNo> sigl = redisUtil.lGet(keySignal, 0, -1);
        if (CollectionUtils.isEmpty(sigl)) {
            sigl = dzProductionPlanDayMapper.selDateLinNoSignal(now);
            if (CollectionUtils.isNotEmpty(sigl)) {
                sigl = sigl.stream().filter(s -> s.getStatisticsEquimentId() != null).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(sigl)) {
                    redisUtil.lSet(keySignal, sigl, 120);
                }
            }
        }
        daySignalService.updateCompletionRate(now, sigl);
        log.info("完成 UpdateOnlyDayRateTask  更新日期：{} 的生产率数据。。。。。。。。。。", now);

    }

    @Override
    public void execute(ShardingContext shardingContext) {
        Integer integer = Integer.valueOf(shardingContext.getShardingParameter());
        LocalDate now = LocalDate.now().plusDays(-(integer));
        updateRate(now);
    }
}
