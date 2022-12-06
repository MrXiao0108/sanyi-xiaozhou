package com.dzics.data.kanban.changsha.changpaoguang.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.common.base.dto.GetToolInfoDataDo;
import com.dzics.data.common.base.dto.ToolDataDo;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.service.DzEquipmentService;
import com.dzics.data.pub.service.kanban.ToolCompService;
import com.dzics.data.tool.db.dao.DzToolCompensationDataDao;
import com.dzics.data.tool.model.entity.DzToolCompensationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname PaoGuangToolCompServiceImpl
 * @Description 刀具接口实现
 * @Date 2022/3/15 9:33
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class PaoGuangToolCompServiceImpl implements ToolCompService<List<GetToolInfoDataDo>> {
    @Autowired
    private DzToolCompensationDataDao compensationDataDao;
    @Autowired
    private DzEquipmentService equipmentService;
    /**
     * 刀具信息数据
     *
     * @param orderNo
     * @param lineNo
     * @return
     */
    @Override
    public List<GetToolInfoDataDo> getToolInfoData(String orderNo, String lineNo) {
        List<GetToolInfoDataDo> dataList = new ArrayList<>();
        List<DzEquipment> dzEquipments = equipmentService.getDzEquipments(orderNo, lineNo, EquiTypeEnum.JC);
        if (CollectionUtils.isEmpty(dzEquipments)) {
            return dataList;
        }
        String orgCode = dzEquipments.get(0).getOrgCode();
        QueryWrapper<DzToolCompensationData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("org_code", orgCode);
        queryWrapper.orderByAsc("tool_no");
        List<DzToolCompensationData> dzToolCompensationData = compensationDataDao.selectList(queryWrapper);

        if (CollectionUtils.isEmpty(dzToolCompensationData)) {
            return dataList;
        }
        for (DzEquipment dzEquipment : dzEquipments) {
            GetToolInfoDataDo getToolInfoDataDo = new GetToolInfoDataDo();
            getToolInfoDataDo.setEquipmentId(dzEquipment.getId());
            getToolInfoDataDo.setEquipmentName(dzEquipment.getEquipmentName());
            getToolInfoDataDo.setEquipmentNo(dzEquipment.getEquipmentNo());
            //设备绑定的刀具信息集合
            List<ToolDataDo> toolDataDos = new ArrayList<>();
            for (DzToolCompensationData data : dzToolCompensationData) {
                if (data.getEquipmentId() != null && data.getEquipmentId().equals(dzEquipment.getId())) {
                    ToolDataDo toolDataDo = new ToolDataDo();
                    toolDataDo.setToolLife(data.getToolLife());
                    toolDataDo.setToolLifeCounter(data.getToolLifeCounter());
                    if (data.getToolNo() != null) {
                        if (data.getToolNo().intValue() < 10) {
                            toolDataDo.setToolNo("T0" + data.getToolNo().toString());
                        } else {
                            toolDataDo.setToolNo("T" + data.getToolNo().toString());
                        }
                    }
                    toolDataDos.add(toolDataDo);
                }
            }
            //填充设备绑定的刀具信息集合
            getToolInfoDataDo.setToolDataList(toolDataDos);
            //填充数据到设备集合里面
            dataList.add(getToolInfoDataDo);
        }
        return dataList;
    }
}
