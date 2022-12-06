package com.dzics.data.logms.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.enums.OperTypeStatus;
import com.dzics.data.common.base.model.vo.UserTokenMsg;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.logms.db.dao.SysLoginLogDao;
import com.dzics.data.logms.model.entity.SysLoginLog;
import com.dzics.data.logms.service.SysLoginLogService;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;


/**
 * <p>
 * 登陆日志 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-06
 */
@Service
@Slf4j
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogDao, SysLoginLog> implements SysLoginLogService {
    @Override
    public void delLoginLog(int i) {
        long time = System.currentTimeMillis() - ((long)i * (24 * 3600 * 1000));
        Date date = new Date(time);
        QueryWrapper<SysLoginLog> wrapper = new QueryWrapper<>();
        wrapper.le("create_time", date);
        remove(wrapper);
    }


    @Override
    public void saveLogin(UserAgent userAgent, String ipAddr, UserTokenMsg login, Map<String, String> userObj, Exception e, String orgCode) {
        Date date = new Date();
        String username = userObj.get("username");
        SysLoginLog sysLoginLog = new SysLoginLog();
        sysLoginLog.setUserName(username);
        sysLoginLog.setOperIp(ipAddr);
        sysLoginLog.setBrowser(JSON.toJSONString(userAgent.getBrowser()));
        sysLoginLog.setOperatingSystem(JSON.toJSONString(userAgent.getOperatingSystem()));
        OperTypeStatus status = login == null ? OperTypeStatus.ERROR : OperTypeStatus.SUCCESS;
        sysLoginLog.setLoginStatus(status);
        sysLoginLog.setLoginMsg(login == null ? JSON.toJSONString(e.getMessage()) : JSON.toJSONString(status));
        sysLoginLog.setCreateTime(date);
        sysLoginLog.setOrgCode(orgCode);
        save(sysLoginLog);
        String dateTime = DateUtil.dateFormatToStingYmdHms(date);
        log.info("用户：{} ,登录时间：{},状态：{}", username, dateTime, e== null ? status.getDesc():e.getMessage());
    }
}
