package com.dzics.data.business.controller.productionplan;


import com.dzics.data.business.service.ProductionPlanService;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.db.model.dao.SelectProductionDetailsDo;
import com.dzics.data.pdm.db.model.dto.SelectProductionDetailsVo;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 产品生产明细
 *
 * @author jq
 * Date 2021/2/20
 */
@Api(tags = {"产品生产明细"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/production/details")
@Controller
public class ProductionDetailsController {
    @Autowired
    ProductionPlanService productionPlanService;

    @ApiOperation(value = "产品生产明细列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 111)
    @GetMapping
    public Result<SelectProductionDetailsDo> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                  PageLimit pageLimit, SelectProductionDetailsVo selectProductionDetailsVo
    ) {
        Result result = productionPlanService.list(sub, pageLimit, selectProductionDetailsVo);
        return result;
    }
}
