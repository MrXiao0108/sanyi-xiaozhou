package com.dzics.data.appoint.changsha.mom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.db.dao.MomDistributionWaitRequestDao;
import com.dzics.data.appoint.changsha.mom.model.dto.AgvParmsDto;
import com.dzics.data.appoint.changsha.mom.model.entity.MomDistributionWaitRequest;
import com.dzics.data.appoint.changsha.mom.service.MomDistributionWaitRequestService;
import com.dzics.data.appoint.changsha.mom.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 工序间配送等待请求 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-10
 */
@Service
public class MomDistributionWaitRequestServiceImpl extends ServiceImpl<MomDistributionWaitRequestDao, MomDistributionWaitRequest>
        implements MomDistributionWaitRequestService {

    @Autowired
    private DateUtil dateUtil;

    @Override
    public MomDistributionWaitRequest getMomDistributionWaitRequest(AgvParmsDto parmsDto, String code, String orderCode, String lineNo, String basketType, String pointModel, String taskType) {
        MomDistributionWaitRequest request = new MomDistributionWaitRequest();
        request.setReqid(parmsDto.getReqId());
        request.setOrderNo(orderCode);
        request.setTaskType(taskType);
        request.setLineNo(lineNo);
        request.setBasketType(basketType);
        request.setProductNo(parmsDto.getParamRsrv1());
        request.setIsUpMach(pointModel);
        request.setReqsys(parmsDto.getReqSys());
        request.setReqtype(parmsDto.getReqType());
        request.setPallettype(parmsDto.getPalletType());
        request.setPalletno(parmsDto.getPalletNo());
        request.setSourceno(parmsDto.getSourceNo());
        request.setRequiretime(dateUtil.stringDateToformatDateYmdHmsMom(parmsDto.getRequireTime()));
        request.setSendtime(dateUtil.stringDateToformatDateYmdHmsMom(parmsDto.getSendTime()));
        request.setOrgCode("MOM");
        request.setDelFlag(false);
        request.setReqStatus(code);
        request.setCreateBy("MOM");
        return request;
    }
}
