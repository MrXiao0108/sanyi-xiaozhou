package com.dzics.data.business.controller.zkms;


import com.dzics.data.common.base.vo.Result;
import com.dzics.data.zkjob.model.dao.GetWorkDo;
import com.dzics.data.zkjob.model.dao.GetWorkStatusDo;
import com.dzics.data.zkjob.model.dto.GetWorkStatusVo;
import com.dzics.data.zkjob.model.dto.GetWorkVo;
import com.dzics.data.zkjob.service.JobExecutionLogService;
import com.dzics.data.zkjob.service.JobStatusTraceLogService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xnb
 * @date 2021年11月22日 8:36
 */
@Api(tags = {"作业历史"})
@RequestMapping("/workHistory")
@RestController
public class WorkHistoryController {

    @Autowired
    private JobExecutionLogService jobExecutionLogService;
    @Autowired
    private JobStatusTraceLogService jobStatusTraceLogService;


    @ApiOperation(value = "历史轨迹")
    @ApiOperationSupport(author = "xnb", order = 1)
    @GetMapping("/trajectory")
    public Result<List<GetWorkDo>> getWorkList(GetWorkVo getWorkVo,
                                               @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                               @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub){
        return jobExecutionLogService.getList(getWorkVo);
    }

    @ApiOperation(value = "历史状态")
    @ApiOperationSupport(author = "xnb", order = 2)
    @GetMapping("/status")
    public Result<List<GetWorkStatusDo>> getWorkStatus(GetWorkStatusVo getWorkVo,
                                                       @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub){
        return jobStatusTraceLogService.getList(getWorkVo);
    }

}
