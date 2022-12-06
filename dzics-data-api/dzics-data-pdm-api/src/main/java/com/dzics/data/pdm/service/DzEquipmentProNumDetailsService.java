package com.dzics.data.pdm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.pdm.model.dao.UpValueDevice;
import com.dzics.data.pdm.model.entity.DzEquipmentProNumDetails;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/**
 * <p>
 * 设备生产数量详情表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
public interface DzEquipmentProNumDetailsService extends IService<DzEquipmentProNumDetails> {


    /**
     * @param lineNum 产线序号
     * @param deviceNum 设备序号
     * @param deviceType 设备类型
     * @param orderNumber
     * @return
     */
    UpValueDevice getupsaveddnumlinnuty(String lineNum, String deviceNum, String deviceType, String orderNumber);

    /**
     * @param lineNum        产线序号
     * @param deviceNum      设备序号
     * @param deviceType     设备类型
     * @param orderNumber
     * @param nowValueDevice 当前生产数据
     * @return
     */
    @CachePut(value = "cacheService.getUpValueDevice", key = "#lineNum+#deviceNum+#deviceType+#orderNumber")
    UpValueDevice saveUpValueDevice(String lineNum, String deviceNum, String deviceType, String orderNumber, UpValueDevice nowValueDevice);

    /**
     * @param lineNum     产线序号
     * @param deviceNum   设备序号
     * @param deviceType  设备类型
     * @param orderNumber
     * @return
     */
    @Cacheable(value = "cacheService.getUpValueDevice", key = "#lineNum+#deviceNum+#deviceType+#orderNumber", unless = "#result == null")
    UpValueDevice getUpValueDevice(String lineNum, String deviceNum, String deviceType, String orderNumber);

    void updateByOrderLine(DzEquipmentProNumDetails details);
}
