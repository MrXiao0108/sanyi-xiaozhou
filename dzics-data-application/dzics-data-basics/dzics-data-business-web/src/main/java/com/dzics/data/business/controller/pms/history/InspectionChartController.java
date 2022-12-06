package com.dzics.data.business.controller.pms.history;

import com.dzics.data.business.model.vo.SelectTrendChartDo;
import com.dzics.data.business.service.ProductTrendChartService;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.model.entity.DzProduct;
import com.dzics.data.pms.model.entity.DzProductDetectionTemplate;
import com.dzics.data.pms.model.vo.SelectTrendChartVo;
import com.dzics.data.pms.service.DzProductDetectionTemplateService;
import com.dzics.data.pms.service.DzProductService;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 检测值走势图
 *
 * @author ZhangChengJun
 * Date 2021/2/21.
 * @since
 */
@Api(tags = {"检测值走势图"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/product/check")
@Controller
public class InspectionChartController {

    @Autowired
    private DzProductService dzProductService;
    @Autowired
    private DzProductDetectionTemplateService templateService;

    @Autowired
    private ProductTrendChartService chartService;
    @Autowired
    private DzicsUserService userService;

    @ApiOperation(value = "检测走势图列表")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping("/trend")
    public Result<SelectTrendChartDo> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                                           @Valid SelectTrendChartVo selectTrendChartVo) {
        Result result = chartService.list(sub, selectTrendChartVo);
        return result;
    }

    @ApiOperation(value = "根据产品id查询检测项")
    @ApiOperationSupport(author = "jq", order = 2)
    @GetMapping("/trend/getByProductId/{productNo}")
    public Result<DzProductDetectionTemplate> getByProductId(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                                             @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                                                             @PathVariable("productNo") @ApiParam("产品id(序号)") String productNo) {
        Result result = templateService.getByProductId(productNo);
        return result;
    }

    @ApiOperation(value = "所有产品")
    @ApiOperationSupport(author = "jq", order = 2)
    @GetMapping("/trend/getDepartId")
    public Result<DzProduct> getByProductId(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub) {
        SysUser byUserName = userService.getByUserName(sub);
        String useDepartId = byUserName.getUseDepartId();
        Result result = dzProductService.getByProductId(useDepartId);
        return result;
    }

}
