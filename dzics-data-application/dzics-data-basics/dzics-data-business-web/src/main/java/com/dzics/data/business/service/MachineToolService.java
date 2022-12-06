package com.dzics.data.business.service;

import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.db.model.dao.EquipmentDataDo;
import com.dzics.data.pdm.db.model.dto.EquipmentDownExcelDo;
import com.dzics.data.pdm.db.model.dto.SelectEquipmentDataVo;
import com.dzics.data.pdm.model.dto.GetByEquipmentNoVo;
import com.dzics.data.pdm.model.vo.MachineDownExcelVo;
import com.dzics.data.pub.model.dto.SelectEquipmentVo;
import com.dzics.data.pub.model.vo.EquipmentDo;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface MachineToolService {
    /**
     * 根据设备类型和搜索条件查询设备数据
     * @param sub
     * @param robotEquipmentCode 设备类型
     * @param selectEquipmentDataVo 查询条件
     * @return
     */
    Result<List<EquipmentDataDo>> listEquipmentData(String sub, Integer robotEquipmentCode, SelectEquipmentDataVo selectEquipmentDataVo);

    /**
     * 获取系统运行模式
     *
     * @param sub
     * @return
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.systemRunModel", key = "'runModel'")
    Result systemRunModel(String sub);

    Result<List<EquipmentDo>> list(String sub, Integer type, PageLimit pageLimit, SelectEquipmentVo data);

    Result getByEquipmentNo(String sub, GetByEquipmentNoVo getByEquipmentNoVo, PageLimit pageLimit);

    /**
     * 机器人停机记录 Excel导出
     * */
    Result<List<MachineDownExcelVo>> getMachineDownExcel(String sub, EquipmentDownExcelDo downExcelDo);
}
