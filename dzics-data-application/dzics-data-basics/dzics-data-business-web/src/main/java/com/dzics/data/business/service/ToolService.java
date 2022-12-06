package com.dzics.data.business.service;

import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.tool.db.model.dao.GetToolInfoDataListDo;
import com.dzics.data.tool.db.model.vo.GetToolInfoDataListVo;
import com.dzics.data.tool.model.dto.AddToolConfigureVo;
import com.dzics.data.tool.model.entity.DzToolCompensationData;

import java.util.List;

public interface ToolService {
    /**
     * 查询刀具配置信息列表
     * @param sub
     * @param pageLimit
     * @param groupNo
     * @return
     */
    Result<List<DzToolCompensationData>> getToolConfigureList(String sub, PageLimit pageLimit, Integer groupNo);

    /**
     * 新增刀具配置信息
     * @param addToolConfigureVo
     * @return
     */
    Result addToolConfigure(AddToolConfigureVo addToolConfigureVo);

    /**
     * 查询所有刀具组
     * @param sub
     * @return
     */
    Result getToolGroupsAll(String sub);

    /**
     * 查询刀具信息数据
     * @param sub
     * @param pageLimit
     * @param getToolInfoDataListVo
     * @return
     */
    Result<List<GetToolInfoDataListDo>> getToolInfoDataList(String sub, PageLimit pageLimit, GetToolInfoDataListVo getToolInfoDataListVo);

    /**
     * 根据设备id 批量绑定刀具信息
     * @param byEquipmentId
     * @return
     */
    Result addToolConfigureById(String byEquipmentId);

    /**
     *
     * @param lineId
     * @return
     */
    Result getEquipmentByLine(String lineId);
}
