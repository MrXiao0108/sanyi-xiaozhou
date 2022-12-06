package com.dzics.data.business.service;

import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.model.dto.AddDetectorPro;
import com.dzics.data.pms.model.dto.EditProDuctTemp;
import com.dzics.data.pms.model.dto.ProDuctCheck;
import com.dzics.data.pms.model.vo.GroupId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.LinkedHashMap;
import java.util.List;

public interface PmsService {

    /**
     * 新增检测项设置，获取检测项默认配置
     *
     * @param groupId 检测项id
     * @param sub
     * @return
     */
    Result selDetectorItem(GroupId groupId, String sub);

    /**
     * 新增产品检测配置
     *
     * @param detectorPro
     * @param sub
     * @param useDepartId
     * @return
     */
    @CacheEvict(cacheNames = {"cacheService.getDzProDetectIonTemp"}, allEntries = true)
    Result addDetectorItem(AddDetectorPro detectorPro, String sub, String useDepartId);

    /**
     * 检测配置列表
     *
     * @param proDuctCheck
     * @param sub
     * @return
     */
    Result<List<LinkedHashMap<String, Object>>> queryProDetectorItem(ProDuctCheck proDuctCheck, String sub);


    /**
     * 删除检测配置
     *
     * @param groupId 同组配置id
     * @param sub
     * @return
     */
    @CacheEvict(cacheNames = {"cacheService.getDzProDetectIonTemp",
            "dzDetectionTemplCache.getByOrderNoProNo"}, allEntries = true)
    Result delProDetectorItem(String groupId, String sub);


    /**
     * 修改检测产品配置
     *
     * @param templateParm
     * @param sub
     * @return
     */

    @CacheEvict(cacheNames = {"cacheService.getDzProDetectIonTemp",
            "dzDetectionTemplCache.getByOrderNoProNo",
            "dzDetectionTemplCache.getByOrderNoProNoIsShow",
            "dzDetectionTemplCache.getDataValue"}, allEntries = true)
    Result editProDetectorItem(EditProDuctTemp templateParm, String sub);

    /**
     * 对比值修改
     *
     * @param editProDuctTemp
     * @param sub
     * @return
     */
    @CacheEvict(cacheNames = {"cacheService.getDzProDetectIonTemp"}, allEntries = true)
    Result dbProDetectorItem(EditProDuctTemp editProDuctTemp, String sub);


    /**
     * @param productNo 产品序号
     * @return 返回产品关联站点
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.getByProeuctNoDepartId", key = "#productNo")
    String getByProeuctNoDepartId(String productNo);



}
