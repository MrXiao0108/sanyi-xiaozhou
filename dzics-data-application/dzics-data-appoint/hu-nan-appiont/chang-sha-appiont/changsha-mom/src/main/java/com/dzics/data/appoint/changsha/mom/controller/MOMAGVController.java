package com.dzics.data.appoint.changsha.mom.controller;

import com.alibaba.fastjson.JSON;
import com.dzics.data.appoint.changsha.mom.model.dto.mom.EmptyFrameMovesDzdc;
import com.dzics.data.appoint.changsha.mom.service.CachingApi;
import com.dzics.data.appoint.changsha.mom.service.MOMAGVService;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@Api(tags = {"机器人-操作AGV"})
@RequestMapping("/api/agv")
@RestController
@Slf4j
public class MOMAGVController {

    @Autowired
    private MOMAGVService momagvService;
    @Value("${order.code}")
    private String orderCode;
    @Autowired
    private CachingApi cachingApi;

    @ApiOperation(value = "机器人请求叫料", notes = "返回 data中值 OKOK 请求叫料成功", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 8)
    @PostMapping("/call/material")
    public Result chlickOkMaterial(@RequestBody EmptyFrameMovesDzdc emptyFrameMovesDzdc) {
//        log.info("MOMAGVController [chlickOkMaterial] 机器人请求叫料：{}", JSON.toJSONString(emptyFrameMovesDzdc));
        return Result.ok(momagvService.processDistribution(emptyFrameMovesDzdc));
    }

    @ApiOperation(value = "人工物流操作", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "FengWanshi", order = 8)
    @PostMapping("/call/material/m")
    public Result callMaterialM(@RequestBody EmptyFrameMovesDzdc emptyFrameMovesDzdc) {
        DzProductionLine orderIdAndLineId = cachingApi.getOrderIdAndLineId();
        emptyFrameMovesDzdc.setOrderCode(orderCode);
        emptyFrameMovesDzdc.setLineNo(orderIdAndLineId.getLineNo());
        emptyFrameMovesDzdc.setQuantity(0);
        log.info("MOMAGVController [chlickOkMaterial] 人工物流操作：{}", JSON.toJSONString(emptyFrameMovesDzdc));
        return Result.ok(momagvService.processDistribution(emptyFrameMovesDzdc));
    }

    @ApiOperation(value = "AGV握手机器人应答模拟接口")
    @GetMapping("/demo/handshake")
    public Result handshake() {
        momagvService.handshakeResult();
        return Result.ok();
    }
}
