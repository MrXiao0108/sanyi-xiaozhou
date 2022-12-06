package com.dzics.data.business.controller.common;

import com.dzics.data.business.service.LineService;
import com.dzics.data.business.service.MainTainService;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.maintain.model.vo.DzCheckUpItemDo;
import com.dzics.data.pms.model.dto.Products;
import com.dzics.data.pms.model.entity.DzMaterial;
import com.dzics.data.pms.service.DzMaterialService;
import com.dzics.data.pms.service.DzProductService;
import com.dzics.data.pub.db.model.dto.Lines;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.entity.SysDictItem;
import com.dzics.data.pub.model.vo.DeviceMessage;
import com.dzics.data.pub.model.vo.Orders;
import com.dzics.data.pub.model.vo.SelOrders;
import com.dzics.data.pub.service.DzEquipmentService;
import com.dzics.data.pub.service.DzOrderService;
import com.dzics.data.pub.service.DzProductionLineService;
import com.dzics.data.pub.service.SysDictItemService;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"公共接口"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/commons")
public class CurrencyController {
    @Autowired
    private SysDictItemService dictItemService;

    @Autowired
    private DzOrderService businessOrderService;

    @Autowired
    private DzProductionLineService productionLineService;

    @Autowired
    private DzEquipmentService equipmentService;

    @Autowired
    DzMaterialService dzMaterialService;

    @Autowired
    private LineService lineService;

    @Autowired
    private MainTainService mainTainService;

    @Autowired
    private DzProductService productService;
    @Autowired
    private DzicsUserService userService;

    @ApiOperation(value = "所有订单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @GetMapping("/orders")
    public Result<Orders> selOrders(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        SysUser byUserName = userService.getByUserName(sub);
        String useDepartId = byUserName.getUseDepartId();
        return businessOrderService.setlOrders(useDepartId, sub);
    }

    @ApiOperation(value = "所有产线", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 113)
    @GetMapping("/lines")
    public Result<Lines> selLines(SelOrders selOrders, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        SysUser byUserName = userService.getByUserName(sub);
        String useOrgCode = byUserName.getUseOrgCode();
        return productionLineService.selLines(selOrders, useOrgCode);
    }

    @ApiOperation(value = "当前用户下的订单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @GetMapping("/orders/depart")
    public Result<Orders> selOrdersDepart(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        SysUser byUserName = userService.getByUserName(sub);
        return businessOrderService.selOrdersDepart(byUserName.getUseDepartId(), sub);
    }

    @ApiOperation(value = "获取对应订单下所有产线V2")
    @ApiOperationSupport(author = "jq", order = 117)
    @GetMapping("/getByOrderId/v2/{id}")
    public Result<Lines> getByOrderIdV2(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                        @PathVariable("id") String id
    ) {
        return productionLineService.getByOrderIdV2(sub, Long.valueOf(id));
    }

    @ApiOperation(value = "所有工件", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 114)
    @GetMapping("/products")
    public Result<Products> selProduct(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                       @RequestParam(value = "lineType", required = false) String lineType) {
        String useOrgCode = userService.getByUserName(sub).getUseOrgCode();
        return productService.selProduct(sub, lineType,useOrgCode);
    }


    @ApiOperation(value = "根据订单id查询产线列表")
    @ApiOperationSupport(author = "jq", order = 116)
    @GetMapping("/getByOrderId/{id}")
    public Result<DzProductionLine> getByOrderId(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                 @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                 @PathVariable("id") String id
    ) {
        return lineService.getByOrderId(sub, Long.valueOf(id));
    }

    @ApiOperation(value = "查询字典item值列表")
    @ApiOperationSupport(author = "jq", order = 4)
    @GetMapping("/dict/item")
    public Result<SysDictItem> listDictItem(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                            @RequestParam("dictId") String dictId, PageLimit pageLimit) {
        return dictItemService.listDictItem(pageLimit, dictId);
    }


    @ApiOperation(value = "查询产品绑定的组件物料列表")
    @GetMapping("/product/material/{productId}")
    public Result<DzMaterial> getMaterialByProductId(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                     @PathVariable("productId") String productId) {
        return dzMaterialService.getMaterialByProductId(productId);
    }

    @ApiOperation(value = "根据dictCode查询字典数据")
    @ApiOperationSupport(author = "jq", order = 4)
    @GetMapping("/getDictItem/{dictCode}")
    public Result<SysDictItem> getDictItem(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                           @PathVariable("dictCode") String dictCode) {
        return dictItemService.getDictItem(dictCode);
    }


    @ApiOperation(value = "产线下所有设备", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 114)
    @GetMapping("/line/devcie")
    public Result<DeviceMessage> selLineDevice(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                               @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                               @RequestParam(value = "lineId") String lineId) {
        return equipmentService.getDevcieLineId(sub, lineId);
    }

    @ApiOperation(value = "查询巡检项列表")
    @GetMapping("/check/up/getList")
    public Result<DzCheckUpItemDo> getCheckList(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                PageLimit pageLimit, Integer equipmentType) {
        Result result = mainTainService.list(pageLimit, equipmentType, null);
        return result;
    }
}
