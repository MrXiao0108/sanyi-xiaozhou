package com.dzics.data.business.controller.sys.ref;

import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.rabbitmq.service.RabbitmqService;
import com.dzics.data.redis.util.RedisUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhangChengJun
 * Date 2021/5/24.
 * @since
 */
@Api(tags = {"触发页面刷新"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/refresh")
@Controller
public class HtmlRefreshController {


    @Value("${push.kanban.ref.routing.simple}")
    private String qushRouting;
    @Value("${push.kanban.ref.exchange.simple}")
    private String pushExchange;

    @Autowired
    private RabbitmqService rabbitmqService;

    @Autowired
    private RedisUtil redisUtil;

    @OperLog(operModul = "触发页面刷新", operType = OperType.UPDATE, operDesc = "刷新页面", operatorType = "后台")
    @ApiOperation(value = "刷新页面")
    @ApiOperationSupport(author = "NeverEnd", order = 8)
    @PostMapping
    public Result refresh(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Object ref = redisUtil.get(RedisKey.REF);
        if (ref != null) {
            long expire = redisUtil.getExpire(RedisKey.REF);
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR, expire + "秒后重试");
        }
        redisUtil.set(RedisKey.REF, "REF", 180);
        rabbitmqService.sendJsonString(pushExchange, qushRouting, "REF");
        return Result.ok();
    }

}
