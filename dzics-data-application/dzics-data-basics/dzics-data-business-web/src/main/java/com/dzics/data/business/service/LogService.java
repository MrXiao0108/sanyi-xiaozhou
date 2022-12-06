package com.dzics.data.business.service;

import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.logms.model.dto.SysOperationLoggingVo;
import com.dzics.data.logms.model.dto.SysloginVo;
import com.dzics.data.logms.model.entity.DzEquipmentStateLog;
import com.dzics.data.logms.model.entity.SysOperationLogging;
import com.dzics.data.logms.model.entity.SysRealTimeLogs;
import com.dzics.data.logms.model.vo.SelectEquipmentStateVo;

import java.util.List;

public interface LogService {

    Result<List<DzEquipmentStateLog>> list(String sub, PageLimit pageLimit, SelectEquipmentStateVo selectEquipmentStateVo);

    /**
     * @param pageLimit 分页参数
     * @param sub 操作用户
     * @param code
     * @param sysOperationLoggingVo
     * @return
     */
    Result<List<SysOperationLogging>> queryOperLog(PageLimit pageLimit, String sub, String code, SysOperationLoggingVo sysOperationLoggingVo);


    /**
     * 登录日志查询
     * @param pageLimit
     * @param sysloginVo
     * @param sub
     * @param code
     * @return
     */
    Result queryLogin(PageLimit pageLimit, SysloginVo sysloginVo, String sub, String code);

    /**
     * 根据ids删除设备运行日志
     * @param ids
     * @return
     */
    void delEquimentLog(Integer ids);


    /**
     * 首页 获取设备告警日志
     *
     * @param orderId
     * @return List<SysRealTimeLogs>
     */
    List<SysRealTimeLogs> getIndexWarnLog(String orderId);

}
