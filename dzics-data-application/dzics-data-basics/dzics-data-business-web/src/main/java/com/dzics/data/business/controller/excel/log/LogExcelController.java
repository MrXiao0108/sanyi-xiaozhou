package com.dzics.data.business.controller.excel.log;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.data.business.service.LogService;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.write.CustomCellWriteHandler;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.logms.model.dto.CommuLogPrm;
import com.dzics.data.logms.model.dto.SysOperationLoggingVo;
import com.dzics.data.logms.model.dto.SysloginVo;
import com.dzics.data.logms.model.entity.*;
import com.dzics.data.logms.model.vo.SelectEquipmentStateVo;
import com.dzics.data.logms.model.vo.TcpLogProDetection;
import com.dzics.data.logms.model.vo.WarnLogVo;
import com.dzics.data.logms.service.SysCommunicationLogService;
import com.dzics.data.logms.service.SysRealTimeLogsService;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.service.DzProductionLineService;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author Administrator
 */
@Slf4j
@Api(tags = {"Excel管理"}, produces = "Excel管理相关接口")
@RestController
@RequestMapping(value = "/api/excel",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
public class LogExcelController {
    @Autowired
    private DzProductionLineService lineService;
    @Autowired
    private LogService logService;
    @Autowired
    private DzicsUserService userService;
    @Autowired
    private SysCommunicationLogService busCommunicationLogService;
    @Autowired
    private SysRealTimeLogsService sysRealTimeLogsService;

    @ApiOperation(value = "操作日志")
    @GetMapping("/log/operation/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     PageLimit pageLimit, SysOperationLoggingVo sysOperationLoggingVo, HttpServletResponse response) throws IOException {
        String fileNameBase = "操作日志";
        try {
            SysUser byUserName = userService.getByUserName(sub);
            pageLimit.setPage(-1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<SysOperationLogging>> listResult = logService.queryOperLog(pageLimit, sub, byUserName.getUseOrgCode(), sysOperationLoggingVo);
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), SysOperationLogging.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(listResult.getData());
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }



    @ApiOperation(value = "登录日志")
    @GetMapping("/log/login/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     PageLimit pageLimit, SysloginVo sysloginVo, HttpServletResponse response) throws IOException {
        String fileNameBase = "登录日志";
        try {
            SysUser byUserName = userService.getByUserName(sub);
            pageLimit.setPage(-1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result result = logService.queryLogin(pageLimit, sysloginVo, sub, byUserName.getUseOrgCode());
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), SysLoginLog.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite((List) result);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


    @ApiOperation(value = "通信日志")
    @GetMapping("/log/communication/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     @Valid CommuLogPrm commuLogPrm, PageLimit pageLimit, HttpServletResponse response) throws IOException {
        String fileNameBase = "通信日志";
        try {
            pageLimit.setPage(-1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<SysCommunicationLog>> listResult = busCommunicationLogService.communicationLog(pageLimit, commuLogPrm);
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), SysCommunicationLog.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(listResult.getData());
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


    @ApiOperation(value = "设备运行日志")
    @GetMapping("/log/equipmentState/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     PageLimit pageLimit, @Valid SelectEquipmentStateVo selectEquipmentStateVo, HttpServletResponse response) throws IOException {
        String fileNameBase = "设备运行日志";
        try {
            pageLimit.setPage(-1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<DzEquipmentStateLog>> list = logService.list(sub, pageLimit, selectEquipmentStateVo);
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), DzEquipmentStateLog.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(list.getData());
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }

    @ApiOperation(value = "通信日志")
    @GetMapping("/log/communication/tcp/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     PageLimit pageLimit,@Valid CommuLogPrm commuLogPrm, HttpServletResponse response) throws IOException {
        String fileNameBase = "通信日志";
        try {
            pageLimit.setPage(-1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            DzProductionLine byId = lineService.getById(commuLogPrm.getLineId());
            commuLogPrm.setLineNo(byId.getLineNo());
            Result result = busCommunicationLogService.communicationLogTcp(pageLimit, commuLogPrm);
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), TcpLogProDetection.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite((List) result);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


    @ApiOperation(value = "告警日志")
    @GetMapping("/equipments/warn/log/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     @Valid WarnLogVo warnLogVo, HttpServletResponse response) throws IOException {
        String fileNameBase = "设备告警日志";
        try {
            warnLogVo.setPage(-1);
            Result<List<SysRealTimeLogs>> backreatimelog = sysRealTimeLogsService.getBackreatimelog(warnLogVo);
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), SysRealTimeLogs.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(backreatimelog.getData());
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }

}
