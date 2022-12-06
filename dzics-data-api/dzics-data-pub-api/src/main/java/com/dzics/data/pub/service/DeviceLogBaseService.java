package com.dzics.data.pub.service;

import com.dzics.data.common.base.dto.log.ReatimLogRes;

import java.util.List;

/**
 * @author Administrator
 */
public interface DeviceLogBaseService {

    /**
     * @param orderNo 订单号
     * @param lineNo 产线号
     * @param logType 日志类型
     * @return 日志列表
     */
    List<ReatimLogRes> getReatimLogRes(String orderNo, String lineNo, Integer logType);

}
