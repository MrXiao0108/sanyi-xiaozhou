package com.dzics.data.appoint.changsha.mom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.db.dao.MomDistributionWaitRequestLogDao;
import com.dzics.data.appoint.changsha.mom.model.dto.AgvParmsDto;
import com.dzics.data.appoint.changsha.mom.model.entity.MomDistributionWaitRequest;
import com.dzics.data.appoint.changsha.mom.model.entity.MomDistributionWaitRequestLog;
import com.dzics.data.appoint.changsha.mom.service.MomDistributionWaitRequestLogService;
import com.dzics.data.appoint.changsha.mom.util.DateUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 工序间配送请求日志记录 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-10
 */
@Service
public class MomDistributionWaitRequestLogServiceImpl extends ServiceImpl<MomDistributionWaitRequestLogDao, MomDistributionWaitRequestLog>
        implements MomDistributionWaitRequestLogService {

    @Autowired
    private DateUtil dateUtil;

    @Override
    public MomDistributionWaitRequestLog getMomDistributionWaitRequestLog(AgvParmsDto parmsDto) {
        //            保存请求日志
        MomDistributionWaitRequestLog requestLog = new MomDistributionWaitRequestLog();
        requestLog.setReqid(parmsDto.getReqId());
        requestLog.setReqsys(parmsDto.getReqSys());
        requestLog.setReqtype(parmsDto.getReqType());
        requestLog.setPallettype(parmsDto.getPalletType());
        requestLog.setPalletno(parmsDto.getPalletNo());
        requestLog.setSourceno(parmsDto.getSourceNo());
        requestLog.setRequiretime(dateUtil.stringDateToformatDateYmdHmsMom(parmsDto.getRequireTime()));
        requestLog.setSendtime(dateUtil.stringDateToformatDateYmdHmsMom(parmsDto.getSendTime()));
        requestLog.setOrgCode("MOM");
        requestLog.setDelFlag(false);
        return requestLog;
    }

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
