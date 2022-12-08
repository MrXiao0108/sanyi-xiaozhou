package com.dzics.data.appoint.changsha.mom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.appoint.changsha.mom.db.dao.DzicsMaintenancePatrolDao;
import com.dzics.data.appoint.changsha.mom.model.entity.DzicsMaintenancePatrol;
import com.dzics.data.appoint.changsha.mom.model.vo.ActiveTipsVo;
import com.dzics.data.appoint.changsha.mom.service.ActiveTipsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author xnb
 * @date 2022/12/6 0006 14:43
 */
@Slf4j
@Service
public class ActiveTipsServiceImpl implements ActiveTipsService {

    @Autowired
    private DzicsMaintenancePatrolDao maintenancePatrolDao;


    @Override
    public List<ActiveTipsVo> getActiveTipsVo() {
        List<ActiveTipsVo>activeTipsVos=new ArrayList<>();
        List<DzicsMaintenancePatrol> patrols = maintenancePatrolDao.selectList(new QueryWrapper<DzicsMaintenancePatrol>().eq("model_type", "1").eq("is_show", "0"));
        if(!CollectionUtils.isEmpty(patrols)){
            for (DzicsMaintenancePatrol patrol : patrols) {
                ActiveTipsVo activeTipsVo = new ActiveTipsVo();
                activeTipsVo.setId(patrol.getId());
                //1：巡检    2：维修
                if(1==patrol.getType()){
                    activeTipsVo.setMessage("有巡检项到期，请及时处理");
                    activeTipsVo.setModelType("4");
                }else{
                    activeTipsVo.setMessage("有维修项到期，请及时处理");
                    activeTipsVo.setModelType("1");
                }
                activeTipsVo.setDataTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(patrol.getUpdateTime()));
                activeTipsVos.add(activeTipsVo);
            }
            //时间降序排序
            activeTipsVos.sort(Comparator.comparing(ActiveTipsVo::getDataTime).reversed());
        }
        return activeTipsVos;
    }
}
