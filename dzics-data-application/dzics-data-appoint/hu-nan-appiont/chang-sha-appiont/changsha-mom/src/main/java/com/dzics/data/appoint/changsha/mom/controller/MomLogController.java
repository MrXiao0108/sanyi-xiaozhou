package com.dzics.data.appoint.changsha.mom.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.data.appoint.changsha.mom.model.entity.DncProgram;
import com.dzics.data.appoint.changsha.mom.model.vo.DncLogVo;
import com.dzics.data.appoint.changsha.mom.service.DncProgramService;
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
 * @author xnb
 * @date 2022/11/11 0011 15:35
 */
@Api(tags = {"Mom通讯日志"})
@Slf4j
@RestController
@RequestMapping("/api/mom/log")
public class MomLogController {

    @Autowired
    private DncProgramService dncProgramService;

    @ApiOperation(value = "查询DNC通讯日志", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/DNC/Query")
    public Result list(DncLogVo dncLogVo) throws ParseException {
        List<DncProgram> dncLog = dncProgramService.getDncLog(dncLogVo);
        return Result.ok(dncLog);
    }


    @ApiOperation(value = "导出DNC通讯日志")
    @GetMapping("/DNC/log/download")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     DncLogVo dncLogVo, HttpServletResponse response) throws IOException {
        String fileNameBase = "DNC通讯日志";
        try {
            dncLogVo.setPage(-1);
            List<DncProgram> dncLog = dncProgramService.getDncLog(dncLogVo);
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), DncProgram.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(dncLog);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


}
