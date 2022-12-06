package com.dzics.data.appoint.changsha.mom.service.wms;


import com.dzics.data.appoint.changsha.mom.model.dto.wms.CallFrame;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.DzLocation;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.DzOrderCompleted;
import com.dzics.data.appoint.changsha.mom.model.vo.wms.WmsRespone;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;

/**
 * @author ZhangChengJun
 * Date 2021/12/6.
 * @since
 */
public interface DzWmsService {
    /**
     * 呼叫料框
     * 叫框接料
     * @param dzCallFrame
     * @return
     */
    Result callFrame(CallFrame dzCallFrame);

    /**
     * 机械手放货位置申请
     * @param dzLocation
     * @return
     */
    Result locationRequest(DzLocation dzLocation);

    /**
     * 订单完成信号
     * @param dzOrderCompleted
     * @return
     */
    Result orderCompleted(DzOrderCompleted dzOrderCompleted);

    /**
     * 放料完成
     * @param dzLocation
     * @return
     */
    Result<WmsRespone> putCompleted(DzLocation dzLocation);

    /**
     * 发送请求历史
     * @param pageLimitBase
     * @return
     */
    Result historical(PageLimitBase pageLimitBase);
}
