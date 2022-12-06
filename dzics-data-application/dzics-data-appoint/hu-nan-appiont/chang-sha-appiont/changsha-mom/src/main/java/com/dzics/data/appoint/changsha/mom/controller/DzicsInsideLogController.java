package com.dzics.data.appoint.changsha.mom.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.data.appoint.changsha.mom.model.entity.DzicsInsideLog;
import com.dzics.data.appoint.changsha.mom.model.vo.DzicsInsideVo;
import com.dzics.data.appoint.changsha.mom.service.DzicsInsideLogService;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.write.CustomCellWriteHandler;
import com.dzics.data.common.base.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xnb
 * @since 2022-11-12
 */

@Slf4j
@Api(tags = {"Mom通讯日志"})
@RestController
@RequestMapping("/api/dzics/inside/log")
public class DzicsInsideLogController {

    @Autowired
    private DzicsInsideLogService dzicsInsideLogService;

    @ApiOperation(value = "查询其他日志", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/inside/log/query")
    public Result<List<DzicsInsideLog>> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,DzicsInsideVo insideVo) throws ParseException {
        return Result.ok(dzicsInsideLogService.getBackInsideLog(insideVo));
    }

    @ApiOperation(value = "其他日志导出")
    @GetMapping("/inside/log/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     DzicsInsideVo insideVo, HttpServletResponse response) throws IOException {
        String fileNameBase = "Dzics内部日志";
        try {
            insideVo.setPage(-1);
            List<DzicsInsideLog> insideLog = dzicsInsideLogService.getBackInsideLog(insideVo);
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), DzicsInsideLog.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(insideLog);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }

}

