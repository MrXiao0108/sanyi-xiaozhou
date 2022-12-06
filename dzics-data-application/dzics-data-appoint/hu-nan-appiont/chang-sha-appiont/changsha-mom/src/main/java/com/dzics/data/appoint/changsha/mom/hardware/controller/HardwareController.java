package com.dzics.data.appoint.changsha.mom.hardware.controller;

import com.alibaba.fastjson.JSON;
import com.dzics.data.appoint.changsha.mom.hardware.service.HardwareService;
import com.dzics.data.common.base.vo.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author: van
 * @since: 2022-07-12
 */
@Slf4j
@RestController
@RequestMapping("/api/hardware")
@Api(tags = {"UDP数据处理"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class HardwareController {

    @Autowired
    private HardwareService hardwareService;

    @PostMapping("/udp")
    public Result<String> udp(@RequestBody Map<String, String> map) {
        log.info("HardwareController [udp] 消息: {}", JSON.toJSONString(map));
        hardwareService.udpHandle(map.get("msg"));
        return Result.ok();
    }
}
