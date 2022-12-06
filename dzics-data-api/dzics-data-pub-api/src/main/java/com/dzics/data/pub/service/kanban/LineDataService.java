package com.dzics.data.pub.service.kanban;


import com.dzics.data.pdm.model.entity.DzWorkpieceData;
import com.dzics.data.pub.model.dao.SelectTrendChartDo;

public interface LineDataService {
    /**
     * 根据机床区分检测数据趋势图
     * @param dzWorkpieceData
     * @param orderNo
     * @param lineNo
     * @return
     */
    SelectTrendChartDo getDetectionByMachine(DzWorkpieceData dzWorkpieceData, String orderNo, String lineNo);


}
