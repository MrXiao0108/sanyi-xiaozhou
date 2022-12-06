package com.dzics.data.appoint.changsha.mom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.dto.AgvParmsDto;
import com.dzics.data.appoint.changsha.mom.model.entity.MomDistributionWaitRequest;
import com.dzics.data.appoint.changsha.mom.model.entity.MomDistributionWaitRequestLog;

/**
 * <p>
 * 工序间配送请求日志记录 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-10
 */
public interface MomDistributionWaitRequestLogService extends IService<MomDistributionWaitRequestLog> {

    MomDistributionWaitRequestLog getMomDistributionWaitRequestLog(AgvParmsDto parmsDto);

    MomDistributionWaitRequest getMomDistributionWaitRequest(AgvParmsDto parmsDto, String code, String orderCode, String lineNo, String basketType, String pointModel, String taskType);
}
