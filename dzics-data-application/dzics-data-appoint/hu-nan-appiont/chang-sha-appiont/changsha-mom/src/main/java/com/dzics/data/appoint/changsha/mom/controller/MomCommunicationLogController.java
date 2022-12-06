package com.dzics.data.appoint.changsha.mom.controller;


import com.dzics.data.appoint.changsha.mom.model.entity.MomCommunicationLog;
import com.dzics.data.appoint.changsha.mom.model.vo.MomCommunicationLogVo;
import com.dzics.data.appoint.changsha.mom.service.MomCommunicationLogService;
import com.dzics.data.common.base.vo.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * MOM通讯日志 前端控制器
 * </p>
 *
 * @author van
 * @since 2022-08-19
 */
@Api(tags = {"MOM通讯日志"})
@RestController
@RequestMapping("/momCommunicationLog")
public class MomCommunicationLogController {

    @Autowired
    private MomCommunicationLogService momCommunicationLogService;

    @ApiOperation(value = "分页查询", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "FengWanShi", order = 1)
    @GetMapping
    public Result<List<MomCommunicationLog>> page(MomCommunicationLogVo vo) {
        return momCommunicationLogService.page(vo);
    }
}

