package com.dzics.data.acquisition.controller;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.model.constant.ShareVo;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.redis.util.RedisUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Classname ApiController
 * @Description 描述
 * @Date 2022/7/28 10:51
 * @Created by NeverEnd
 */
@Api(tags = {"消费端接收数据"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/methods/share")
@Slf4j
public class ApiController {

    @Autowired
    private RedisUtil<ShareVo> redisUtil;

    @ApiOperation(value = "接收是否共享机床脉冲数据到队列")
    @ApiOperationSupport(author = "NeverEnd")
    @PostMapping("/pulse")
    public Result sharePulse(@Valid @RequestBody ShareVo shareVo) {
        log.info("接收到共享设置数据存储到redis中：{}", JSONObject.toJSONString(shareVo));
        redisUtil.set(RedisKey.SHARED_DATA + shareVo.getOrderNo() + shareVo.getLineNo(), shareVo);
        return Result.ok();
    }

}
