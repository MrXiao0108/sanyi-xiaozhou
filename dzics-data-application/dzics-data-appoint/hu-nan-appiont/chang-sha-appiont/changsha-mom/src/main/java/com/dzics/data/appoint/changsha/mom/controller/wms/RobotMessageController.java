package com.dzics.data.appoint.changsha.mom.controller.wms;

import com.dzics.data.appoint.changsha.mom.model.constant.ChangShaRedisKey;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.redis.util.RedisUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @Classname RobotMessageController
 * @Description 与机器人交互的接口
 * @Date 2022/7/15 16:40
 * @Created by NeverEnd
 */
@Api(tags = "机器人交互")
@RequestMapping("/api/ui/wms/robot")
@RestController
public class RobotMessageController {

    @Autowired
    private RedisUtil<String> redisUtil;

    @ApiOperation(value = "是否输入二维码", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 20)
    @GetMapping("/req/code")
    public Result getCode(@ApiParam("订单号") @RequestParam("orderNo") String orderNo) {
        String reqQrCode = redisUtil.get(ChangShaRedisKey.TCP_REQUEST_QR_CODE + orderNo);
        if (StringUtils.isEmpty(reqQrCode)) {
            return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
        } else {
            return Result.ok();
        }
    }


    @ApiOperation(value = "接收二维码", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 101, params = @DynamicParameters(name = "map", properties = {
            @DynamicParameter(name = "code",value = "二维码",example = "X000111",required = true,dataTypeClass = String.class),
            @DynamicParameter(name = "orderNo",value = "订单号",example = "DZ-1976",required = true,dataTypeClass = String.class)
    }))
    @PostMapping("/req/code")
    public Result putCode(@RequestBody HashMap<String,String> map) {
        String code = map.get("code");
        String orderNo = map.get("orderNo");
        redisUtil.del(ChangShaRedisKey.TCP_REQUEST_QR_CODE + orderNo);
        redisUtil.set(ChangShaRedisKey.TCP_READ_QR_CODE + orderNo, code);
        return Result.ok();
    }
}
