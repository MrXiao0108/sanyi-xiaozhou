package com.dzics.data.business.service;

import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.db.model.dto.DetectorDataQuery;

import javax.servlet.http.HttpServletResponse;

/**
 * @Classname ProductCheckRecordService
 * @Description 产品检测记录
 * @Date 2022/3/4 15:46
 * @Created by NeverEnd
 */
public interface ProductCheckRecordService {
    /**
     * 检测记录
     *
     * @param detectorDataQuery
     * @param sub
     * @return
     */
    Result selDetectorData(DetectorDataQuery detectorDataQuery, String sub);


    /**
     * 导出 excel
     *
     * @param detectorDataQuery
     * @param sub
     * @return
     */
    void getDetectorExcel1(HttpServletResponse response, DetectorDataQuery detectorDataQuery, String sub);
}
