package com.dzics.data.appoint.changsha.mom.service.impl.wms;

import com.dzics.data.appoint.changsha.mom.model.dto.wms.CallFrame;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.GetOrderCfig;
import com.dzics.data.appoint.changsha.mom.model.entity.WmsOrderConfig;
import com.dzics.data.appoint.changsha.mom.service.wms.DzWmsOrderCfgService;
import com.dzics.data.appoint.changsha.mom.service.wms.WmsOrderConfigService;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/12/7.
 * @since
 */
@Slf4j
@Service
public class DzWmsOrderCfgServiceImpl implements DzWmsOrderCfgService {
    @Autowired
    private WmsOrderConfigService wmsOrderConfigService;

    @Override
    public Result addOrderConfig(CallFrame callFrame) {
        try {
            //更新内容维护物料信息
            WmsOrderConfig wmsOrderConfig = new WmsOrderConfig();
            wmsOrderConfig.setRfid(callFrame.getRfid());
            //新增
            wmsOrderConfig.setOrderNum(callFrame.getOrderNum());
            //新增物料号
            wmsOrderConfig.setMaterialCode(callFrame.getMaterialCode());
            //新增机构编码
            wmsOrderConfig.setOrgCode("WMS-000");
            wmsOrderConfig.setDelFlag(false);
            wmsOrderConfig.setCreateBy("UI");
            wmsOrderConfigService.save(wmsOrderConfig);
            return Result.ok();
        } catch (Throwable throwable) {
            return Result.error(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR43);
        }

    }

    @Override
    public Result getCfig(PageLimitBase pageLimitBase) {
        PageHelper.startPage(pageLimitBase.getPage(), pageLimitBase.getLimit());
        List<GetOrderCfig> orderCfigs = wmsOrderConfigService.getCfg(pageLimitBase.getField(), pageLimitBase.getType());
        PageInfo<GetOrderCfig> info = new PageInfo<>(orderCfigs);
        return Result.ok(info.getList(), info.getTotal());
    }

    @Override
    public Result updateOrderConfig(GetOrderCfig orderCfig) {
        WmsOrderConfig wmsOrderConfig = new WmsOrderConfig();
        wmsOrderConfig.setConfigOrderId(orderCfig.getConfigOrderId());
        wmsOrderConfig.setOrderNum(orderCfig.getOrderNum());
        wmsOrderConfig.setMaterialCode(orderCfig.getMaterialCode());
        wmsOrderConfig.setRfid(orderCfig.getRfid());
        wmsOrderConfigService.updateById(wmsOrderConfig);
        return Result.ok();
    }

    @Override
    public Result delOrderConfig(GetOrderCfig orderCfig) {
        wmsOrderConfigService.removeById(orderCfig.getConfigOrderId());
        return Result.ok();
    }
}
