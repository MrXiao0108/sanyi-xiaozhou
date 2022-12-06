package com.dzics.data.appoint.changsha.mom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.dto.AgvParmsDto;
import com.dzics.data.appoint.changsha.mom.model.entity.MomDistributionWaitRequest;

/**
 * <p>
 * 工序间配送等待请求 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-10
 */
public interface MomDistributionWaitRequestService extends IService<MomDistributionWaitRequest> {

    MomDistributionWaitRequest getMomDistributionWaitRequest(AgvParmsDto parmsDto, String code, String orderCode, String lineNo, String basketType, String pointModel, String taskType);
}
