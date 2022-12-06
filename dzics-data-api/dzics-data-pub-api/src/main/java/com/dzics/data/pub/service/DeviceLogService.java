package com.dzics.data.pub.service;

import com.dzics.data.common.base.vo.Result;
import org.springframework.stereotype.Component;

public interface DeviceLogService extends DeviceLogBaseService {

    /**
     * 实时日志发送
     *
     * @param orderNo
     * @param lineNo
     * @return
     */
    Result getRealTimeLogsResp(String orderNo, String lineNo, Integer logType);

    /**
     * 实时告警日志
     *
     * @param orderNo
     * @param lineNo
     * @param logType
     * @return
     */
    Result getRealTimeLogWarn(String orderNo, String lineNo, Integer logType);
}
