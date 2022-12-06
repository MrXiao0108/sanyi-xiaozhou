package com.dzics.data.business.controller.productionplan;


import com.dzics.data.business.service.ProductionPlanService;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.db.model.vo.ProductionPlanDo;
import com.dzics.data.pdm.model.dto.GetOneDayPlanDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 产线日生产计划设置
 * @author jq
 * Date 2021/2/19
 */
@Api(tags = {"生产计划设置"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/production/plan")
@Controller
public class ProductionPlanController {

    @Autowired
    private ProductionPlanService productionPlanService;

    @ApiOperation(value = "查询产线日生产计划列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping
    public Result<GetOneDayPlanDto> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub, PageLimit pageLimit) {
        return productionPlanService.list(sub,pageLimit);
    }

    @OperLog(operModul = "生产计划设置相关", operType = OperType.UPDATE, operDesc = "修改产线日生产计划", operatorType = "后台")
    @ApiOperation(value = "修改产线日生产计划", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping
    public Result<ProductionPlanDo> put(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                        @RequestBody @Valid ProductionPlanDo productionPlanDo) {
        return productionPlanService.put(sub,productionPlanDo);
    }
}
