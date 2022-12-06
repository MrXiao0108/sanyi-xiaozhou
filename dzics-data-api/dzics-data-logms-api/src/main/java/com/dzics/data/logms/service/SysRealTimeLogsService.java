package com.dzics.data.logms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.dto.log.ReatimLogRes;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.logms.model.entity.SysRealTimeLogs;
import com.dzics.data.logms.model.vo.WarnLogVo;

import java.text.ParseException;
import java.util.List;


/**
 * <p>
 * 设备运行告警日志 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
public interface SysRealTimeLogsService extends IService<SysRealTimeLogs> {
    /**
     * 保存日志
     * @param rabbitmqMessage 日志信息
     * @return 日志信息
     */
    List<SysRealTimeLogs> saveRealTimeLog(RabbitmqMessage rabbitmqMessage) throws ParseException;

    /**
     * @param orderNo 订单号
     * @param lineNo 产线号
     * @param logtype 日志类型
     * @param size 查询条数
     * @return 日志列表
     */
    List<ReatimLogRes> getReatimeLogsType(String orderNo, String lineNo,String logtype,Integer size);

    /**
     * @param orderNo 订单号
     * @param lineNo 产线号
     * @param deviceType 设备类型
     * @param deviceCode 设备编码
     * @param logtype 日志类型
     * @param size 查询条数
     * @return 日志列表
     */
    List<ReatimLogRes> getReatimeLog(String orderNo, String lineNo, String deviceType, String deviceCode, Integer logtype, Integer size);


    void delJobExecutionLog(int i);

    void delJobStatusTraceLog(int i);

    /**
     * @param warnLogVo
     * @return List<ReatimLogRes>
     */
    Result<List<SysRealTimeLogs>> getBackreatimelog(WarnLogVo warnLogVo) throws ParseException;
}
