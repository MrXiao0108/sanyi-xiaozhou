package com.dzics.data.logms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.model.vo.UserTokenMsg;
import com.dzics.data.logms.model.entity.SysLoginLog;
import eu.bitwalker.useragentutils.UserAgent;

import java.util.Map;

/**
 * <p>
 * 登陆日志 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-06
 */
public interface SysLoginLogService extends IService<SysLoginLog> {

    void delLoginLog(int i);

    void saveLogin(UserAgent userAgent, String ipAddr, UserTokenMsg login, Map<String, String> userObj, Exception o, String orgCode);
}
