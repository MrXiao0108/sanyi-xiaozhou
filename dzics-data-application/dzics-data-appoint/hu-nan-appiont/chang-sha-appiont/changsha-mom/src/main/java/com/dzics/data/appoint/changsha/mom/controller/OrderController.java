package com.dzics.data.appoint.changsha.mom.controller;

import cn.hutool.json.JSONUtil;
import com.dzics.data.appoint.changsha.mom.model.dao.MomOrderDo;
import com.dzics.data.appoint.changsha.mom.model.dto.PutMomOrder;
import com.dzics.data.appoint.changsha.mom.model.dto.SearchOrderParms;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.appoint.changsha.mom.service.MomOrderService;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.vo.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * MOM订单页面
 *
 * @Classname OrderController
 * @Description MOM 订单接口  命名规则  /api+模块名+具体功能+请求类型（如 add del query）
 * @Date 2022/4/25 14:03
 * @Created by NeverEnd
 */
@Api(tags = {"MOM订单"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Slf4j
@RequestMapping("/api/mom/order")
public class OrderController {
    @Autowired
    private MomOrderService momOrderService;

    @ApiOperation(value = "分页查询订单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PostMapping("/query")
    public Result<List<MomOrderDo>> list(@RequestBody SearchOrderParms parms) {
        if (!StringUtils.hasText(parms.getField())) {
            parms.setField("scheduledStartDate");
        }
        if (!StringUtils.hasText(parms.getType())) {
            parms.setType("DESC");
        }
        return momOrderService.listQuery(parms);
    }

    @OperLog(operModul = "MOM订单管理", operType = OperType.UPDATE, operDesc = "开始订单", operatorType = "后台")
    @ApiOperation(value = "开始订单按钮", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "Ubiead", order = 111)
    @PostMapping("/start")
    public Result orderStart(@RequestBody @Valid PutMomOrder putMomOrder) {
        log.info("OrderController [start] {}", JSONUtil.toJsonStr(putMomOrder));
        return momOrderService.orderStart(putMomOrder);
    }

    @OperLog(operModul = "MOM订单管理", operType = OperType.UPDATE, operDesc = "暂停订单", operatorType = "后台")
    @ApiOperation(value = "暂停订单按钮", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "Ubiead", order = 111)
    @PostMapping("/stop")
    public Result orderStop(@RequestBody @Valid PutMomOrder putMomOrder) {
        return momOrderService.orderStop(putMomOrder);
    }

    @OperLog(operModul = "MOM订单管理", operType = OperType.UPDATE, operDesc = "强制关闭", operatorType = "后台")
    @ApiOperation(value = "强制关闭订单按钮", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "Ubiead", order = 111)
    @PostMapping("/forceclose")
    public Result forceClose(@RequestBody @Valid PutMomOrder putMomOrder) {
        return momOrderService.forceClose(putMomOrder);
    }

    @OperLog(operModul = "MOM订单管理", operType = OperType.UPDATE, operDesc = "恢复订单", operatorType = "后台")
    @ApiOperation(value = "恢复订单按钮", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "Ubiead", order = 111)
    @PostMapping("/recover")
    public Result orderRecover(@RequestBody @Valid PutMomOrder putMomOrder) {
        return momOrderService.orderRecover(putMomOrder);
    }

    @OperLog(operModul = "MOM订单管理", operType = OperType.UPDATE, operDesc = "订单作废", operatorType = "后台")
    @ApiOperation(value = "订单作废按钮", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "Ubiead", order = 111)
    @DeleteMapping("/delete/{proTaskOrderId}")
    public Result orderDelete(@PathVariable(value = "proTaskOrderId") String proTaskOrderId) {
        return momOrderService.orderDelete(proTaskOrderId);
    }

    @OperLog(operModul = "MOM订单管理", operType = OperType.UPDATE, operDesc = "查看物料详情", operatorType = "后台")
    @ApiOperation(value = "查看物料详情按钮", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "Ubiead", order = 111)
    @GetMapping("/material/{workingProcedureId}")
    public Result orderMaterialDetail(@PathVariable(value = "workingProcedureId") String workingProcedureId) {
        return momOrderService.orderMaterialDetail(workingProcedureId);
    }

    @ApiOperation("切换订单")
    @ApiOperationSupport(author = "FengWanshi", order = 111)
    @PostMapping("/changeOrder/{proTaskOrderId}")
    public Result<String> changeOrder(@PathVariable("proTaskOrderId") String proTaskOrderId) {
        momOrderService.changeOrder(proTaskOrderId);
        return Result.ok();
    }



    @ApiOperation(value = "订单状态变更", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "FengWanshi", order = 111)
    @PostMapping("/updState")
    public Result updState(@RequestBody @Valid PutMomOrder putMomOrder) {
        MomOrder byId = momOrderService.getById(putMomOrder.getProTaskOrderId());
        byId.setProgressStatus(putMomOrder.getProgressStatus());
        momOrderService.updateById(byId);
        return Result.ok();
    }

    @ApiOperation(value = "订单转发", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "Xnb", order = 111)
    @PutMapping("/forwardOrder/{proTaskOrderId}")
    public Result forwardOrder(@PathVariable("proTaskOrderId") String proTaskOrderId) {
        return momOrderService.forwardOrder(proTaskOrderId);
    }
}
