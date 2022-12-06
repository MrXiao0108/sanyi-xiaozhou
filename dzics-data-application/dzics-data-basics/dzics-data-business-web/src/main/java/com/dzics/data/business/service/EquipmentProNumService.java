package com.dzics.data.business.service;

import com.dzics.data.common.base.model.vo.kanban.WorkShiftSum;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.base.model.dao.QualifiedAndOutputDo;
import com.dzics.data.pdm.model.vo.DayDataDo;
import com.dzics.data.pub.model.entity.DzProductionLine;

import java.util.List;

public interface EquipmentProNumService {
    /**
     * 产出率和合格率
     * @param lineId
     * @return
     */
    Result getOutputAndQualified(String lineId);

    List<WorkShiftSum> getWorkShiftSum(String orderNo,String equimentId);

    Result geDayAndMonthDataV2(String lineId);

    /**
     * 月生产综合报表
     * @return
     */
    DayDataDo monthData(DzProductionLine dzProductionLine);

    /**
     * 日产出率
     * @param lineId
     * @return
     */
    QualifiedAndOutputDo outputCapacity(String lineId);
}
