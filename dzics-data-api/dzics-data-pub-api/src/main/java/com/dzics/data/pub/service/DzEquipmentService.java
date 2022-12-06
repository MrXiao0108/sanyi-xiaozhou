package com.dzics.data.pub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.vo.MachiningJC;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.EqIdOrgCode;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.vo.JCEquiment;
import com.dzics.data.pub.model.vo.PutEquipmentDataStateVo;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 设备表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
public interface DzEquipmentService extends IService<DzEquipment> {

    /**
     * 查询设备状态列表
     * @return
     */
    Result getEquipmentState(PageLimit pageLimit, String lineId);


    /**
     * 修改设备的数据展示状态
     * @param stateVo
     * @return
     */
    Result putEquipmentDataState(PutEquipmentDataStateVo stateVo);


    /**
     * 设备状态
     *
     * @param deviceNum
     * @param lineNum
     * @param deviceTypeStr
     * @param orderNumber
     * @return
     */
    @Cacheable(value = "cacheService.getTypeLingEqNo", key = "#deviceNum+#lineNum+#deviceTypeStr+#orderNumber", unless = "#result == null")
    DzEquipment getTypeLingEqNo(String deviceNum, String lineNum, String deviceTypeStr, String orderNumber);

    @Cacheable(value = "cacheService.getTypeLingEqNoPush", key = "#deviceNum+#lineNum+#deviceTypeStr+#orderNumber", unless = "#result == null")
    DzEquipment getTypeLingEqNoPush(String deviceNum, String lineNum, String deviceTypeStr, String orderNumber);

    @CachePut(value = "cacheService.getTypeLingEqNoPush", key = "#upDzDqState.equipmentNo+#upDzDqState.lineNo+#upDzDqState.equipmentType+#upDzDqState.orderNo")
    DzEquipment updateByLineNoAndEqNoPush(DzEquipment upDzDqState);

    /**
     * @param lineNum     产线序号
     * @param deviceNum   设备序号
     * @param deviceType  设备类型
     * @param orderNumber
     * @return
     */
    @Cacheable(value = "cacheService.getDeviceOrgCode", key = "#lineNum+#deviceNum+#deviceType+#orderNumber", unless = "#result == null")
    EqIdOrgCode getDeviceOrgCode(String lineNum, String deviceNum, String deviceType, String orderNumber);

    @Cacheable(cacheNames = {"cacheService.getDeviceId"}, key = "#orderCode+#lineNo+#deviceCode+#deviceType", unless = "#result == null")
    String getDeviceId(String orderCode, String lineNo, String deviceCode, String deviceType);


    @Cacheable(cacheNames = "cacheServiceSig.getTypeLingEqNoDeviceSignalValue", key = "#orderNumber+#lineNum+#deviceType+#deviceNum")
    Integer getTypeLingEqNoDeviceSignalValue(String orderNumber, String lineNum, String deviceType, String deviceNum);

    @Cacheable(value = "cacheService.upDownSum", key = "#lineNum+#deviceNum+#deviceTypeStr+#orderNumber", unless = "#result == null")
    Long upDownSum(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber);

    @CachePut(value = "cacheService.upDownSum", key = "#lineNum+#deviceNum+#deviceTypeStr+#orderNumber")
    Long upDateDownSum(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber, Long dowmSum);

    @Cacheable(value = "cacheService.upDownSumTime", key = "#lineNum+#deviceNum+#deviceTypeStr+#orderNumber", unless = "#result == null")
    Long upDownSumTime(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber);

    @CachePut(value = "cacheService.upDownSumTime", key = "#lineNum+#deviceNum+#deviceTypeStr+#orderNumber")
    Long upDateDownTime(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber, Long downTime);
    /**
     * @param dzEquipment 根据设置序号和产线序号更新设备状态
     */
    int updateByLineNoAndEqNo(DzEquipment dzEquipment);

    List<JCEquiment> listjcjqr();

    /**
     * 获取所有有开始运行时间的设备
     * @return
     */
    List<DzEquipment> getRunStaTimeIsNotNull();

    /**
     *
     * @param deviceId
     * @param localDate
     * @return
     */
    DzEquipment listjcjqrdeviceid(Long deviceId, LocalDate localDate);



    List<DzEquipment> getDeviceOrderNoLineNo(String orderNo, String lineNo);


    int updateByLineNoAndEqNoDownTime(DzEquipment downEq);

    /**
     * 根据产线id查询设备列表
     * @param sub
     * @param id
     * @return
     */
    Result<DzEquipment> getEquipmentByLineId(String sub, Long id);
    MachiningJC getEquimentState(String lineNo, String orderNum, LocalDate now);

    /**
     * 根据产线获取所有设备
     * @param sub
     * @param lineId
     * @return
     */
    Result getDevcieLineId(String sub, String lineId);

    List<DzEquipment> getDzEquipments(String orderNo, String lineNo, EquiTypeEnum jc);

    String getDeviceNo(String eqNo, String orderNo, String lineNo);
}
