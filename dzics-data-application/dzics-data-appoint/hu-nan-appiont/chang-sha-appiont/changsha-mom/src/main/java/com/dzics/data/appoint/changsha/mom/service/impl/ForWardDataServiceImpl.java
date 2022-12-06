package com.dzics.data.appoint.changsha.mom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.dzics.data.appoint.changsha.mom.exception.CustomMomException;
import com.dzics.data.appoint.changsha.mom.model.constant.MomVersion;
import com.dzics.data.appoint.changsha.mom.model.dto.IssueOrderInformation;
import com.dzics.data.appoint.changsha.mom.model.dto.MomHeader;
import com.dzics.data.appoint.changsha.mom.model.dto.Task;
import com.dzics.data.appoint.changsha.mom.model.dto.mom.OprSequence;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrderPath;
import com.dzics.data.appoint.changsha.mom.model.vo.ResultDto;
import com.dzics.data.appoint.changsha.mom.service.CachingApi;
import com.dzics.data.appoint.changsha.mom.service.ForWardDataService;
import com.dzics.data.appoint.changsha.mom.service.MomOrderPathService;
import com.dzics.data.appoint.changsha.mom.service.MomOrderService;
import com.dzics.data.appoint.changsha.mom.util.MomTaskType;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.service.DzWorkStationManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author xnb
 * @date 2022/10/8 0008 10:53
 */
@Service
@Slf4j
public class ForWardDataServiceImpl implements ForWardDataService {

    @Autowired
    private MomOrderService proTaskOrderService;
    @Autowired
    private MomOrderPathService momOrderPathService;

    @Autowired
    private CachingApi cachingApi;

    @Autowired
    private DzWorkStationManagementService stationManagementService;

    @Override
    public ResultDto forWardData(String msg) {
        ResultDto resultDto = new ResultDto();
        MomHeader header = JSONObject.parseObject(msg, MomHeader.class);
        try {
//        生产订单下发
            if(MomTaskType.MOM_ORDER_TYPE.equals(header.getTaskType())){
                IssueOrderInformation<Task> task = JSONObject.parseObject(msg, new TypeReference<IssueOrderInformation<Task>>() {});
                return proTaskOrderService.saveMomOrder(task, msg);
            }
//        工位信息配置下发
            if(MomTaskType.MOM_ORDER_OPR_SEQUENCE.equals(header.getTaskType())){
                IssueOrderInformation<OprSequence> task = JSONObject.parseObject(msg, new TypeReference<IssueOrderInformation<OprSequence>>() {});
                DzProductionLine line = cachingApi.getOrderIdAndLineId();
                MomOrderPath oldMomOrderPath = momOrderPathService.getNewByMomOrder(line.getOrderId());
                OprSequence oprSequence = task.getTask();
                oprSequence.setWorkStation(oldMomOrderPath.getWorkStation());
                oprSequence.setWorkStationName(oldMomOrderPath.getWorkStationName());
                oprSequence.setWorkCenter(oldMomOrderPath.getWorkCenter());
                oprSequence.setWorkCenterName(oldMomOrderPath.getWorkCenterName());
                return momOrderPathService.saveOrderOprSequence(task,msg);
            }
        }catch(Throwable throwable){
            log.error("接收MOM订单:{}", msg, throwable);
            resultDto.setVersion(ObjectUtils.isNotEmpty(header) ? header.getVersion() : MomVersion.VERSION);
            resultDto.setTaskId(ObjectUtils.isNotEmpty(header) ? header.getTaskId() : "");
            resultDto.setCode("500");
            if (throwable instanceof CustomMomException || throwable instanceof CustomException) {
                resultDto.setMsg(throwable.getMessage());
            } else {
                resultDto.setMsg(CustomResponseCode.ERR0.getChinese());
            }
        }
        return resultDto;
    }
}
