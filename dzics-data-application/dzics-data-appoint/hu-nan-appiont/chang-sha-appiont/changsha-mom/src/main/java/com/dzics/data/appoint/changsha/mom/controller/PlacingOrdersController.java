package com.dzics.data.appoint.changsha.mom.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.dzics.data.appoint.changsha.mom.exception.CustomMomException;
import com.dzics.data.appoint.changsha.mom.model.constant.MomVersion;
import com.dzics.data.appoint.changsha.mom.model.dto.IssueOrderInformation;
import com.dzics.data.appoint.changsha.mom.model.dto.MomHeader;
import com.dzics.data.appoint.changsha.mom.model.dto.mom.MOMAGVHandshakeDto;
import com.dzics.data.appoint.changsha.mom.model.dto.mom.MOMStorageDto;
import com.dzics.data.appoint.changsha.mom.model.dto.mom.OprSequence;
import com.dzics.data.appoint.changsha.mom.model.dto.Task;
import com.dzics.data.appoint.changsha.mom.model.vo.ResultDto;
import com.dzics.data.appoint.changsha.mom.service.*;
import com.dzics.data.appoint.changsha.mom.util.AutomaticGuidedVehicle;
import com.dzics.data.appoint.changsha.mom.util.MomTaskType;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 下发订单
 *
 * @author ZhangChengJun
 * Date 2021/5/28.
 * @since
 */
@Api(tags = {"总控订单"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Slf4j
public class PlacingOrdersController {
    @Autowired
    private MomOrderService proTaskOrderService;
    @Autowired
    private MomOrderPathService momOrderPathService;
    @Autowired
    private AgvRobackService agvRobackService;
    @Autowired
    private MomMaterialStorageService momMaterialStorageService;
    @Autowired
    private MOMAGVService momagvService;

    /**
     * 端口8082
     * 总控->中控 订单任务下发
     * http://10.0.91.172:8056/SANY/gateway/pdev/sany/task/post
     *
     * @param momParms
     * @return
     */
    @ApiOperation(value = "接收MOM请求统一API")
    @PostMapping("/SANY/gateway/pdev/sany/slaveId/task/post")
    public ResultDto myRequest(@RequestBody String momParms) {
        log.info("收到到MOM请求参数：{}", momParms);
        MomHeader header = JSONObject.parseObject(momParms, MomHeader.class);
        try {
            String taskType = header.getTaskType();
            if (MomTaskType.MOM_ORDER_TYPE.equals(taskType)) {
//              生产订单下发
//                json字符串 转化为泛型对象
                IssueOrderInformation<Task> task = JSONObject.parseObject(momParms, new TypeReference<IssueOrderInformation<Task>>() {});
                return proTaskOrderService.saveMomOrder(task, momParms);
            }
            if (MomTaskType.MOM_ORDER_OPR_SEQUENCE.equals(taskType)) {
                IssueOrderInformation<OprSequence> task = JSONObject.parseObject(momParms, new TypeReference<IssueOrderInformation<OprSequence>>() {
                });
                return momOrderPathService.saveOrderOprSequence(task,momParms);
            }
            if (MomTaskType.AGV_HANDLING_FEEDBACK.equals(taskType)) {
//                AGV搬运反馈信息确认到中控
                AutomaticGuidedVehicle vehicle = JSONObject.parseObject(momParms, new TypeReference<AutomaticGuidedVehicle>() {
                });
                return agvRobackService.automaticGuidedVehicle(vehicle);
            }
            if (MomTaskType.STORAGE_ISSUE.equals(taskType)) {
                IssueOrderInformation<MOMStorageDto> task = JSON.parseObject(momParms, new TypeReference<IssueOrderInformation<MOMStorageDto>>() {
                });
                return momMaterialStorageService.storageHandle(task);
            }
            if (MomTaskType.AGV_HANDSHAKE.equals(taskType)) {
                IssueOrderInformation<MOMAGVHandshakeDto> task = JSON.parseObject(momParms, new TypeReference<IssueOrderInformation<MOMAGVHandshakeDto>>() {
                });
                return momagvService.handshakeHandle(task);
            }
            throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR74.getChinese());
        } catch (Throwable throwable) {
            log.error("接收MOM订单:{}", momParms, throwable);
            ResultDto resultDto = new ResultDto();
            resultDto.setVersion(ObjectUtils.isNotEmpty(header) ? header.getVersion() : MomVersion.VERSION);
            resultDto.setTaskId(ObjectUtils.isNotEmpty(header) ? header.getTaskId() : "");
            resultDto.setCode("500");
            if (throwable instanceof CustomMomException || throwable instanceof CustomException) {
                resultDto.setMsg(throwable.getMessage());
            } else {
                resultDto.setMsg(CustomResponseCode.ERR0.getChinese());
            }
            return resultDto;
        }
    }
}
