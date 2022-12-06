package com.dzics.data.pub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.PutProcessShowVo;
import com.dzics.data.pub.model.dto.SelWorkStation;
import com.dzics.data.pub.model.entity.DzWorkStationManagement;
import com.dzics.data.pub.model.vo.ResWorkStation;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * <p>
 * 工位表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
public interface DzWorkStationManagementService extends IService<DzWorkStationManagement> {


    boolean putOnoffShow(PutProcessShowVo processShowVo);

    Result getLineId(String lineId);

    /**
     * 删除工位
     * @param stationId
     * @param sub
     * @return
     */
    Result delWorkingStation(String stationId, String sub);


    /**
     * 获取工位列表
     * @param selWorkStation
     * @param sub
     * @param useOrgCode
     * @return
     */
    Result<List<ResWorkStation>> getWorkingStation(SelWorkStation selWorkStation, String sub, String useOrgCode);

    /**
     * 工位是否展示
     * @param
     * @param
     * @return
     */
    Result putOnoff(PutProcessShowVo pShow);

    @Cacheable(cacheNames = "cacheService.getStationId", key = "#deviceCode+#orderId+#lineId", unless = "#result == null")
    DzWorkStationManagement getWorkStationCode(String deviceCode, String orderId, String lineId);

    DzWorkStationManagement getStationIdMergeCode(String mergeCode, String deviceCode, String orderId, String lineId);
}
