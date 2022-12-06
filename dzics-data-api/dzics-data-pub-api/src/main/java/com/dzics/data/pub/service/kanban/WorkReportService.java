package com.dzics.data.pub.service.kanban;

import com.dzics.data.pub.model.dto.workreport.ResponseWorkStationBg;

import java.util.List;

/**
 * @Classname WorkReportService
 * @Description 报工数据
 * @Date 2022/3/15 13:33
 * @Created by NeverEnd
 */
public interface WorkReportService {

    /**
     * 报工看板 以工位展示数据
     * @param qrCode
     * @param orderId
     * @param lineId
     * @return
     */
    List<ResponseWorkStationBg> getPosition(List<String> qrCode, String orderId, String lineId);
}
