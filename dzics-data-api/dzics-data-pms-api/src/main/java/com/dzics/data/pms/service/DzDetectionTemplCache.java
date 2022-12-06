package com.dzics.data.pms.service;

import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.model.dto.DetectionData;
import com.dzics.data.pms.model.dto.EditProDuctTemp;
import com.dzics.data.pms.model.dto.ProductParm;
import com.dzics.data.pms.model.entity.DzProductDetectionTemplate;
import com.dzics.data.pms.model.vo.DBDetectTempVo;
import com.dzics.data.pms.model.vo.DzDetectTempVo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 检测模板缓存接口类
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
public interface DzDetectionTemplCache {
    @Cacheable(cacheNames = "dzDetectionTemplCache.list")
    List<DzDetectTempVo> list();

    @Cacheable(cacheNames = "dzDetectionTemplCache.getByOrderNoProNo", key = "#departId+#productNo+#orderId1+#lineId1")
    List<DzProductDetectionTemplate> getByOrderNoProNo(Long departId, String productNo, String orderId1, String lineId1);


    /**
     * 设备名称，产品名称，站点名称
     *
     * @param groupKey
     * @return
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.getGroupKey", key = "#groupKey")
    List<Map<String, Object>> getGroupKey(List<String> groupKey);

    @Cacheable(cacheNames = "dzDetectionTemplCache.getDataValue", key = "#groupKey")
    List<DetectionData> getDataValue(String groupKey);

    /**
     * 根据站点id查询站点下的产品
     *
     * @param departId 站点id
     * @return 产品信息
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.getByDepartId", key = "#departId", unless = "#result == null")
    List<ProductParm> getByDepartId(Long departId);

    /**
     * @param groupId 分组id信息
     * @return 配置信息
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.getEditDetectorItem", key = "#groupId")
    List<DzDetectTempVo> getEditDetectorItem(String groupId);

    /**
     * @param productNo 产品序号
     * @return 返回产品关联站点
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.getByProeuctNoDepartId", key = "#productNo")
    String getByProeuctNoDepartId(String productNo);

    /**
     * 修改检测配置
     *
     * @param templateParm
     * @return
     */
    @CacheEvict(cacheNames = {"dzDetectionTemplCache.getByDepartId",
            "dzDetectionTemplCache.getEditDetectorItem",
            "dzDetectionTemplCache.getByProeuctNoDepartId", "dzDetectionTemplCache.getEditDBDetectorItem"}, allEntries = true)
    Result editProDetectorItem(EditProDuctTemp templateParm);

    /**
     * 对比值修改
     *
     * @param editProDuctTemp
     * @return
     */
    @CacheEvict(cacheNames = {"dzDetectionTemplCache.getByDepartId",
            "dzDetectionTemplCache.getEditDetectorItem",
            "dzDetectionTemplCache.getByProeuctNoDepartId", "dzDetectionTemplCache.getEditDBDetectorItem"}, allEntries = true)
    Result dbProDetectorItem(EditProDuctTemp editProDuctTemp);

    /**
     * @param groupId 获取对比检测值
     * @return
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.getEditDBDetectorItem", key = "#groupId")
    List<DBDetectTempVo> getEditDBDetectorItem(String groupId);

    @CacheEvict(cacheNames = {"dzDetectionTemplCache.getByDepartId",
            "dzDetectionTemplCache.getEditDetectorItem",
            "dzDetectionTemplCache.getByProeuctNoDepartId", "dzDetectionTemplCache.getEditDBDetectorItem"}, allEntries = true)
    boolean delGroupupId(Long groupId);

    @Cacheable(cacheNames = "dzDetectionTemplCache.getByOrderNoProNoIsShow", key = "#departId+#productNo")
    List<DzProductDetectionTemplate> getByOrderNoProNoIsShow(long departId, String productNo);

    /**
     * 根据订单号和产线号查询产线    清除缓存
     */
    @CacheEvict(cacheNames={"dzDetectionTemplCache.getLineIdByOrderNoLineNo"}, allEntries = true)
    void deleteLineIdByOrderNoLineNo();


    @Cacheable(value = "cacheService.getProductNameFrequency",key = "#lineType+#productAlias")
    BigDecimal getProductNameFrequency(String lineType, String productAlias);

    /**
     * 删除检测配置
     *
     * @param groupId 同组配置id
     * @param sub
     * @return
     */
    @CacheEvict(cacheNames = {"cacheService.getDzProDetectIonTemp",
            "dzDetectionTemplCache.getByOrderNoProNo"}, allEntries = true)
    Result delProDetectorItem(Long groupId, String sub);

}
