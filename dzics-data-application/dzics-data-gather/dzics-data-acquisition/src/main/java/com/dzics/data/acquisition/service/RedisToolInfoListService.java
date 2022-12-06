package com.dzics.data.acquisition.service;


import com.dzics.data.tool.model.entity.DzToolCompensationData;

import java.util.List;

public interface RedisToolInfoListService {

    /**
     * 查询所有刀具信息
     * @return
     * @param eqId
     */
    List<DzToolCompensationData> getCompensationDataList(String eqId);

    /**
     *更新所有刀具信息
     * @param data
     * @return
     */
    List<DzToolCompensationData> updateCompensationDataList(List<DzToolCompensationData> data);
}
