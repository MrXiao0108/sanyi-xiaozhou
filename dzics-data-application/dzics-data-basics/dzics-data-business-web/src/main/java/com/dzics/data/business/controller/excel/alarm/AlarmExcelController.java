package com.dzics.data.business.controller.excel.alarm;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.model.write.CustomCellWriteHandler;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.GetDeivceAlarmConfig;
import com.dzics.data.pub.model.entity.DzDeviceAlarmConfig;
import com.dzics.data.pub.service.DzDeviceAlarmConfigService;
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
@Api(tags = {"Excel管理"}, produces = "产品Excel管理相关接口")
@RestController
@RequestMapping(value = "/api/excel",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
@Slf4j
public class AlarmExcelController {

    @Autowired
    private DzDeviceAlarmConfigService deviceAlarmConfig;
    @Autowired
    private DzicsUserService userService;

    @ApiOperation(value = "告警配置")
    @GetMapping("/equipment/alarm/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     GetDeivceAlarmConfig alarmConfig, HttpServletResponse response) throws IOException {
        String fileNameBase = "告警配置";
        try {
            alarmConfig.setPage(-1);
            alarmConfig.setLimit(FinalCode.SELECT_SUM_EXCEL);
            SysUser byUserName = userService.getByUserName(sub);
            Result<List<DzDeviceAlarmConfig>> giveAlarmConfig = deviceAlarmConfig.getGiveAlarmConfig(alarmConfig, byUserName.getUseOrgCode());
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), DzDeviceAlarmConfig.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(giveAlarmConfig.getData());
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


}
