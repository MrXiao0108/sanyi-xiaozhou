package com.dzics.data.appoint.changsha.mom.controller;


import com.dzics.data.appoint.changsha.mom.model.vo.ResultDto;
import com.dzics.data.appoint.changsha.mom.service.ForWardDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author xnb
 * @date 2022/10/8 0008 9:30
 */
@Api(tags = {"同类型岛权限开放接口"},produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RestController
public class PublicWorkStationController {

    @Autowired
    private ForWardDataService forWardDataService;

    @ApiOperation(value = "接收、处理，异岛转发数据", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/forward/data")
    public ResultDto handleData(@RequestBody String msg){
        return forWardDataService.forWardData(msg);
    }
}
