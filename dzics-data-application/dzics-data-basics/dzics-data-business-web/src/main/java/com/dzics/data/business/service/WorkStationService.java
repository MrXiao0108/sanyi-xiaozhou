package com.dzics.data.business.service;

import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.AddWorkStation;
import com.dzics.data.pub.model.dto.UpdateWorkStation;
import org.springframework.cache.annotation.CacheEvict;

public interface WorkStationService {
    /**
     * 新增工位
     * @param workStation 工位信息
     * @param sub
     * @return
     */
    Result addWorkingStation(AddWorkStation workStation, String sub);

    /**
     * 编辑工位
     * @param station
     * @param sub
     * @return
     */
    @CacheEvict(cacheNames = {"cacheService.getStationIdMergeCode","cacheService.getStationId"},allEntries = true)
    Result updateWorkingStation(UpdateWorkStation station, String sub);
}
