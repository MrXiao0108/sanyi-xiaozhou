package com.dzics.data.logms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.logms.model.entity.DzEquipmentStateLog;

/**
 * <p>
 * 设备运行状态记录表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-20
 */
public interface DzEquipmentStateLogService extends IService<DzEquipmentStateLog> {

    void delEquimentLog(Integer delEquipmentLog);

}
