package com.dzics.data.business.controller.logger;

import com.dzics.data.business.service.LogService;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.logms.model.dto.CommuLogPrm;
import com.dzics.data.logms.model.dto.SysOperationLoggingVo;
import com.dzics.data.logms.model.dto.SysloginVo;
import com.dzics.data.logms.model.entity.SysCommunicationLog;
import com.dzics.data.logms.model.entity.SysLoginLog;
import com.dzics.data.logms.model.entity.SysOperationLogging;
import com.dzics.data.logms.model.entity.SysRealTimeLogs;
import com.dzics.data.logms.service.SysCommunicationLogService;
import com.dzics.data.logms.service.SysRealTimeLogsService;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.logms.model.vo.WarnLogVo;
import com.dzics.data.pub.service.DzProductionLineService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

/**
 * 日志相关api接口
 *
 * @author ZhangChengJun
 * Date 2021/1/15.
 * @since
 */
@Api(tags = {"日志管理"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/user")
@Controller
public class LogController {
    @Autowired
    private SysCommunicationLogService busCommunicationLogService;
    @Autowired
    private LogService logService;
    @Autowired
    private DzProductionLineService lineService;
    @Autowired
    private SysRealTimeLogsService sysRealTimeLogsService;

    @ApiOperation(value = "操作日志", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/log")
    public Result<List<SysOperationLogging>> queryOperLog(
            PageLimit pageLimit, SysOperationLoggingVo sysOperationLoggingVo,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
            @RequestHeader(value = "code", required = false) @ApiParam(value = "用户账号", required = true) String code) {
        return logService.queryOperLog(pageLimit, sub, code, sysOperationLoggingVo);
    }

//    @OperLog(operModul = "日志管理", operType = OperType.DEL, operDesc = "刪除操作日志", operatorType = "后台")
//    @ApiOperation(value = "刪除操作日志", consumes = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperationSupport(author = "jq", order = 112)
//    @DeleteMapping("/delOperLog")
//    public Result delOperLog(
//            @RequestBody List<Integer> ids,
//            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
//            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
//        return busiOperationLog.delOperLog(ids);
//    }

    @ApiOperation(value = "登录日志", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/log/login")
    public Result<SysLoginLog> queryLogin(
            PageLimit pageLimit, SysloginVo sysloginVo,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
            @RequestHeader(value = "code", required = false) @ApiParam(value = "用户账号", required = true) String code) {
        return logService.queryLogin(pageLimit, sysloginVo, sub, code);
    }

//    @OperLog(operModul = "日志管理", operType = OperType.DEL, operDesc = "刪除登录日志", operatorType = "后台")
//    @ApiOperation(value = "刪除登录日志", consumes = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperationSupport(author = "jq", order = 112)
//    @DeleteMapping("/delLoginLog")
//    public Result delLoginLog(
//            @RequestBody List<Integer> ids,
//            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
//            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
//        return loginLogService.delLoginLog(ids);
//    }

    @ApiOperation(value = "通信日志", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @GetMapping("/communication/log")
    public Result<List<SysCommunicationLog>> communicationLog(PageLimit pageLimit,@Valid CommuLogPrm commuLogPrm,
                                                        @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        DzProductionLine byId = lineService.getById(commuLogPrm.getLineId());
        commuLogPrm.setLineNo(byId.getLineNo());
        return busCommunicationLogService.communicationLog(pageLimit, commuLogPrm);
    }

    @ApiOperation(value = "通信日志指令", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @GetMapping("/communication/tcp/log")
    public Result communicationLogTcp(PageLimit pageLimit,@Valid CommuLogPrm commuLogPrm,
                                      @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        DzProductionLine byId = lineService.getById(commuLogPrm.getLineId());
        commuLogPrm.setLineNo(byId.getLineNo());
        return busCommunicationLogService.communicationLogTcp(pageLimit, commuLogPrm);
    }

    @ApiOperation(value = "设备告警日志", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/equipments/warn/log")
    public Result<List<SysRealTimeLogs>> equipmentsWarnLog(@Valid WarnLogVo warnLogVo, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer, @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws ParseException, ParseException {
        return sysRealTimeLogsService.getBackreatimelog(warnLogVo);
    }

}
