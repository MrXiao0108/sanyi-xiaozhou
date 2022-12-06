package com.dzics.data.appoint.changsha.mom.service.wms;


import com.dzics.data.appoint.changsha.mom.model.dto.wms.CallFrame;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.GetOrderCfig;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;

/**
 * @author ZhangChengJun
 * Date 2021/12/7.
 * @since
 */
public interface DzWmsOrderCfgService {
    /**
     * 新增配置
     * @param callFrame
     * @return
     */
    Result addOrderConfig(CallFrame callFrame);

    /**
     * 配置列表
     * @param pageLimitBase
     * @return
     */
    Result getCfig(PageLimitBase pageLimitBase);

    Result updateOrderConfig(GetOrderCfig orderCfig);

    Result delOrderConfig(GetOrderCfig orderCfig);

}
