package com.dzics.data.appoint.changsha.mom.controller;

import com.dzics.data.appoint.changsha.mom.model.entity.MomMaterialPoint;
import com.dzics.data.appoint.changsha.mom.service.CleanRedisCache;
import com.dzics.data.common.base.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LiuDongFei
 * @date 2022年07月11日 14:24
 */
@Api(tags = {"接收数据"},produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping("/api/receive/data")
@RestController
@Slf4j
public class ReceiveDataController {
    @Autowired
    private CleanRedisCache cleanRedisCache;

    @ApiOperation(value = "清空redis缓存",notes = "清空redis缓存")
    @PostMapping("/get/position")
    public Result getPosition(@RequestBody MomMaterialPoint momMaterialPoint){
        return cleanRedisCache.cleanPosition(momMaterialPoint.getOrderNo(),momMaterialPoint.getLineNo(),momMaterialPoint.getInIslandCode());
    }
}
