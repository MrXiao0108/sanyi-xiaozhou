package com.dzics.data.pdm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.pdm.db.dao.DzEquipmentProNumDetailsSignalDao;
import com.dzics.data.pdm.model.dao.UpValueDeviceSignal;
import com.dzics.data.pdm.model.entity.DzEquipmentProNumDetailsSignal;
import com.dzics.data.pdm.service.DzEquipmentProNumDetailsSignalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备生产数量详情表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-23
 */
@Service
public class DzEquipmentProNumDetailsSignalServiceImpl extends ServiceImpl<DzEquipmentProNumDetailsSignalDao, DzEquipmentProNumDetailsSignal> implements DzEquipmentProNumDetailsSignalService {
    @Autowired
    private DzEquipmentProNumDetailsSignalDao detailsSignalMapper;

    @Override
    public UpValueDeviceSignal getupsaveddnumlinnuty(String lineNum, String deviceNum, String deviceType, String orderNumber, Long dayId) {
        return detailsSignalMapper.getupsaveddnumlinnuty(lineNum, deviceNum, deviceType, orderNumber,dayId);
    }
}
