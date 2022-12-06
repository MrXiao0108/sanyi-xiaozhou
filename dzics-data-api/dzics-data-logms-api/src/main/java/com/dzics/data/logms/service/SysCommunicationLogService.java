package com.dzics.data.logms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.logms.model.dto.CommuLogPrm;
import com.dzics.data.logms.model.entity.SysCommunicationLog;

import java.util.List;

/**
 * <p>
 * 通信日志 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-08
 */
public interface SysCommunicationLogService extends IService<SysCommunicationLog> {

    void delCommunicationLog(Integer delCommunicationLog, String orderNo, String lineNo, String device_type, String device_code);

    /**
     * 通信日志
     * @param pageLimit
     * @param commuLogPrm
     * @return
     */
    Result<List<SysCommunicationLog>> communicationLog(PageLimit pageLimit, CommuLogPrm commuLogPrm);

    /**
     * 通信指令日志
     * @param pageLimit
     * @param commuLogPrm
     * @return
     */
    Result communicationLogTcp(PageLimit pageLimit, CommuLogPrm commuLogPrm);

}
