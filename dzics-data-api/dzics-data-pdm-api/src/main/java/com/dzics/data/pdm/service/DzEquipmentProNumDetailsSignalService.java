package com.dzics.data.pdm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.pdm.model.dao.UpValueDeviceSignal;
import com.dzics.data.pdm.model.entity.DzEquipmentProNumDetailsSignal;

/**
 * <p>
 * 设备生产数量详情表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-23
 */
public interface DzEquipmentProNumDetailsSignalService extends IService<DzEquipmentProNumDetailsSignal> {

    UpValueDeviceSignal getupsaveddnumlinnuty(String lineNum, String deviceNum, String deviceType, String orderNumber, Long dayId);
}
