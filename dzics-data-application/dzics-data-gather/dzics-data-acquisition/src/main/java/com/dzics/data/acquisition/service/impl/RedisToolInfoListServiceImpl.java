package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.acquisition.service.RedisToolInfoListService;
import com.dzics.data.tool.db.dao.DzToolCompensationDataDao;
import com.dzics.data.tool.model.entity.DzToolCompensationData;
import com.dzics.data.tool.service.DzToolCompensationDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RedisToolInfoListServiceImpl implements RedisToolInfoListService {

    @Autowired
    DzToolCompensationDataDao dzToolCompensationDataMapper;
    @Autowired
    DzToolCompensationDataService dzToolCompensationDataService;

    @Override
    public List<DzToolCompensationData> getCompensationDataList(String eqId) {
        List<DzToolCompensationData> dataList = dzToolCompensationDataMapper.selectList(new QueryWrapper<DzToolCompensationData>().eq("equipment_id",eqId));
        return dataList;
    }

    @Override
    public List<DzToolCompensationData> updateCompensationDataList(List<DzToolCompensationData> data) {
        boolean b = dzToolCompensationDataService.updateBatchById(data);
        if(b){
            return data;
        }
        return null;
    }
}
