package com.dzics.data.pub.service.kanban;

import org.springframework.stereotype.Component;

/**
 * @Classname AnrenDetectorDataService
 * @Description 检测记录接口
 * @Date 2022/3/13 16:51
 * @Created by NeverEnd
 */
@Component
public interface DetectorDataService<R> {
    /**
     * 产品检测数据
     *
     * @return
     * @param orderNo
     * @param lineNo
     * @param modelNumber
     */
    R getDetectorData(String orderNo, String lineNo, String modelNumber);
}
