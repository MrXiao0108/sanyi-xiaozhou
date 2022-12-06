package com.dzics.data.appoint.changsha.mom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.db.dao.MomWaitCallMaterialReqDao;
import com.dzics.data.appoint.changsha.mom.model.dto.MaterialParmsDto;
import com.dzics.data.appoint.changsha.mom.model.entity.MomWaitCallMaterialReq;
import com.dzics.data.appoint.changsha.mom.model.vo.ResultVo;
import com.dzics.data.appoint.changsha.mom.service.MomWaitCallMaterialReqService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 生产叫料待完成请求 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-10
 */
@Service
@Slf4j
public class MomWaitCallMaterialReqServiceImpl extends ServiceImpl<MomWaitCallMaterialReqDao, MomWaitCallMaterialReq> implements MomWaitCallMaterialReqService {


    @Override
    public void saveWaitCallMateReq(Date date, String reqId, String reqSys, String materialType, String sequenceNo, String oprSequenceNo, String sourceNo, String wipOrderNo, String reqType, List<MaterialParmsDto> materialLists, Gson gson, ResultVo body, String waitMaterialId, String basketType, String productNo) {
        MomWaitCallMaterialReq callMaterialReq = new MomWaitCallMaterialReq();
        callMaterialReq.setWaitMaterialId(waitMaterialId);
        callMaterialReq.setReqid(reqId);
        callMaterialReq.setReqsys(reqSys);
        callMaterialReq.setProductNo(productNo);
        callMaterialReq.setMaterialType(materialType);
        callMaterialReq.setSequenceno(sequenceNo);
        callMaterialReq.setBasketType(basketType);
        callMaterialReq.setOprsequenceno(oprSequenceNo);
        callMaterialReq.setReqtype(reqType);
        callMaterialReq.setSourceno(sourceNo);
        callMaterialReq.setWiporderno(wipOrderNo);
        callMaterialReq.setRequiretime(date);
        callMaterialReq.setSendtime(date);
        callMaterialReq.setMateriallist(gson.toJson(materialLists));
        callMaterialReq.setReqStatus(body.getCode());
        callMaterialReq.setOrgCode("ROB");
        callMaterialReq.setDelFlag(false);
        callMaterialReq.setCreateBy("ROB");
        callMaterialReq.setCreateTime(new Date());
        this.save(callMaterialReq);
    }
}
