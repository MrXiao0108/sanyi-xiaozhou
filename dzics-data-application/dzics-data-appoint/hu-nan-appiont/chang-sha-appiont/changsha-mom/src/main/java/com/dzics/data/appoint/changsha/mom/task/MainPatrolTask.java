package com.dzics.data.appoint.changsha.mom.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.appoint.changsha.mom.model.entity.DzicsMaintenancePatrol;
import com.dzics.data.appoint.changsha.mom.service.DzicsMaintenancePatrolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xnb
 * @date 2022/12/8 0008 13:09
 */
@Slf4j
@Component
public class MainPatrolTask {

    @Autowired
    private DzicsMaintenancePatrolService patrolService;

    /**
     * 启动运行，每5分钟执行一次，检查Mom服务巡检维修列表是否有到期记录
     * */
    @PostConstruct
    @Scheduled(cron = "0 0/5 * * * ?")
    public void validStatus(){
        List<DzicsMaintenancePatrol> patrols = patrolService.getBaseMapper().selectList(new QueryWrapper<DzicsMaintenancePatrol>().eq("is_show",1));
        if(!CollectionUtils.isEmpty(patrols)){
            List<DzicsMaintenancePatrol>list=new ArrayList<>();
            for (DzicsMaintenancePatrol patrol : patrols) {
                LocalDate date = LocalDate.parse(patrol.getExecuteData(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if(date.compareTo(LocalDate.now())<=0){
                    patrol.setIsShow(0);
                    patrol.setUpdateTime(new Date());
                    list.add(patrol);
                }
            }
            if(CollectionUtils.isEmpty(list)){

                return;
            }
            patrolService.updateBatchById(list, list.size());
        }
    }

}
