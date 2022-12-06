package com.dzics.data.appoint.changsha.mom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.dto.MaterialParmsDto;
import com.dzics.data.appoint.changsha.mom.model.entity.MomWaitCallMaterialReq;
import com.dzics.data.appoint.changsha.mom.model.vo.ResultVo;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 生产叫料待完成请求 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-10
 */
public interface MomWaitCallMaterialReqService extends IService<MomWaitCallMaterialReq> {

    void saveWaitCallMateReq(Date date, String reqId, String reqSys, String materialType, String sequenceNo, String oprSequenceNo, String sourceNo, String wipOrderNo,
                             String reqType, List<MaterialParmsDto> materialLists, Gson gson, ResultVo body, String waitMaterialId, String basketType, String productNo);
}
