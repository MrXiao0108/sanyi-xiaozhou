package com.dzics.data.pdm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.model.vo.MachiningJC;
import com.dzics.data.pdm.model.entity.DzEquipmentProNum;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;

/**
 * <p>
 * 设备生产数量表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
public interface DzEquipmentProNumService extends IService<DzEquipmentProNum> {
    /**
     *
     *
     * @param orderNumber
     * @param deviceId
     * @param id          每日排班中的 班次id
     * @param productType
     * @param batchNumber
     * @param modelNumber
     * @param hour
     * @return
     */
    @Cacheable(cacheNames = "cacheService.getDzDayEqProNum", key = "#id+#productType+#batchNumber+#modelNumber+#hour", unless = "#result == null")
    DzEquipmentProNum getDzDayEqProNum(String orderNumber, String deviceId, String id, String productType, String batchNumber, String modelNumber, int hour);

    @CachePut(cacheNames = "cacheService.getDzDayEqProNum", key = "#dzEqProNum.dayId+#dzEqProNum.productType+#dzEqProNum.batchNumber+#dzEqProNum.modelNumber")
    DzEquipmentProNum saveDzEqProNum(DzEquipmentProNum dzEqProNum);

    @CachePut(cacheNames = "cacheService.getDzDayEqProNum", key = "#proNum.dayId+#proNum.productType+#proNum.batchNumber+#proNum.modelNumber")
    DzEquipmentProNum updateDzEqProNum(DzEquipmentProNum proNum);


    /**
     * 排班任务
     */
    void arrange();



    DzEquipmentProNum getDzEquipmentProNum(Long id);



    MachiningJC machiningNumTotals(MachiningJC jc, LocalDate now, String tableKey, String orderNo);

}
