package com.dzics.data.appoint.changsha.mom.controller;

import com.dzics.data.appoint.changsha.mom.model.respon.IndexLogDo;
import com.dzics.data.appoint.changsha.mom.service.LogService;
import com.dzics.data.common.base.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xnb
 * @date 2022/11/14 0014 10:22
 */
@Slf4j
@Api(tags = {"首页接口"})
@RestController
@RequestMapping("/api/mom/index")
public class IndexController {
    @Autowired
    private LogService logService;



    @ApiOperation(value = "Mom定制日志")
    @GetMapping("/appoint/log")
    public Result<IndexLogDo> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub){
        return Result.ok(logService.getMomIndexBackLog());
    }

}
