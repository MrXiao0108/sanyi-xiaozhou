package com.dzics.data.business.controller.productionplan;


import com.dzics.data.business.service.ProductionPlanService;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.db.model.dao.SelectEquipmentProductionDo;
import com.dzics.data.pdm.db.model.dto.SelectEquipmentProductionVo;
import com.dzics.data.pdm.model.dao.SelectEquipmentProductionDetailsVo;
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

import javax.validation.Valid;
import java.util.List;

/**
 * 设备生产数量明细
 * @author jq
 * Date 2021/2/20
 */
@Api(tags = {"设备生产数量明细"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/production/equipment")
@Controller
public class EquipmentProductionController {
    @Autowired
    ProductionPlanService productionPlanService;

    @ApiOperation(value = "设备生产数量明细列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 111)
    @GetMapping
    public Result<List<SelectEquipmentProductionDo>> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                          PageLimitBase pageLimit, @Valid SelectEquipmentProductionVo dto) {
        return productionPlanService.listProductionEquipment(sub,pageLimit,dto);
    }

    @ApiOperation(value = "设备生产数量详情列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 111)
    @GetMapping("/listDetails")
    public Result listDetails(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                              SelectEquipmentProductionDetailsVo detailsVo
    ) {
        Result result =productionPlanService.listProductionEquipmentDetails(sub,detailsVo);
        return result;
    }
}

