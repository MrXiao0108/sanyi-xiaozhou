package com.dzics.data.business.controller.excel.maintainCheck;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.write.CustomCellWriteHandler;
import com.dzics.data.common.base.vo.BaseTimeLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.maintain.model.dto.GetDeviceCheckVo;
import com.dzics.data.maintain.model.dto.MaintainDeviceParms;
import com.dzics.data.maintain.model.vo.GetDeviceCheckDo;
import com.dzics.data.maintain.model.vo.MaintainDevice;
import com.dzics.data.maintain.service.DzCheckHistoryService;
import com.dzics.data.maintain.service.DzMaintainDeviceService;
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
public class MainCheckExcelController {
    @Autowired
    private DzMaintainDeviceService busMaintainDeviceService;
    @Autowired
    private DzicsUserService userService;
    @Autowired
    private DzCheckHistoryService checkHistoryService;

    @ApiOperation(value = "保养记录")
    @GetMapping("/equipment/maintain/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     BaseTimeLimit pageLimit, MaintainDeviceParms parmsReq, HttpServletResponse response) throws IOException {
        String fileNameBase = "保养记录";
        try {
            pageLimit.setPage(-1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            SysUser byUserName = userService.getByUserName(sub);
            String useOrgCode = byUserName.getUseOrgCode();
            Result<List<MaintainDevice>> list = busMaintainDeviceService.getMaintainList(sub, pageLimit, parmsReq, useOrgCode);
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), MaintainDevice.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(list.getData());
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


    @ApiOperation(value = "设备巡检项记录")
    @GetMapping("/equipment/Inspection/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     GetDeviceCheckVo getDeviceCheckVo, HttpServletResponse response) throws IOException {
        SysUser byUserName = userService.getByUserName(sub);
        getDeviceCheckVo.setUseOrgCode(byUserName.getUseOrgCode());
        String fileNameBase = "设备巡检项记录";
        try {
            getDeviceCheckVo.setPage(-1);
            getDeviceCheckVo.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<GetDeviceCheckDo>> list = checkHistoryService.list(getDeviceCheckVo);
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), GetDeviceCheckDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(list.getData());
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }



}
