package com.dzics.data.business.controller.workStation;

import com.dzics.data.business.model.dto.locationartifacts.AddLocationArtifactsVo;
import com.dzics.data.business.model.dto.locationartifacts.PutLocationArtifactsVo;
import com.dzics.data.business.model.dto.product.OrderLinePrms;
import com.dzics.data.business.model.dto.product.OrderLinePrmsWork;
import com.dzics.data.business.model.vo.locationartifacts.GetLocationArtifactsByIdDo;
import com.dzics.data.business.service.StationProductService;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.service.DzProductDetectionTemplateService;
import com.dzics.data.pub.model.vo.locationartifacts.LocationArtifactsVo;
import com.dzics.data.pub.service.DzWorkingStationProductService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"产品检测项绑定"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/location/artifacts")
@Controller
public class LocationArtifactsController {
    @Autowired
    private DzWorkingStationProductService dzStpro;
    @Autowired
    private DzProductDetectionTemplateService detectionTemplateService;

    @Autowired
    private StationProductService stationProductService;

    @ApiOperation(value = "工位工件列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 200)
    @GetMapping
    public Result list(
            LocationArtifactsVo locationArtifactsVo,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return dzStpro.locationArtifactsList(locationArtifactsVo, sub);
    }

    @ApiOperation(value = "获取产品检测项", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 112)
    @GetMapping("/productNo")
    public Result selProductTemplate(
            OrderLinePrms orderId,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return detectionTemplateService.selProductTemplate(orderId.getOrderId(), orderId.getLineId(), orderId.getProductNo(), sub);
    }


    @OperLog(operModul = "产品检测项绑定", operType = OperType.UPDATE, operDesc = "添加", operatorType = "后台")
    @ApiOperation(value = "工位工件添加", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 201)
    @PostMapping
    public Result add(
            @Valid @RequestBody AddLocationArtifactsVo addLocationArtifactsVo,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return stationProductService.add(addLocationArtifactsVo, sub);
    }


    @ApiOperation(value = "获取单个工位工件", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 112)
    @GetMapping("/get")
    public Result<GetLocationArtifactsByIdDo> getById(
            OrderLinePrmsWork orderId,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return stationProductService.selEditProcedureProduct(orderId.getOrderId(), orderId.getLineId(), orderId.getWorkStationProductId(), sub);
    }


    @OperLog(operModul = "产品检测项绑定", operType = OperType.UPDATE, operDesc = "编辑", operatorType = "后台")
    @ApiOperation(value = "编辑工位工件", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 112)
    @PutMapping
    public Result updateProcedureProduct(
            @Valid @RequestBody PutLocationArtifactsVo putLocationArtifactsVo,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return stationProductService.updateProcedureProduct(putLocationArtifactsVo, sub);
    }


    @OperLog(operModul = "工位工件绑定", operType = OperType.DEL, operDesc = "删除工位工件", operatorType = "后台")
    @ApiOperation(value = "删除工位工件", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 112)
    @DeleteMapping("/{workStationProductId}")
    public Result delWorkStationProductId(
            @PathVariable(value = "workStationProductId") @ApiParam(value = "工位-工件关联关系ID", required = true) String workStationProductId,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return stationProductService.delWorkStationProductId(workStationProductId, sub);
    }
}
