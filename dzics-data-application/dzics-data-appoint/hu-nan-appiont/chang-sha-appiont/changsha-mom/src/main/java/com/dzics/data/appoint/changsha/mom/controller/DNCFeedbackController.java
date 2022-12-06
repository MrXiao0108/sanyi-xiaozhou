package com.dzics.data.appoint.changsha.mom.controller;

import com.alibaba.fastjson.JSON;
import com.dzics.data.appoint.changsha.mom.model.dto.dnc.DNCDto;
import com.dzics.data.appoint.changsha.mom.service.DncFeedbackService;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: van
 * @since: 2022-06-28
 */
@Slf4j
@Api(tags = {"DNC反馈传输结果处理"})
@RestController
@RequestMapping("/feedback")
public class DNCFeedbackController {

    @Autowired
    private DncFeedbackService dncFeedbackService;

    @ApiOperation("DNC反馈传输结果处理")
    @PostMapping
    public Result<String> feedback(@RequestBody DNCDto.Feedback feedback) {
        log.info("DNCFeedbackController [feedback] DNC反馈传输结果: {}", JSON.toJSONString(feedback));
        if (!"2a14187c-d58c-4dba-9caa-7a3421257e70".equals(feedback.getTokenstr())) {
            return new Result<>(CustomExceptionType.AUTHEN_TICATIIN_FAILURE);
        }
        dncFeedbackService.feedbackHandel(feedback);
        return Result.ok();
    }
}
