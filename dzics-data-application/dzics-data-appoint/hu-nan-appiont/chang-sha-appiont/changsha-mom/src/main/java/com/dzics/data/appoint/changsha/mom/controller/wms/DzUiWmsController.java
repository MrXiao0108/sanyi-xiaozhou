package com.dzics.data.appoint.changsha.mom.controller.wms;

import com.dzics.data.appoint.changsha.mom.model.dto.wms.CallFrame;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.DzOrderCompleted;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.GetOrderCfig;
import com.dzics.data.appoint.changsha.mom.model.entity.WmsCallframeHistory;
import com.dzics.data.appoint.changsha.mom.model.vo.wms.WmsRespone;
import com.dzics.data.appoint.changsha.mom.service.wms.DzWmsOrderCfgService;
import com.dzics.data.appoint.changsha.mom.service.wms.DzWmsService;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 维护新增物料信息接口
 * @author ZhangChengJun
 * Date 2021/12/6.
 * @since
 */
@Api(tags = "WMS-页面接口")
@RequestMapping("/api/ui/wms")
@RestController
public class DzUiWmsController {

    @Autowired
    private DzWmsService wmsService;

    @Autowired
    private DzWmsOrderCfgService dzWmsOrderCfgService;

    @ApiOperation(value = "新增", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 20)
    @PostMapping("/add/cfg")
    public Result addOrderConfig(@Valid  @RequestBody CallFrame CallFrame) {
        return dzWmsOrderCfgService.addOrderConfig(CallFrame);
    }

    //查找MOM下发的订单

    @ApiOperation(value = "编辑", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 20)
    @PostMapping("/update/cfg")
    public Result updateOrderConfig(@Valid  @RequestBody GetOrderCfig orderCfig) {
        return dzWmsOrderCfgService.updateOrderConfig(orderCfig);
    }

    @ApiOperation(value = "删除", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 20)
    @PostMapping("/del/cfg")
    public Result delOrderConfig(@RequestBody GetOrderCfig orderCfig) {
        return dzWmsOrderCfgService.delOrderConfig(orderCfig);
    }

    @ApiOperation(value = "配置列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 20)
    @PostMapping("/get/cfg")
    public Result<GetOrderCfig> getCfig(@RequestBody PageLimitBase pageLimitBase) {
        return dzWmsOrderCfgService.getCfig(pageLimitBase);
    }

    @ApiOperation(value = "历史记录", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 20)
    @PostMapping("/historical/records")
    public Result<WmsCallframeHistory> historical(@RequestBody PageLimitBase pageLimitBase) {
        return wmsService.historical(pageLimitBase);
    }

    /**
     * 订单完成信号
     *
     * @param
     * @return
     */
    @ApiOperation(value = "订单完成信号", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 21)
    @PostMapping("/order/completed")
    public Result<WmsRespone> orderCompleted(@Valid @RequestBody DzOrderCompleted dzOrderCompleted) {
        return wmsService.orderCompleted(dzOrderCompleted);
    }


}
