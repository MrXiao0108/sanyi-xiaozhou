package com.dzics.data.business.controller.productionplan;


import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.model.dto.GetOneDayPlanDayDto;
import com.dzics.data.pdm.service.DzProductionPlanDayService;
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

import java.util.List;

/**
 * 产线日生产计划记录
 * @author jq
 * Date 2021/2/19
 */
@Api(tags = {"产线日生产计划记录"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/production/record")
@Controller
public class ProductionPlanRecordController {
    @Autowired
    private DzProductionPlanDayService planDayService;

    @ApiOperation(value = "查询日生产计划记录列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping
    public Result<List<GetOneDayPlanDayDto>> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub, PageLimitBase pageLimit) {
        return planDayService.getList(pageLimit);
    }
}

