package com.dzics.data.appoint.changsha.mom.controller.wms;

import com.dzics.data.appoint.changsha.mom.model.dto.wms.CallFrame;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.DzLocation;
import com.dzics.data.appoint.changsha.mom.model.vo.wms.CallFrameResp;
import com.dzics.data.appoint.changsha.mom.model.vo.wms.LocationResp;
import com.dzics.data.appoint.changsha.mom.model.vo.wms.WmsRespone;
import com.dzics.data.appoint.changsha.mom.service.wms.DzWmsService;
import com.dzics.data.common.base.vo.Result;
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

/**
 * @author ZhangChengJun
 * Date 2021/12/6.
 * @since
 */
@Api(tags = "WMS-呼叫")
@RequestMapping("/api/dz/wms")
@RestController
@Slf4j
public class DzWmsController {

    @Autowired
    private DzWmsService wmsService;

    /**
     * 呼叫料框
     * @param
     * @return
     */
    @ApiOperation(value = "呼叫料框", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @PostMapping("/call/material")
    public Result<CallFrameResp> chlickOkMaterial(@RequestBody CallFrame dzCallFrame) {
//        log.info("DzWmsController [chlickOkMaterial] 机器人呼叫料框");
        Result result = wmsService.callFrame(dzCallFrame);
        return result;
    }

    /**
     * 机械手放货位置申请
     * @param
     * @return
     */
    @ApiOperation(value = "机械手放货位置申请", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 2)
    @PostMapping("/location")
    public Result<LocationResp> locationRequest(@RequestBody DzLocation dzLocation) {
        return wmsService.locationRequest(dzLocation);
    }


    /**
     * 放料完成
     * @param
     * @return
     */
    @ApiOperation(value = "放料完成", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 4)
    @PostMapping("/put/completed")
    public Result<WmsRespone> putCompleted(@RequestBody DzLocation dzLocation) {
        return wmsService.putCompleted(dzLocation);
    }

}
