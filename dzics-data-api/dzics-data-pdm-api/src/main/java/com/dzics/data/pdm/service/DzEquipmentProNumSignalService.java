package com.dzics.data.pdm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.dto.GetOrderNoLineNo;
import com.dzics.data.pdm.model.entity.DzEquipmentProNumSignal;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <p>
 * 班次生产记录表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-23
 */
public interface DzEquipmentProNumSignalService extends IService<DzEquipmentProNumSignal> {


    void updateDzEqProNum(DzEquipmentProNumSignal dzEquipmentProNumSignal);

    DzEquipmentProNumSignal saveDzEqProNum(DzEquipmentProNumSignal dzEquipmentProNumSignal);


    Long getEquimentIdDayProNum(Long id, LocalDate nowDay, String tableKey);


    @Cacheable(cacheNames = "cacheServiceSig.getDzDayEqProNumSig", key = "#id+#productType+#batchNumber+#modelNumber+#hour", unless = "#result == null")
    DzEquipmentProNumSignal getDzDayEqProNumSig(String orderNumber, String deviceId, String id, String productType, String batchNumber, String modelNumber, int hour);

    @CachePut(cacheNames = "cacheServiceSig.getDzDayEqProNumSig", key = "#dzEquipmentProNumSignal.dayId+#dzEquipmentProNumSignal.productType+#dzEquipmentProNumSignal.batchNumber+#dzEquipmentProNumSignal.modelNumber+#dzEquipmentProNumSignal.workHour")
    DzEquipmentProNumSignal updateDzEqProNumSignal(DzEquipmentProNumSignal dzEquipmentProNumSignal);

    /**
     * @param dzEquipmentProNumSignal
     * @return
     */
    @CachePut(cacheNames = "cacheServiceSig.getDzDayEqProNumSig", key = "#dzEquipmentProNumSignal.dayId+#dzEquipmentProNumSignal.productType+#dzEquipmentProNumSignal.batchNumber+#dzEquipmentProNumSignal.modelNumber+#dzEquipmentProNumSignal.workHour")
    DzEquipmentProNumSignal saveDzEqProNumSignal(DzEquipmentProNumSignal dzEquipmentProNumSignal);

    /**
     * 获取某个设备的当班的总合格生产数量
     * @Parme getOrderNoLineNo
     * @Parme equipmentId
     * @Parme orderNo
     * @return Long
     * */
    BigDecimal getSumQuaNum(GetOrderNoLineNo getOrderNoLineNo, String equipmentId, String orderNo);

}
