package com.dzics.data.business.controller.appoint;

import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * @Classname AppointContoller
 * @Description 描述
 * @Date 2022/4/25 13:21
 * @Created by NeverEnd
 */
@Api(tags = {"定制服务"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/appoint")
@Slf4j
public class AppointContoller {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${appoint.port}")
    private String appointPort;

    /**
     * /api+模块名+具体功能+请求类型（如 add del query）
     * @param jsonString
     * @param module
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @ApiOperation(value = "多功能接口", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PostMapping(value = "/{prefix}/{module}/{appoint}/{type}")
    public Object getAppointApi(
            @RequestBody @ApiParam(required = true,name = "jsonObject") JSONObject jsonString,
            @PathVariable(value = "prefix", required = true) @ApiParam(value = "prefix") String prefix,
            @PathVariable(value = "module", required = true) @ApiParam(value = "模块名") String module,
            @PathVariable(value = "appoint", required = true) @ApiParam(value = "具体功能") String appoint,
            @PathVariable(value = "type", required = true) @ApiParam(value = "请求类型") String type,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        try {
            String format = String.format("http://%s:%s/%s/%s/%s/%s", "127.0.0.1", appointPort,prefix, module,appoint,type);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("application/json;charset=UTF-8"));
            HttpEntity<JSONObject> entity = new HttpEntity<>(jsonString, headers);
            ResponseEntity<Object> responseEntity = restTemplate.postForEntity(format, entity, Object.class);
            return responseEntity.getBody();
        } catch (HttpClientErrorException clientErrorException) {
            log.error("请求定制服务错误：{}", clientErrorException.getMessage());
            return clientErrorException.getResponseBodyAsString();
        } catch (Throwable throwable) {
            String message = throwable.getMessage();
            log.error("请求定制服务错误：{}", message);
            return message;
        }

    }

}
