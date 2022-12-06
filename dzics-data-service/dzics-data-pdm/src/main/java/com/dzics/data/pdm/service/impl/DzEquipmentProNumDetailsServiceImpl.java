package com.dzics.data.pdm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.pdm.db.dao.DzEquipmentProNumDetailsDao;
import com.dzics.data.pdm.model.dao.UpValueDevice;
import com.dzics.data.pdm.model.entity.DzEquipmentProNumDetails;
import com.dzics.data.pdm.service.DzEquipmentProNumDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备生产数量详情表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Service
@Slf4j
public class DzEquipmentProNumDetailsServiceImpl extends ServiceImpl<DzEquipmentProNumDetailsDao, DzEquipmentProNumDetails> implements DzEquipmentProNumDetailsService {

    @Override
    public UpValueDevice saveUpValueDevice(String lineNum, String deviceNum, String deviceType, String orderNumber, UpValueDevice nowValueDevice) {
        return nowValueDevice;
    }

    @Override
    public UpValueDevice getUpValueDevice(String lineNum, String deviceNum, String deviceType, String orderNumber) {
        UpValueDevice upValueDevice = getupsaveddnumlinnuty(lineNum, deviceNum, deviceType, orderNumber);
        if (upValueDevice == null) {
//                    上次数据赋值默认0
            upValueDevice = new UpValueDevice();
            upValueDevice.setWorkNum(0L);
            upValueDevice.setTotalNum(0L);
            upValueDevice.setQualifiedNum(0L);
            upValueDevice.setTotalQualifiedNum(0L);
            upValueDevice.setRoughNum(0L);
            upValueDevice.setTotalRoughNum(0L);
            upValueDevice.setBadnessNum(0L);
            upValueDevice.setTotalBadnessNum(0L);
            log.warn("上次班次生产数据不存在,设置默认值:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, upValueDevice);
        }
        return upValueDevice;
    }

    @Override
    public void updateByOrderLine(DzEquipmentProNumDetails details) {
        QueryWrapper<DzEquipmentProNumDetails> wp = new QueryWrapper<>();
        wp.eq("order_no",details.getOrderNo());
        wp.eq("line_no",details.getLineNo());
        wp.eq("device_type",details.getDeviceType());
        wp.eq("equipment_no",details.getEquipmentNo());
        update(details, wp);
    }

    @Override
    public UpValueDevice getupsaveddnumlinnuty(String lineNum, String deviceNum, String deviceType, String orderNumber) {
        return this.baseMapper.getupsaveddnumlinnuty(lineNum, deviceNum, deviceType, orderNumber);
    }
}
