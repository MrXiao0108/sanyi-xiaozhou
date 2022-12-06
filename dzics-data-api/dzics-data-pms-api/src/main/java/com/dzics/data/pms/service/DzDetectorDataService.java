package com.dzics.data.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.model.entity.DzDetectorData;
import org.springframework.cache.annotation.CacheEvict;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品检测设置默认模板 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
public interface DzDetectorDataService extends IService<DzDetectorData> {

    /**
     * @param
     * @param productNo 产品id
     * @param detectionResult
     * @param startTime
     * @param endTime
     * @param orgCode
     * @return 检测数据分组
     */
    List<DzDetectorData> groupBuby(String productNo, Integer detectionResult, Date startTime, Date endTime, String orgCode);

    /**
     * @param groupKey 根据key查询检测值
     * @return
     */
    List<DzDetectorData> getByOrderNoProNo(String groupKey);

    /**
     * @return
     */
    List<DzDetectorData> groupBubyData();

    List<Map<String, Object>> getGroupKey(List<String> groupKey);

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
