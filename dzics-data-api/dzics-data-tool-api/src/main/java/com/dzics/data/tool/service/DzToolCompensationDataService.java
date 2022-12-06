package com.dzics.data.tool.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.tool.model.dto.AddToolConfigureVo;
import com.dzics.data.tool.model.entity.DzToolCompensationData;

/**
 * <p>
 * 刀具补偿数据表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
public interface DzToolCompensationDataService extends IService<DzToolCompensationData> {
    /**
     * 删除刀具配置信息
     * @param id
     * @return
     */
    Result delToolConfigure(String id);

    /**
     * 查询设备指定刀具组下为绑定的刀具
     * @param equipmentId
     * @param groupNo
     * @return
     */
    Result getToolByEqIdAndGroupNo(Long equipmentId, Integer groupNo,Long toolGroupsId);

    /**
     * 修改刀具配置信息
     * @param addToolConfigureVo
     * @return
     */
    Result putToolConfigure(AddToolConfigureVo addToolConfigureVo);

    /**
     * 根据id查询刀具配置详情
     * @param id
     * @return
     */
    Result getToolConfigureById(String id);
}
