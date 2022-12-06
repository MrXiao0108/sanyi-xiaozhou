package com.dzics.data.business.controller.odline;


import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.business.service.OrderService;
import com.dzics.data.pub.model.dto.AddOrderVo;
import com.dzics.data.pub.model.dto.OrderParmsModel;
import com.dzics.data.pub.model.entity.DzOrder;
import com.dzics.data.pub.model.vo.DzOrderDo;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"订单管理"}, produces = "订单管理相关接口")
@RequestMapping("/order")
@RestController
public class OrderController {
    @Autowired
    OrderService orderService;


    @ApiOperation(value = "分页查询订单")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping
    public Result<DzOrderDo> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                  OrderParmsModel orderParmsModel) {
        Result result =  orderService.list(sub, orderParmsModel);
        return result;
    }

    @OperLog(operModul = "订单管理相关", operType = OperType.ADD, operDesc = "新增订单", operatorType = "后台")
    @ApiOperation(value = "新增订单")
    @ApiOperationSupport(author = "jq", order = 2)
    @PostMapping
    public Result<DzOrder> add(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                               @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                               @RequestBody @Valid AddOrderVo data) {
        return orderService.add(sub, data);
    }

    @OperLog(operModul = "订单管理相关", operType = OperType.DEL, operDesc = "删除订单", operatorType = "后台")
    @ApiOperation(value = "删除订单")
    @ApiOperationSupport(author = "jq", order = 3)
    @DeleteMapping(value = "/{id}")
    public Result del(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @PathVariable("id") Long id) {
        return orderService.del(sub, id);
    }

    @OperLog(operModul = "订单管理相关", operType = OperType.DEL, operDesc = "编辑订单", operatorType = "后台")
    @ApiOperation(value = "编辑订单")
    @ApiOperationSupport(author = "jq", order = 3)
    @PutMapping
    public Result put(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @RequestBody @Valid AddOrderVo data) {
        return orderService.put(sub, data);
    }

}
