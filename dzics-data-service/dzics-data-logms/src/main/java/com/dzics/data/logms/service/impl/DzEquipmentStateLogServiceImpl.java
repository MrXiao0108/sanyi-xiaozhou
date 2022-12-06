package com.dzics.data.logms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.logms.db.dao.DzEquipmentStateLogDao;
import com.dzics.data.logms.model.entity.DzEquipmentStateLog;
import com.dzics.data.logms.service.DzEquipmentStateLogService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 设备运行状态记录表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-20
 */
@Service
public class DzEquipmentStateLogServiceImpl extends ServiceImpl<DzEquipmentStateLogDao, DzEquipmentStateLog> implements DzEquipmentStateLogService {

    @Override
    public void delEquimentLog(Integer i) {
        long time = System.currentTimeMillis() - ((long)i * (24 * 3600 * 1000));
        Date date = new Date(time);
        QueryWrapper<DzEquipmentStateLog> wrapper = new QueryWrapper<>();
        wrapper.le("create_time", date);
        remove(wrapper);
    }
}
