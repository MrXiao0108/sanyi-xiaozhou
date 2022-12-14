package com.dzics.data.appoint.changsha.mom.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.appoint.changsha.mom.config.MapConfig;
import com.dzics.data.appoint.changsha.mom.db.dao.MomWaitCallMaterialReqLogDao;
import com.dzics.data.appoint.changsha.mom.enums.MOMAGVReqTypeEnum;
import com.dzics.data.appoint.changsha.mom.exception.CustomMomException;
import com.dzics.data.appoint.changsha.mom.model.constant.*;
import com.dzics.data.appoint.changsha.mom.model.dto.AgvTask;
import com.dzics.data.appoint.changsha.mom.model.dto.mom.EmptyFrameMovesDzdc;
import com.dzics.data.appoint.changsha.mom.model.dto.MaterialFrameRes;
import com.dzics.data.appoint.changsha.mom.model.dto.agv.AgvClickSignal;
import com.dzics.data.appoint.changsha.mom.model.entity.MomDistributionWaitRequest;
import com.dzics.data.appoint.changsha.mom.model.entity.MomWaitCallMaterialReq;
import com.dzics.data.appoint.changsha.mom.model.entity.MomWaitCallMaterialReqLog;
import com.dzics.data.appoint.changsha.mom.model.vo.ResultDto;
import com.dzics.data.appoint.changsha.mom.mq.RabbitMQService;
import com.dzics.data.appoint.changsha.mom.service.*;
import com.dzics.data.appoint.changsha.mom.udp.UDPUtil;
import com.dzics.data.appoint.changsha.mom.util.AutomaticGuidedVehicle;
import com.dzics.data.appoint.changsha.mom.util.MomTaskType;
import com.dzics.data.appoint.changsha.mom.util.RedisUniqueID;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.AgvPalletType;
import com.dzics.data.common.base.model.constant.DzUdpType;
import com.dzics.data.common.base.model.constant.LogClientType;
import com.dzics.data.common.base.model.constant.PointType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.logms.model.entity.SysRealTimeLogs;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.rabbitmq.service.RabbitmqService;
import com.dzics.data.redis.util.RedisUtil;
import com.google.gson.Gson;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @Classname AgvRobackServiceImpl
 * @Description ??????
 * @Date 2022/7/11 17:07
 * @Created by NeverEnd
 */
@Slf4j
@Service
public class AgvRobackServiceImpl implements AgvRobackService {
    @Autowired
    private MOMAGVServiceImpl momagvService;
    @Autowired
    private RedisUniqueID redisUniqueID;
    @Autowired
    private RedisUtil<String> redisUtil;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MomWaitCallMaterialReqService waitCallMaterialReqService;
    @Autowired
    private MOMBaseService momBaseService;
    @Autowired
    private MomDistributionWaitRequestService distributionWaitRequestService;
    @Autowired
    private MomWaitCallMaterialReqLogDao callMaterialReqLogDao;
    @Value("${business.robot.ip}")
    private String busIpPort;
    @Value("${business.robot.material.click.path}")
    private String materialClick;
    @Value(("${accq.read.cmd.queue.equipment.realTime}"))
    private String logQuery;

    @Autowired
    private RabbitmqService rabbitmqService;
    @Autowired
    private CachingApi cachingApi;

    /**
     * ??????agv ?????? ?????????
     */
    @Value("${call.direct.agv.exchange}")
    private String exchange;
    @Value("${call.direct.agv.routing.repeatTradeRouting}")
    private String routing;
    @Value("${dzdc.udp.client.qr.port}")
    private Integer plcPort;
    @Autowired
    private MapConfig mapConfig;
    @Autowired
    private RabbitMQService rabbitMQService;

    /**
     * ??????AGV????????????
     *
     * @param vehicle ?????????agv
     * @return ????????????
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public synchronized ResultDto automaticGuidedVehicle(AutomaticGuidedVehicle vehicle) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(vehicle);
            log.info("????????? AGV????????????????????????????????? json???{}", json);
            if (vehicle == null) {
                throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR43.getChinese(), MomVersion.VERSION, redisUniqueID.getUUID());
            }
            AgvTask task = vehicle.getTask();
            if (task == null) {
                throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR12.getChinese(), vehicle.getVersion(), vehicle.getTaskId());
            }
            String reqId = task.getReqId();
            String order_status = task.getOrder_Status();
            String myReqMomType = getMyReqTypeId(reqId);
            if (MyReqMomType.CALL_MATERIAL.equals(myReqMomType)) {
                log.info("????????????????????????:{}", json);
//               ??????????????????
                MomWaitCallMaterialReq materialReq = new MomWaitCallMaterialReq();
                materialReq.setOrderStatus(order_status);
                QueryWrapper<MomWaitCallMaterialReq> wp = new QueryWrapper<>();
                wp.eq("reqId", reqId);
                boolean update = waitCallMaterialReqService.update(materialReq, wp);
                log.info("?????????????????????????????????{},materialReq:{}", update, materialReq);
                if (AgvRollBackStatus.End_Point_OK.equals(order_status)) {
                    MomWaitCallMaterialReq one = waitCallMaterialReqService.getOne(wp);
                    if (one == null) {
                        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR93);
                    }
                    String sourceno = one.getSourceno();
                    if (!StringUtils.isEmpty(sourceno)) {
                        updatePointStatus(sourceno, "??????????????????");
                    }
                    try {
                        if (StringUtil.isEmpty(task.getDestNo())) {
                            throw new CustomException(CustomExceptionType.Parameter_Exception, CustomResponseCode.ERR12.getChinese());
                        }
                        String basketType = one.getBasketType();
                        DzProductionLine line = cachingApi.getOrderIdAndLineId();
                        String lineNo = line.getLineNo();
                        String orderNo = line.getOrderNo();
                        log.info("???????????????????????????????????? ??????,??????:{} ?????????{} :?????????{}", basketType, orderNo, lineNo);
                        String groupId = redisUniqueID.getGroupId();
                        String innerGroupId = redisUniqueID.getGroupId();
                        MaterialFrameRes frameRes = momBaseService.getStringPalletType(innerGroupId, groupId, orderNo, lineNo, sourceno, "");
                        boolean b = chlickSignal(basketType, orderNo, lineNo, frameRes.getPalletNo());
                        log.info("???????????????????????????????????? ??????,??????:{} ?????????{} :?????????{}", basketType, orderNo, lineNo);
                    } catch (Throwable throwable) {
                        log.error("?????????????????????????????????????????????{}", throwable.getMessage(), throwable);
                    }
                }
                ResultDto resultDto = new ResultDto();
                resultDto.setCode("0");
                resultDto.setMsg("OK");
                resultDto.setVersion(vehicle.getVersion());
                resultDto.setTaskId(vehicle.getTaskId());
                return resultDto;
            } else {
                MomDistributionWaitRequest waitRequest = new MomDistributionWaitRequest();
                waitRequest.setOrderStatus(order_status);
                QueryWrapper<MomDistributionWaitRequest> wp = new QueryWrapper<>();
                wp.eq("reqId", reqId);
                boolean update = distributionWaitRequestService.update(waitRequest, wp);
                log.info("??????????????????????????? ???????????????????????? update:{},waitRequest:{}", update, waitRequest);
                if (AgvRollBackStatus.End_Point_OK.equals(order_status)) {
                    log.info(" ---> 1");
                    String ishand = redisUtil.get(reqId + ":" + order_status);
                    if (ishand != null) {
                        log.error("AGV?????????????????? ??????:{}", JSONObject.toJSONString(vehicle));
                    } else {
                        log.info(" ---> 2");
                        boolean ok = redisUtil.set(reqId + ":" + order_status, "OK", 24 * 60 * 60);
                        if (!ok) {
                            log.error("??????????????????: ??????????????????: {}", JSONObject.toJSONString(vehicle));
                        }
                        log.info(" ---> 3");
                        boolean b1 = rabbitmqService.sendDataCenter(routing, exchange, vehicle);
                        log.info(" ---> 4" + b1);
                        if (!b1) {
                            throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR0.getChinese(), vehicle.getVersion(), vehicle.getTaskId());
                        }
                    }
                }
                ResultDto resultDto = new ResultDto();
                resultDto.setCode("0");
                resultDto.setMsg("OK");
                resultDto.setVersion(vehicle.getVersion());
                resultDto.setTaskId(vehicle.getTaskId());
                return resultDto;
            }
        } catch (Throwable throwable) {
            Gson gson = new Gson();
            String json = gson.toJson(vehicle);
            log.error("????????? AGV??????????????????:{} ??????????????? ???????????????{}", json, throwable);
            throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR0.getChinese(), vehicle.getVersion(), vehicle.getTaskId());
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void handelAgvMessage(String reqId) {
        try {
            QueryWrapper<MomDistributionWaitRequest> wpRes = new QueryWrapper<>();
            wpRes.eq("reqId", reqId);
            MomDistributionWaitRequest request = distributionWaitRequestService.getOne(wpRes);
            if (request == null) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR94);
            }
            log.info("AgvRobackServiceImpl [handelAgvMessage] MomDistributionWaitRequest{}", request);
            String isUpMach = request.getIsUpMach();
            String basketType = request.getBasketType();
            String orderNo = request.getOrderNo();
            String lineNo = request.getLineNo();
            String taskType = request.getTaskType();
            String sourceno = request.getSourceno();
            if (MomTaskType.CALL_MATERIAL.equals(taskType)) {
                if (MOMAGVReqTypeEnum.IN_EMPTY_CONTAINER.val().equals(request.getReqtype())) {
                    log.info("?????????????????? ????????? ?????? ?????? ,??????:{} ?????????{} :?????????{}", orderNo, lineNo, basketType);
                    updatePointStatus(sourceno, "???????????????");
                    String groupId = redisUniqueID.getGroupId();
                    String innerGroupId = redisUniqueID.getGroupId();
                    MaterialFrameRes frameRes = momBaseService.getStringPalletType(innerGroupId, groupId, orderNo, lineNo, sourceno, request.getProductNo());
                    String palletNo = frameRes.getPalletNo();
                    request.setPalletno(palletNo);
                    boolean b = distributionWaitRequestService.updateById(request);
                    log.info("??????????????????????????????:??????ID: {},????????????: {}", request.getWorkpieceDistributionId(), palletNo);
                    chlickSignal(basketType, orderNo, lineNo, palletNo);
                    log.info("?????????????????? ????????? ?????? ?????? ,??????:{} ?????????{} :?????????{}", orderNo, lineNo, basketType);
                }
                if (MOMAGVReqTypeEnum.OUT_FULL_CONTAINER.val().equals(request.getReqtype())) {
                    if (PointType.SLXL.equals(isUpMach)) {
                        updatePointStatus(sourceno, "?????????????????????");
                        //??????????????????
                        EmptyFrameMovesDzdc frameMoves = new EmptyFrameMovesDzdc();
                        frameMoves.setBasketType(basketType);
                        frameMoves.setPalletType(AgvPalletType.DNU);
                        frameMoves.setOrderCode(orderNo);
                        frameMoves.setLineNo(lineNo);
                        frameMoves.setDeviceType("DZDC");
                        Result result = momagvService.processDistribution(frameMoves);
                        log.info("????????????????????????:{}", JSONObject.toJSONString(result));
                    } else {
                        try {
//                          ?????????????????????  ?????????????????????
                            updatePointStatus(sourceno, "?????????????????????");
                            EmptyFrameMovesDzdc movesDzdc = new EmptyFrameMovesDzdc();
                            movesDzdc.setBasketType(basketType);
                            movesDzdc.setPalletType(AgvPalletType.GNU);
                            movesDzdc.setOrderCode(orderNo);
                            movesDzdc.setLineNo(request.getLineNo());
                            movesDzdc.setQuantity(0);
                            movesDzdc.setDeviceType("DZDC");
                            movesDzdc.setSyProductNo(request.getProductNo());
                            Result result = momagvService.processDistribution(movesDzdc);
                            log.info("?????????????????????:{}", result);
                        } catch (Throwable throwable) {
                            log.error("????????????????????????:{}", throwable.getMessage(), throwable);
                        }
                    }
                }
                if (MOMAGVReqTypeEnum.OUT_EMPTY_CONTAINER.val().equals(request.getReqtype())) {
//                            ?????????????????????
                    if (PointType.UP.equals(isUpMach)) {
                        updatePointStatus(sourceno, "?????????????????????");
                        EmptyFrameMovesDzdc frameMoves = new EmptyFrameMovesDzdc();
                        frameMoves.setBasketType(basketType);
                        frameMoves.setPalletType(AgvPalletType.DNU);
                        frameMoves.setOrderCode(orderNo);
                        frameMoves.setLineNo(lineNo);
                        frameMoves.setDeviceType("DZDC");
                        Result result = momagvService.processDistribution(frameMoves);
                        log.info("????????????????????????:{}", JSONObject.toJSONString(result));
                    }
                }
            } else {
                log.error("??????????????????????????????{}", taskType);
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR941);
            }
        } catch (Throwable throwable) {
            log.error("AGV ???????????????????????? reqId:{},?????????{}", reqId, throwable.getMessage(), throwable);
        }
    }

    @Override
    public Result chlickSignal(AgvClickSignal clickSignal) {
        SysRealTimeLogs timeLogs = new SysRealTimeLogs();
        String orderNo = clickSignal.getOrderNo();
        String lineNo = clickSignal.getLineNo();
        String msgID = UUID.randomUUID().toString().replaceAll("-", "");
        timeLogs.setMessageId(msgID);
        timeLogs.setQueueName(logQuery);
        timeLogs.setClientId(LogClientType.BUS_AGV);
        timeLogs.setOrderCode(orderNo);
        timeLogs.setLineNo(lineNo);
        timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
        timeLogs.setDeviceCode(clickSignal.getBasketType());
        timeLogs.setMessageType(1);
        timeLogs.setMessage("?????? " + clickSignal.getBasketType() + " ?????????????????????");
        timeLogs.setTimestampTime(new Date());
        //     ?????????????????????
        boolean b = rabbitMQService.sendRabbitmqLog(JSONObject.toJSONString(timeLogs));
        //      ?????????????????? mq ????????? UDP ??????
        Map<String, String> mapIps = mapConfig.getMaps();
        String plcIp = mapIps.get(orderNo);
        if (CollectionUtils.isNotEmpty(mapIps) && !StringUtils.isEmpty(plcIp)) {
            String palletNo = clickSignal.getPalletNo();
            if (StringUtils.isEmpty(palletNo)) {
                palletNo = "";
                log.warn("AGV ?????????????????????????????????palletNo: {}", palletNo);
            }
            String msg = "Q," + DzUdpType.udpType_AGV + "," + DzUdpType.udpTypeAgvSinal + "," + clickSignal.getBasketType() + "," + msgID + "," + orderNo + "," + lineNo;
            boolean sendResult = UDPUtil.sendUdpServer(plcIp, plcPort, msg);
            if (sendResult) {
                log.info("AgvRobackServiceImpl [chlickSignal] ???????????? msg:{}", msg);
            } else {
                log.error("AgvRobackServiceImpl [chlickSignal] ?????? msg:{}", msg);
            }
        } else {
            log.error("?????????????????????????????????UDP IP ???????????????orderNo : {}, lineNo: {} , mapIps: {}", orderNo, lineNo, mapIps);
        }

        return Result.ok();
    }

    private void updatePointStatus(String sourceno, String pointStatus) {
        try {
            if (!StringUtils.isEmpty(sourceno)) {
                redisUtil.set(ChangShaRedisKey.MATERIAL_POINT_STATUS + sourceno, pointStatus);
            }
        } catch (Throwable throwable) {
            log.error("????????????????????????:{}", throwable.getMessage(), throwable);
        }
    }


    private boolean chlickSignal(String basketType, String orderNo, String lineNo, String palletNo) {
        String url = busIpPort + materialClick;
        AgvClickSignal clickSignal = new AgvClickSignal();
        clickSignal.setBasketType(basketType);
        clickSignal.setOrderNo(orderNo);
        clickSignal.setLineNo(lineNo);
        clickSignal.setPalletNo(palletNo);
        Result result = this.chlickSignal(clickSignal);
        log.info("??????AGV???????????? url: {} ?????????:{} ??????????????????{}", url, JSON.toJSONString(clickSignal), JSON.toJSONString(result));
        return true;
    }

    private String getMyReqTypeId(String reqId) {
        String type = redisUtil.get(ChangShaRedisKey.MOM_HTTP_REQUEST_SERVICE_GET_MY_REQ_TYPE_ID + reqId);
        if (type != null) {
            return type;
        }
        QueryWrapper<MomWaitCallMaterialReqLog> wp = new QueryWrapper<>();
        wp.eq("reqId", reqId);
        MomWaitCallMaterialReqLog one = callMaterialReqLogDao.selectOne(wp);
        if (one != null) {
            return MyReqMomType.CALL_MATERIAL;
        }
        return MyReqMomType.DISTRIBUTION;
    }

}
