package com.dzics.data.business.service;

import com.dzics.data.common.base.model.dto.AddWorkShiftVo;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.*;
import com.dzics.data.pub.model.entity.DzProductionLine;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

/**
 * @author xnb
 * @date 2022年02月09日 13:30
 */
public interface LineService {
    Result add(String sub, AddLineVo data) throws Exception;

    Result<List<LineDo>> list(String sub, LineParmsList lineParmsList);

    @CacheEvict(cacheNames = "cacheService.lineId",allEntries = true)
    Result del(String sub, String id);

    @CacheEvict(cacheNames = "cacheService.lineId",allEntries = true)
    Result pud(String sub, PudLineVo data) throws Exception;

    Result<LineDo> getById(String sub, String id);

    /**
     * 产线排班信息判断
     * @param workShiftVos
     * @return
     */
    boolean lineWorkTime(List<AddWorkShiftVo> workShiftVos) throws Exception;

    /**
     * 查询所有产线 公共方法
     * @param sub
     * @return
     */
    Result allList(String sub);

    /**
     * 根据订单id查询产线列表
     * @param sub
     * @param id
     * @return
     */
    Result<DzProductionLine> getByOrderId(String sub, Long id);


}
