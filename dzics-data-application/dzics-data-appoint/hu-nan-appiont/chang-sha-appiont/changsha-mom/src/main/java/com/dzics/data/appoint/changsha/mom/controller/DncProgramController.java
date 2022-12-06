package com.dzics.data.appoint.changsha.mom.controller;


import com.dzics.data.appoint.changsha.mom.model.vo.DNCProgramVo;
import com.dzics.data.appoint.changsha.mom.service.DncProgramService;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.vo.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * dnc 换型信息 前端控制器
 * </p>
 *
 * @author van
 * @since 2022-06-28
 */
@Api(tags = "DNC处理")
@RestController
@RequestMapping("/dncProgram")
public class DncProgramController {

    @Autowired
    private DncProgramService dncProgramService;

    @ApiOperation(value = "查询订单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "FengWanShi", order = 1)
    @GetMapping
    public Result<List<DNCProgramVo>> page(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                           DNCProgramVo vo) {
        return dncProgramService.page(vo);
    }

    @ApiOperation(value = "重发")
    @ApiOperationSupport(author = "FengWanShi")
    @PutMapping("/repeat")
    public Result<String> repeat(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                 @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                 @RequestBody DNCProgramVo dncProgramVo) {
        if (!StringUtils.hasLength(dncProgramVo.getId())) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_5);
        }
        return dncProgramService.repeat(dncProgramVo.getId());
    }

    @ApiOperation(value = "输入主程序号")
    @ApiOperationSupport(author = "FengWanShi")
    @PutMapping("/manual")
    public Result<String> manual(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                 @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                 @RequestBody DNCProgramVo dncProgramVo) {
        if (!StringUtils.hasLength(dncProgramVo.getId())) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_5);
        }
        return dncProgramService.manualIntervention(dncProgramVo);
    }

    @ApiOperation(value = "取消")
    @ApiOperationSupport(author = "FengWanShi")
    @PutMapping("/cancel")
    public Result<String> cancel(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                 @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                 @RequestBody DNCProgramVo dncProgramVo) {
        if (!StringUtils.hasLength(dncProgramVo.getId())) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_5);
        }
        return dncProgramService.cancel(dncProgramVo.getId());
    }
}

