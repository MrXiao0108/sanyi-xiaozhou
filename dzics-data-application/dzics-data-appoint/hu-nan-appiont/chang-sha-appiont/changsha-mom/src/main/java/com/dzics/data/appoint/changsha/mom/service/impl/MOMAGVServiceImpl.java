package com.dzics.data.appoint.changsha.mom.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.dzics.data.appoint.changsha.mom.config.MomRequestPath;
import com.dzics.data.appoint.changsha.mom.config.mq.AGVConfig;
import com.dzics.data.appoint.changsha.mom.config.param.MOMReqSys;
import com.dzics.data.appoint.changsha.mom.controller.Agv2rasterController;
import com.dzics.data.appoint.changsha.mom.enums.MOMAGVHandshakeResultEnum;
import com.dzics.data.appoint.changsha.mom.enums.MOMAGVHandshakeWaitTypeEnum;
import com.dzics.data.appoint.changsha.mom.enums.MOMAGVReqTypeEnum;
import com.dzics.data.appoint.changsha.mom.enums.MomEnum;
import com.dzics.data.appoint.changsha.mom.exception.CustomMomExceptionReq;
import com.dzics.data.appoint.changsha.mom.model.constant.*;
import com.dzics.data.appoint.changsha.mom.model.dto.*;
import com.dzics.data.appoint.changsha.mom.model.dto.mom.EmptyFrameMovesDzdc;
import com.dzics.data.appoint.changsha.mom.model.dto.mom.MOMAGVHandshakeDto;
import com.dzics.data.appoint.changsha.mom.model.entity.*;
import com.dzics.data.appoint.changsha.mom.model.vo.*;
import com.dzics.data.appoint.changsha.mom.mq.RabbitMQService;
import com.dzics.data.appoint.changsha.mom.service.*;
import com.dzics.data.appoint.changsha.mom.util.*;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.RobRequestException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.AgvPalletType;
import com.dzics.data.common.base.model.constant.MomProgressStatus;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.redis.util.RedisUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: van
 * @since: 2022-07-07
 */
@Slf4j
@Service
public class MOMAGVServiceImpl implements MOMAGVService {

    @Autowired
    private RedisUtil<String> redisUtil;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private RedisUniqueID redisUniqueID;
    @Autowired
    private MomRequestPath momRequestPath;
    @Autowired
    private MomMaterialPointService momMaterialPointService;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private MOMBaseService momBaseService;
    @Autowired
    private MomWaitCallMaterialService momWaitCallMaterialService;
    @Autowired
    private MomWaitCallMaterialReqService momWaitCallMaterialReqService;
    @Autowired
    private MomDistributionWaitRequestService momDistributionWaitRequestService;
    @Autowired
    private MomWaitCallMaterialReqLogService momWaitCallMaterialReqLogService;
    @Autowired
    private MomDistributionWaitRequestLogService momDistributionWaitRequestLogService;
    @Autowired
    private MOMReqSys momReqSys;
    @Value("${order.code}")
    private String orderCode;
    @Autowired
    private MomOrderPathService momOrderPathService;
    @Autowired
    private RabbitMQService rabbitMQService;
    @Autowired
    private MomCommunicationLogService momCommunicationLogService;
    @Autowired
    private CachingApi cachingApi;
    @Autowired
    private ListUtil listUtil;
    @Autowired
    private DzicsInsideLogService insideLogService;

    @Override
    public Result<String> callAgv(EmptyFrameMovesDzdc movesDzdc) {
        String deviceType = movesDzdc.getDeviceType();
        log.info("请求AGV :{} 开始 ++++++++++++++++++++++++++", deviceType);
        String basketType = movesDzdc.getBasketType();
        String palletType = movesDzdc.getPalletType();
        String orderCode = movesDzdc.getOrderCode();
        String lineNo = movesDzdc.getLineNo();
        log.info("类型:{} ,请求 AGV 参数：{} ", deviceType, movesDzdc);
        if (StringUtils.isEmpty(basketType) || StringUtils.isEmpty(palletType) || StringUtils.isEmpty(lineNo) | StringUtils.isEmpty(orderCode)) {
            log.warn("类型:{} , 请求 AGV 部分参数为空：{}", deviceType, movesDzdc);
            throw new RobRequestException(CustomResponseCode.ERR12);
        }
        MomUpPoint momUpPoint = momMaterialPointService.getStationCode(basketType, orderCode, lineNo);
        if (StringUtils.isEmpty(momUpPoint)) {
            log.warn("根据小车{}订单{}产线{}查询工位编号不存在；上料点小车未绑定工位", basketType, orderCode, lineNo);
            throw new RobRequestException(CustomResponseCode.ERR68);
        }

        String pointModel = momUpPoint.getPointModel();
        movesDzdc.setPointModel(pointModel);
        movesDzdc.setExternalCode(momUpPoint.getExternalCode());
        movesDzdc.setSanyPalletType(momUpPoint.getPalletType());
        MomOrder momOrder = momOrderService.getByLoading(orderCode, lineNo, MomProgressStatus.LOADING);
        if (momOrder != null) {
            movesDzdc.setWiporderno(momOrder.getWiporderno());
            movesDzdc.setProTaskOrderId(momOrder.getProTaskOrderId());
            movesDzdc.setMomOrder(momOrder);
        }
//        String momRunModel = cachingApi.getMomRunModel();
//        if("auto".equals(momRunModel)){
            if (AgvPalletType.DNU.equals(palletType)) {
                return this.callBoxMaterial(movesDzdc);
            }
            if (AgvPalletType.GNU.equals(palletType)) {
                return this.callEmptyBox(movesDzdc);
            }
            if (AgvPalletType.BNU.equals(palletType)) {
                return this.removeEmptyBox(movesDzdc);
            }
            if (AgvPalletType.ANU.equals(palletType)) {
                return this.exportFullBox(movesDzdc);
            }
//        }
        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
    }

    @Override
    public Result<String> callBoxMaterial(EmptyFrameMovesDzdc frameMoves) {
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        DzicsInsideLog insideLog = new DzicsInsideLog();
        insideLog.setBusinessType(MomTaskType.APPLICATION_MATERIAL);
        insideLog.setCreateTime(new Date());
        insideLog.setOrderId(line.getOrderId());
        insideLog.setOrderNo(line.getOrderNo());
        insideLog.setLineId(line.getId());
        insideLog.setLineNo(line.getLineNo());
        //内部请求参数
        insideLog.setRequestContent(JSON.toJSONString(frameMoves));

        log.info("MOMAGVServiceImpl [callBoxMaterial] 生产叫料请求参数: {} ", frameMoves);
        String sourceNo = frameMoves.getExternalCode();
        String proTaskOrderId = frameMoves.getProTaskOrderId();
        String wiporderno = frameMoves.getWiporderno();
        if (StringUtils.isEmpty(proTaskOrderId) || StringUtils.isEmpty(wiporderno)) {
            log.error("生产叫料不存在生产中的订单, proTaskOrderId: {} wiporderno: {} ", proTaskOrderId, wiporderno);
            insideLog.setThrowMsg("生产叫料不存在生产中的订单,Mom订单:"+wiporderno);
            insideLog.setState("1");
            throw new RobRequestException(CustomResponseCode.ERR69);
        }
        if (StringUtils.isEmpty(proTaskOrderId) || StringUtils.isEmpty(wiporderno)) {
            log.error("Mom订单ID或者Mom订单编号为空，请检查底层上发参数："+frameMoves);
            insideLog.setState("1");
            insideLog.setThrowMsg("Mom订单ID或者Mom订单编号为空，请检查底层上发参数："+frameMoves);
            throw new RobRequestException(CustomResponseCode.ERR69);
        }
        MomOrderPath momOrderPath = momOrderPathService.getproTaskOrderId(proTaskOrderId);

        String lineNo = frameMoves.getLineNo();
        String orderCode = frameMoves.getOrderCode();
        String basketType = frameMoves.getBasketType();
        /*MomUpPoint momUpPoint = (MomUpPoint) redisUtil.get("ProTaskOrderServiceImpl:processDistribution:" + orderCode + lineNo + basketType);
        if (StringUtils.isEmpty(momUpPoint)) {*/
        MomUpPoint momUpPoint = momMaterialPointService.getStationCode(basketType, orderCode, lineNo);
        if (StringUtils.isEmpty(momUpPoint)) {
            log.error("根据小车{}订单{}产线{}查询工位编号不存在；上料点小车未绑定工位", basketType, orderCode, lineNo);
            insideLog.setThrowMsg("根据小车"+basketType+",订单编号:"+orderCode+",产线:"+lineNo+",查询工位编号不存在、上料点小车未绑定工位");
            insideLog.setState("1");
            throw new RobRequestException(CustomResponseCode.ERR68);
        }
            /*redisUtil.set("ProTaskOrderServiceImpl:processDistribution:" + orderCode + lineNo + basketType, momUpPoint);
        }*/
        log.info("生产叫料开始 小车: {} , 工位信息: {}", basketType, JSONObject.toJSONString(momUpPoint));
//        获取订单的物料型号
        String stationCode = momUpPoint.getStationCode();
        List<CallMaterial> materials = momWaitCallMaterialService.getWorkStation(proTaskOrderId, stationCode);
        if (CollectionUtils.isEmpty(materials)) {
            log.error("当前叫料信息不存在：proTaskId: {},stationCode: {}", proTaskOrderId, stationCode);
            insideLog.setThrowMsg("生产叫料不存在生产中的订单,Mom订单:"+wiporderno);
            insideLog.setState("1");
            throw new RobRequestException(CustomResponseCode.ERR70);
        }
        insideLog.setState("0");
        CallMaterial materialDef = momWaitCallMaterialService.getCallMaterial(lineNo, orderCode, materials);
//       封装请求参数
        String reqId = redisUniqueID.getkey();
        AgvParmsDto dto = new AgvParmsDto();
        dto.setReqId(reqId);

        Map<String, String> maps = momReqSys.getMaps();
        dto.setReqSys(maps.get(orderCode));
        dto.setFacility(MomReqContent.FACILITY);
        dto.setReqType("0");
        dto.setPalletType("");
        dto.setPalletNo("");
        dto.setSourceNo(sourceNo);
        dto.setWipOrderNo(wiporderno);
        dto.setSequenceNo(momOrderPath.getSequenceNo());
        dto.setOprSequenceNo(momOrderPath.getOprSequenceNo());
        String now = LocalDateTimeUtil.formatNormal(LocalDateTime.now());
        dto.setRequireTime(now);
        dto.setSendTime(now);
        dto.setParamRsrv1("");
        dto.setParamRsrv2("");
        dto.setParamRsrv3("");
        MaterialParmsDto parmsDto = new MaterialParmsDto();
        if(MomConstant.ORDER_DZ_1976.equals(orderCode)){
            parmsDto.setMaterialType("");
            parmsDto.setLength(BigDecimal.valueOf(0));
            parmsDto.setWidth(BigDecimal.valueOf(0));
            parmsDto.setHeight(BigDecimal.valueOf(0));
            parmsDto.setMaterialNo(materialDef.getMaterialNo());
            parmsDto.setQuantity(BigDecimal.valueOf(frameMoves.getMomOrder().getQuantity()));
        }else{
            parmsDto.setMaterialNo(materialDef.getMaterialNo());
            parmsDto.setQuantity(BigDecimal.valueOf(frameMoves.getMomOrder().getQuantity()));
        }
        List<MaterialParmsDto> parmsDtos = new ArrayList<>();
        parmsDtos.add(parmsDto);
        dto.setMaterialList(parmsDtos);
        ProductionExpected<AgvParmsDto> expected = new ProductionExpected<>();
        expected.setTaskId(redisUniqueID.getUUID());
        expected.setTaskType(MomTaskType.APPLICATION_MATERIAL);
        expected.setVersion(MomVersion.VERSION);
        expected.setReported(dto);
        Gson gson = new Gson();
        String reqJson = gson.toJson(expected);
        MomWaitCallMaterialReqLog reqLog = new MomWaitCallMaterialReqLog();
        reqLog.setReqId(reqId);
        reqLog.setSendMsg(reqJson);
        String resultState = "1";
        try {
            reqLog.setMaterialType(materialDef.getMaterialType());
            reqLog.setSendTime(new Date());
            reqLog.setOrgCode("ROB");
            reqLog.setDelFlag(false);
            reqLog.setCreateBy("ROB");
            reqLog.setCreateTime(new Date());
            reqLog.setUpdateBy("ROB");
            redisUtil.set(ChangShaRedisKey.MOM_HTTP_REQUEST_SERVICE_GET_MY_REQ_TYPE_ID + reqId, MyReqMomType.CALL_MATERIAL, 24 * 3600);
            log.info("请求MOM叫料接口 地址：{} ,参数：{}", momRequestPath.ipPortPath, reqJson);
            ResultVo body = momBaseService.sendPost(momRequestPath.ipPortPath, reqJson, ResultVo.class);
            String gsn = gson.toJson(body);
            log.info("请求MOM叫料接口 地址：{} ,响应信息：{}", momRequestPath.ipPortPath, gsn);
            String code = body.getCode();
            reqLog.setResMomCode(code);
            reqLog.setResMsg(gsn);
            reqLog.setStatus(body.getStatusCode());
            momWaitCallMaterialReqService.saveWaitCallMateReq(new Date(), reqId, MomReqContent.REQ_SYS, "materialType", "sequenceNo", "oprSequenceNo", sourceNo, wiporderno, "1", parmsDtos, gson, body, null, basketType, materialDef.getMaterialNo());
            if (MomReqContent.MOM_CODE_OK.equals(code) || MomReqContent.MOM_CALL_WAIT.equals(code)) {
                resultState = "0";
                return Result.ok("OKOK");
            }
            /*Long quantity1 = orderAndMaterial.getQuantity();*/
            log.error("订单：{} 产线：{},请求MOM叫料接口异常 body:{}", orderCode, lineNo, body);
            throw new CustomException(gsn);
        } catch (CustomMomExceptionReq ex) {
            reqLog.setResMsg(ex.getMessage());
            log.error("发送叫料请求异常：{}", ex.getMessage(), ex);
            throw ex;
        } catch (ResourceAccessException throwable) {
            reqLog.setResMsg(throwable.getMessage());
            log.error("发送叫料请求异常：{}", throwable.getMessage(), throwable);
            throw throwable;
        } catch (RobRequestException throwable) {
            reqLog.setResMsg(throwable.getMessage());
            log.error("发送叫料请求异常：{}", throwable.getMessage(), throwable);
            throw throwable;
        } finally {
            momWaitCallMaterialReqLogService.save(reqLog);
            momCommunicationLogService.addForMQ(MomEnum.CommunicationLogType.IN.val(),
                    MomEnum.CommunicationLog.JIAO_LIAO.val(),
                    resultState,
                    reqJson,
                    reqLog.getResMsg());
            insideLogService.save(insideLog);
        }
    }

    @Override
    public Result<String> callEmptyBox(EmptyFrameMovesDzdc frameMoves) {
        log.info("MOMAGVServiceImpl [callEmptyBox] 请求空料框  请求参数: {} ", frameMoves);
        String deviceType = frameMoves.getDeviceType();
        String pointModel = frameMoves.getPointModel();
        String sourceNo = frameMoves.getExternalCode();
        String lineNo = frameMoves.getLineNo();
        String basketType = frameMoves.getBasketType();
        String proTaskOrderId = frameMoves.getProTaskOrderId();
        String wiporderno = frameMoves.getWiporderno();
        if (StringUtils.isEmpty(proTaskOrderId)) {
            log.warn("订单：{} 产线：{} 当前没有订单在生产 proTaskOrderId :{}, wiporderno: {}", orderCode, lineNo, proTaskOrderId, wiporderno);
            throw new RobRequestException(CustomResponseCode.ERR65);
        }
        String productNo = frameMoves.getSyProductNo();
        String requireTime = TimeUtil.normalTime();
        AgvParmsDto parmsDto = new AgvParmsDto();
        parmsDto.setReqId(redisUniqueID.getkey());

        Map<String, String> maps = momReqSys.getMaps();
        parmsDto.setReqSys(maps.get(orderCode));
        parmsDto.setFacility(MomReqContent.FACILITY);
        parmsDto.setReqType(MOMAGVReqTypeEnum.IN_EMPTY_CONTAINER.val());
        parmsDto.setPalletType(frameMoves.getSanyPalletType());
        parmsDto.setPalletNo("");
        parmsDto.setSourceNo(sourceNo);
        parmsDto.setMaterialGroup("");
        parmsDto.setDestNo(sourceNo);
        parmsDto.setDestPointType("");
        parmsDto.setRequireTime("");
        parmsDto.setSendTime(requireTime);
        parmsDto.setParamRsrv1("");
        parmsDto.setParamRsrv2("");
        parmsDto.setParamRsrv3("");
        parmsDto.setMaterialList(new ArrayList<>());
        //头信息
        RequestHeaderVo<AgvParmsDto> requestHeaderVo = new RequestHeaderVo<>();
        requestHeaderVo.setTaskId(redisUniqueID.getUUID());
        requestHeaderVo.setTaskType(MomTaskType.CALL_MATERIAL);
        requestHeaderVo.setVersion(MomVersion.VERSION);
        requestHeaderVo.setReported(parmsDto);
        MomDistributionWaitRequestLog requestLog = momDistributionWaitRequestLogService.getMomDistributionWaitRequestLog(parmsDto);
        requestLog.setSourceno(sourceNo);
        Gson gson = new Gson();
        String reqJson = gson.toJson(requestHeaderVo);
        String resultState = "1";
        try {
            redisUtil.set("momHttpRequestService:getMyReqTypeId:" + parmsDto.getReqId(), MyReqMomType.DISTRIBUTION, 24 * 3600);
            log.info("请求MOM 空料框接口地址：{} ,参数：{}", momRequestPath.ipPortPath, reqJson);
            ResultVo body = momBaseService.sendPost(momRequestPath.ipPortPath, reqJson, ResultVo.class);
            String code = body.getCode();
            log.info("请求MOM 空料框接口 地址：{} ,响应信息：{}", momRequestPath.ipPortPath, gson.toJson(body));
            try {
                MomDistributionWaitRequest request = momDistributionWaitRequestService.getMomDistributionWaitRequest(parmsDto, code, orderCode, lineNo, basketType, pointModel, MomTaskType.CALL_MATERIAL);
                request.setProductNo(productNo);
                request.setSourceno(sourceNo);
                momDistributionWaitRequestService.save(request);
            } catch (Throwable e) {
                log.error("请求MOM 空料框请求错误，在保存记录请求记录用于二次任务发送时，保存失败：{}", e.getMessage(), e);
            }
            requestLog.setStatusCode(body.getStatusCode());
            if (MomReqContent.MOM_CODE_OK.equals(code) || MomReqContent.MOM_CALL_WAIT.equals(code)) {
//               请求正常
                requestLog.setResMomCode(true);
                requestLog.setResMsg(body.getMsg());
                Result<String> okok = Result.ok("OKOK");
                resultState = "0";
                return okok;
            } else {
                log.error("订单：{},产线：{} ,请求参数: reqJson：{} ,请求MOM 空料框异常:{}", orderCode, lineNo, reqJson, body);
                requestLog.setResMomCode(false);
                throw new CustomException(body.getMsg());
            }
        } catch (ResourceAccessException throwable) {
            requestLog.setResMsg(throwable.getMessage());
            log.error("订单：{},产线：{} ,请求MOM 空料框请求参数: reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
            requestLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw throwable;
        } catch (RobRequestException throwable) {
            requestLog.setResMsg(throwable.getMessage());
            log.error("订单：{},产线：{} ,请求MOM 空料框 请求参数 reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
            throw throwable;
        } catch (Throwable throwable) {
            requestLog.setResMsg(throwable.getMessage());
            log.error("订单：{},产线：{} ,请求MOM 空料框 请求参数 reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
            requestLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw throwable;
        } finally {
            momDistributionWaitRequestLogService.save(requestLog);
            momCommunicationLogService.addForMQ(MomEnum.CommunicationLogType.IN.val(),
                    MomEnum.CommunicationLog.JIAO_KONG_KUANG.val(),
                    resultState,
                    reqJson,
                    requestLog.getResMsg());
            log.info("MOMAGVServiceImpl [callEmptyBox] 请求空料框 结束 : {} ", deviceType);
        }
    }

    @Override
    public Result<String> exportFullBox(EmptyFrameMovesDzdc frameMoves) {
        DzicsInsideLog insideLog = new DzicsInsideLog();
        insideLog.setBusinessType(MomEnum.CommunicationLog.MAN_LIAO_TUO_CHU.val());
        insideLog.setRequestContent(JSON.toJSONString(frameMoves));
        insideLog.setState("0");
        insideLog.setCreateTime(new Date());
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        insideLog.setOrderId(line.getOrderId());
        insideLog.setOrderNo(line.getOrderNo());
        insideLog.setLineId(line.getId());
        insideLog.setLineNo(line.getLineNo());
        try {
            log.info("MOMAGVServiceImpl [exportFullBox] 请求 移出满料框 请求参数: {} ", frameMoves);
            String deviceType = frameMoves.getDeviceType();
            String pointModel = frameMoves.getPointModel();
            String sourceNo = frameMoves.getExternalCode();
            String palletNo = frameMoves.getPalletNo();
            String lineNo = frameMoves.getLineNo();
            Integer quantity = frameMoves.getQuantity();
            String basketType = frameMoves.getBasketType();
//        List<String> serialNos = frameMoves.getSerialNos();
//        去除序列号中的前缀
//        this.deletePrefix(serialNos);
            MomOrder momOrder = frameMoves.getMomOrder();
            String productNo = momOrder.getProductNo();
            frameMoves.setWiporderno(momOrder.getWiporderno());
            frameMoves.setProTaskOrderId(momOrder.getProTaskOrderId());
            //            空料框回缓存区，或者上下同料点移出满料 清空料框
            if (StringUtils.isEmpty(palletNo)) {
//                查询MOM料框编码
                log.warn("订单：{} 产线：{} 移出满料框 料框编码为空，执行查询MOM料框编码: palletNo :{} ", orderCode, lineNo, palletNo);
                MaterialFrameRes stringPalletType = momBaseService.getStringPalletType(frameMoves.getInnerGroupId(), frameMoves.getGroupId(), orderCode, lineNo, sourceNo, "");
                if (StringUtils.isEmpty(stringPalletType.getPalletNo())) {
                    log.error("订单：{} 产线：{} 移出满料框 查询MOM料框编码不存在: palletNo :{} ", orderCode, lineNo, palletNo);
                    insideLog.setThrowMsg("移出满料框 查询MOM料框编码不存在,palletNo:"+palletNo);
                    insideLog.setState("1");
                    throw new RobRequestException(CustomResponseCode.ERR73);
                }
                palletNo = stringPalletType.getPalletNo();
            }

            MomOrderPath momOrderPath = momOrderPathService.getproTaskOrderId(momOrder.getProTaskOrderId());
            if (ObjectUtils.isEmpty(momOrderPath)) {
                log.error("MOMAGVServiceImpl [callBoxMaterial] momOrderPath:{} ", momOrderPath);
                insideLog.setThrowMsg("当前Mom订单"+momOrder.getWiporderno()+"工序信息不存在，请检查Mom是否下发");
                insideLog.setState("1");
                throw new RobRequestException(CustomResponseCode.ERR69);
            }
            if (quantity <= 0) {
                log.warn("订单：{} 产线：{} ,移出满料框 物料数量为零：{}", orderCode, lineNo, quantity);
            }
//        小车绑定的工位信息
            String stationId = momMaterialPointService.getNextPoint(orderCode, lineNo, basketType);
            if (StringUtils.isEmpty(stationId)) {
                log.error("订单：{},产线：{} 没有工位,小车,绑定的料点信息 MomMaterialPoint :{}", orderCode, lineNo, stationId);
                insideLog.setThrowMsg("当前产线下，小车："+basketType+",工位："+stationId+",绑定信息不存在,请检查配置");
                insideLog.setState("1");
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR81);
            }
            String requireTime = TimeUtil.normalTime();
            AgvParmsDto parmsDto = new AgvParmsDto();
            parmsDto.setReqId(redisUniqueID.getkey());

            Map<String, String> maps = momReqSys.getMaps();
            parmsDto.setReqSys(maps.get(orderCode));
            parmsDto.setFacility(MomReqContent.FACILITY);
            parmsDto.setReqType(MOMAGVReqTypeEnum.OUT_FULL_CONTAINER.val());
            parmsDto.setPalletType(frameMoves.getSanyPalletType());
            parmsDto.setPalletNo(palletNo);
            parmsDto.setSourceNo(sourceNo);
            parmsDto.setRequireTime("");
            parmsDto.setSendTime(requireTime);
            parmsDto.setParamRsrv1("");
            parmsDto.setParamRsrv2("");
            parmsDto.setParamRsrv3("");
            List<MaterialParmsDto> materialLists = new ArrayList<>();
//        Object openNo = redisUtil.get("open:serialnos:" + orderCode);
            MaterialParmsDto materialList = new MaterialParmsDto();
            materialList.setMaterialNo(productNo);
            materialList.setWipOrderNo(frameMoves.getWiporderno());
            materialList.setQuantity(new BigDecimal(quantity));
            materialList.setSerialNo("");
            materialList.setSourceSequenceNo(momOrderPath.getSequenceNo());
            materialList.setSourceOprSequenceNo(momOrderPath.getOprSequenceNo());
            materialLists.add(materialList);
            /*}*/
            parmsDto.setMaterialList(materialLists);
            //头信息
            RequestHeaderVo<AgvParmsDto> requestHeaderVo = new RequestHeaderVo<>();
            requestHeaderVo.setTaskId(redisUniqueID.getUUID());
            requestHeaderVo.setTaskType(MomTaskType.CALL_MATERIAL);
            requestHeaderVo.setVersion(MomVersion.VERSION);
            requestHeaderVo.setReported(parmsDto);
            MomDistributionWaitRequestLog requestLog = momDistributionWaitRequestLogService.getMomDistributionWaitRequestLog(parmsDto);
            Gson gson = new Gson();
            String reqJson = gson.toJson(requestHeaderVo);
            String code = "";
            String resultState = "1";
            try {
                redisUtil.set(ChangShaRedisKey.MOM_HTTP_REQUEST_SERVICE_GET_MY_REQ_TYPE_ID + parmsDto.getReqId(), MyReqMomType.DISTRIBUTION, 24 * 3600);
                log.info("请求MOM 移出满料框 接口地址：{} ,参数：{}", momRequestPath.ipPortPath, reqJson);
                ResultVo body = momBaseService.sendPost(momRequestPath.ipPortPath, reqJson, ResultVo.class);
                code = body.getCode();
                log.info("请求MOM 移出满料框 接口 地址：{} ,响应信息：{}", momRequestPath.ipPortPath, gson.toJson(body));
                requestLog.setStatusCode(body.getStatusCode());
                if (MomReqContent.MOM_CODE_OK.equals(code) || MomReqContent.MOM_CALL_WAIT.equals(code)) {
//               请求正常
                    requestLog.setResMomCode(true);
                    requestLog.setResMsg(body.getMsg());
                    Result<String> okok = Result.ok("OKOK");
                    resultState = "0";
                    return okok;
                } else {
                    log.error("订单：{},产线：{} ,请求参数: reqJson：{} ,移出满料框 响应信息:{}", orderCode, lineNo, reqJson, body);
                    requestLog.setResMomCode(false);
                    throw new CustomException(body.getMsg());
                }
            } catch (ResourceAccessException throwable) {
                requestLog.setResMsg(throwable.getMessage());
                log.error("订单：{},产线：{} ,发送 移出满料框 请求参数: reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
                requestLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                throw throwable;
            } catch (RobRequestException throwable) {
                requestLog.setResMsg(throwable.getMessage());
                log.error("订单：{},产线：{} ,发送 移出满料框 请求参数 reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
                throw throwable;
            } catch (Throwable throwable) {
                requestLog.setResMsg(throwable.getMessage());
                log.error("订单：{},产线：{} ,发送 移出满料框 请求参数 reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
                requestLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                throw throwable;
            } finally {
                try {
                    MomDistributionWaitRequest request = momDistributionWaitRequestService.getMomDistributionWaitRequest(parmsDto, code, orderCode, lineNo, basketType, pointModel, MomTaskType.CALL_MATERIAL);
                    request.setProductNo(productNo);
                    momDistributionWaitRequestService.save(request);
                } catch (Throwable e) {
                    log.error("发送 移出满料框 请求错误，在保存记录请求记录用于二次任务发送时，保存失败：{}", e.getMessage(), e);
                }
                momDistributionWaitRequestLogService.save(requestLog);
                momCommunicationLogService.addForMQ(MomEnum.CommunicationLogType.IN.val(),
                        MomEnum.CommunicationLog.MAN_LIAO_TUO_CHU.val(),
                        resultState,
                        reqJson,
                        requestLog.getResMsg());
                log.info("MOMAGVServiceImpl [exportFullBox]  请求 移出满料框 结束: {} ", deviceType);
            }
        }catch(Throwable throwable){
            throwable.printStackTrace();
        }finally {
            insideLogService.save(insideLog);
        }
        return null;
    }

    @Override
    public Result processDistribution(EmptyFrameMovesDzdc emptyFrameMovesDzdc) {
        String orderNo = emptyFrameMovesDzdc.getOrderCode();
        String lineNo = emptyFrameMovesDzdc.getLineNo();
        emptyFrameMovesDzdc.setGroupId(redisUniqueID.getGroupId());
        emptyFrameMovesDzdc.setInnerGroupId(redisUniqueID.getGroupId());
        emptyFrameMovesDzdc.setDeviceType("机器人");
        Result<String> body = this.callAgv(emptyFrameMovesDzdc);
        log.info("机器人请求 AGV 到单岛 订单:{} ,产线：{} ,请求参数:{},响应参数：{}", orderNo, lineNo, JSONObject.toJSONString(emptyFrameMovesDzdc), JSONObject.toJSONString(body));
        return body;
    }

    @Override
    public Result<String> removeEmptyBox(EmptyFrameMovesDzdc frameMoves) {
        log.info("MOMAGVServiceImpl [removeEmptyBox] 空料框移出  请求参数: {} ", frameMoves);
        String deviceType = frameMoves.getDeviceType();
        String pointModel = frameMoves.getPointModel();
        String sourceNo = frameMoves.getExternalCode();
        String palletNo = frameMoves.getPalletNo();
        String lineNo = frameMoves.getLineNo();
        String basketType = frameMoves.getBasketType();
        String pointType = "";
        //空料框回缓存区，或者上下同料点移出满料 清空料框
        if (StringUtils.isEmpty(palletNo)) {
//                查询MOM料框编码
            log.warn("订单：{} 产线：{} 移出满料框｜拖出空料框 料框编码为空，执行查询MOM料框编码: palletNo :{} ", orderCode, lineNo, palletNo);
            MaterialFrameRes stringPalletType = momBaseService.getStringPalletType(frameMoves.getInnerGroupId(), frameMoves.getGroupId(), orderCode, lineNo, sourceNo, "");
            if (StringUtils.isEmpty(stringPalletType.getPalletNo())) {
                log.error("订单：{} 产线：{} 移出满料框｜拖出空料框 查询MOM料框编码不存在: palletNo :{} ", orderCode, lineNo, palletNo);
                throw new RobRequestException(CustomResponseCode.ERR73);
            }
            palletNo = stringPalletType.getPalletNo();
            pointType = stringPalletType.getPalletType();
        }
//            更新料点信息
        boolean b = momBaseService.updatePointPallet(frameMoves.getInnerGroupId(), frameMoves.getGroupId(), lineNo, orderCode, sourceNo, palletNo);
        String requireTime = TimeUtil.normalTime();
        AgvParmsDto parmsDto = new AgvParmsDto();
        parmsDto.setReqId(redisUniqueID.getkey());

        Map<String, String> maps = momReqSys.getMaps();
        parmsDto.setReqSys(maps.get(orderCode));
        parmsDto.setReqType(MOMAGVReqTypeEnum.OUT_EMPTY_CONTAINER.val());
        parmsDto.setFacility(MomReqContent.FACILITY);
        parmsDto.setPalletType(pointType);
        parmsDto.setPalletNo(palletNo);
        parmsDto.setSourceNo(sourceNo);
        parmsDto.setRequireTime("");
        parmsDto.setSendTime(requireTime);
        parmsDto.setParamRsrv1("");
        parmsDto.setParamRsrv2("");
        parmsDto.setParamRsrv3("");
        parmsDto.setMaterialList(new ArrayList<>());
        //头信息
        RequestHeaderVo<AgvParmsDto> requestHeaderVo = new RequestHeaderVo<>();
        requestHeaderVo.setTaskId(redisUniqueID.getUUID());
        requestHeaderVo.setTaskType(MomTaskType.CALL_MATERIAL);
        requestHeaderVo.setVersion(MomVersion.VERSION);
        requestHeaderVo.setReported(parmsDto);
        MomDistributionWaitRequestLog requestLog = momDistributionWaitRequestLogService.getMomDistributionWaitRequestLog(parmsDto);
        Gson gson = new Gson();
        String reqJson = gson.toJson(requestHeaderVo);
        String resultState = "1";
        try {
            redisUtil.set("momHttpRequestService:getMyReqTypeId:" + parmsDto.getReqId(), MyReqMomType.DISTRIBUTION, 24 * 3600);
            log.info("请求MOM 空料框移出 接口地址：{} ,参数：{}", momRequestPath.ipPortPath, reqJson);
            ResultVo body = momBaseService.sendPost(momRequestPath.ipPortPath, reqJson, ResultVo.class);
            log.info("请求MOM 空料框移出 接口地址：{} ,响应信息：{}", momRequestPath.ipPortPath, gson.toJson(body));
            String code = body.getCode();
            try {
                MomDistributionWaitRequest request = momDistributionWaitRequestService.getMomDistributionWaitRequest(parmsDto, code, orderCode, lineNo, basketType, pointModel, MomTaskType.CALL_MATERIAL);
                momDistributionWaitRequestService.save(request);
            } catch (Throwable e) {
                log.error("发送 空料框移出 请求错误，在保存记录请求记录用于二次任务发送时，保存失败：{}", e.getMessage(), e);
            }
            requestLog.setStatusCode(body.getStatusCode());
            if (MomReqContent.MOM_CODE_OK.equals(code) || MomReqContent.MOM_CALL_WAIT.equals(code)) {
//               请求正常
                requestLog.setResMomCode(true);
                requestLog.setResMsg(body.getMsg());
                resultState = "0";
                return Result.ok("OKOK");
            } else {
                log.error("订单：{},产线：{} ,请求参数: reqJson：{} ,空料框移出异常:{}", orderCode, lineNo, reqJson, body);
                requestLog.setResMomCode(false);
                throw new CustomException(body.getMsg());
            }
        } catch (ResourceAccessException throwable) {
            requestLog.setResMsg(throwable.getMessage());
            log.error("订单：{},产线：{} ,发送 空料框移出请求参数: reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
            requestLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw throwable;
        } catch (RobRequestException throwable) {
            requestLog.setResMsg(throwable.getMessage());
            log.error("订单：{},产线：{} ,发送 空料框移出 请求参数 reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
            throw throwable;
        } catch (Throwable throwable) {
            requestLog.setResMsg(throwable.getMessage());
            log.error("订单：{},产线：{} ,发送 空料框移出 请求参数 reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
            requestLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw throwable;
        } finally {
            momDistributionWaitRequestLogService.save(requestLog);
            momCommunicationLogService.addForMQ(MomEnum.CommunicationLogType.IN.val(),
                    MomEnum.CommunicationLog.KONG_KUANG_TUO_CHU.val(),
                    resultState,
                    reqJson,
                    requestLog.getResMsg());
            log.info("请求空料框移出 结束 : {} ", deviceType);
        }
    }

    @Override
    public ResultDto handshakeHandle(IssueOrderInformation<MOMAGVHandshakeDto> information) {
        MOMAGVHandshakeDto task = information.getTask();
        if (MOMAGVHandshakeWaitTypeEnum.IN.val().equals(task.getWaitType())) {
            redisUtil.set(ChangShaRedisKey.MOM_AVG_HANDSHAKE, JSONUtil.toJsonStr(information), (long) 12 * 60 * 60);
            log.info("MOMAGVServiceImpl [handshakeHandle] redis{}", redisUtil.get(ChangShaRedisKey.MOM_AVG_HANDSHAKE));
        }
        if (MOMAGVHandshakeWaitTypeEnum.OK.val().equals(task.getWaitType())) {
            redisUtil.del(ChangShaRedisKey.MOM_AVG_HANDSHAKE);
        }
        rabbitMQService.sendMsg(AGVConfig.TOPIC_EXCHANGE, AGVConfig.HANDSHAKE_ROUTING, String.valueOf(System.currentTimeMillis()));
        return ResultDto.ok(information.getVersion(), information.getTaskId());
    }

    @Override
    public Map<String, Object> handshakeHandle(String state) {
        Map<String, Object> result = new HashMap<>();
        result.put("return_code", 0);
        result.put("return_desc", "");
        if (Agv2rasterController.Status.IN_REQ.val().equals(state)) {
            boolean ing = redisUtil.hasKey(ChangShaRedisKey.MOM_AVG_HANDSHAKE_ING);
            log.info("MOMAGVServiceImpl [handshakeResult] ing{}", ing);
            if (ing) {
                result.put("return_code", 400);
            } else {
                redisUtil.set(ChangShaRedisKey.MOM_AVG_HANDSHAKE, " ", (long) 12 * 60 * 60);
            }
        }
        if (Agv2rasterController.Status.OUT_COMPLETE.val().equals(state)) {
            redisUtil.del(ChangShaRedisKey.MOM_AVG_HANDSHAKE);
        }
        return result;
    }

    @Override
    public void handshakeOk() {
        log.info("MOMAGVServiceImpl [handshakeOk]");
        Object o = redisUtil.get(ChangShaRedisKey.MOM_AVG_HANDSHAKE);
        if (ObjectUtils.isEmpty(o)) {
            log.info("MOMAGVServiceImpl [handshakeOk] o is null");
            return;
        }
        IssueOrderInformation<MOMAGVHandshakeDto> information = JSON.parseObject(o.toString(), new TypeReference<IssueOrderInformation<MOMAGVHandshakeDto>>() {
        });
        MOMAGVHandshakeDto task = information.getTask();
        if (MOMAGVHandshakeWaitTypeEnum.IN.val().equals(task.getWaitType())) {
            task.setResult(MOMAGVHandshakeResultEnum.IN.val());
        }
        if (MOMAGVHandshakeWaitTypeEnum.OK.val().equals(task.getWaitType())) {
            task.setResult(MOMAGVHandshakeResultEnum.OK.val());
        }
        RequestHeaderVo<MOMAGVHandshakeDto> requestHeaderVo = new RequestHeaderVo<>();
        requestHeaderVo.setTaskId(information.getTaskId());
        requestHeaderVo.setTaskType(information.getTaskType());
        requestHeaderVo.setVersion(information.getVersion());
        requestHeaderVo.setReported(task);
        String jsonStr = JSONUtil.toJsonStr(requestHeaderVo);
        log.info("MOMAGVServiceImpl [handshakeResult] AGV握手请求MOM参数：{}", jsonStr);
        ResultVo body = null;
        try {
            body = momBaseService.sendPost(momRequestPath.ipPortPath, jsonStr, ResultVo.class);
        } catch (Exception e) {
            log.info("MOMAGVServiceImpl [handshakeResult] AGV握手请求失败e：{}", e.getMessage());
            return;
        }
        log.info("MOMAGVServiceImpl [handshakeResult] AGV握手MOM响应参数：{}", JSONUtil.toJsonStr(body));
        String code = body.getCode();
        if (MomReqContent.MOM_CODE_OK.equals(code) || MomReqContent.MOM_CALL_WAIT.equals(code)) {
            log.info("MOMAGVServiceImpl [handshakeResult] AGV握手反馈成功");
        } else {
            log.info("MOMAGVServiceImpl [handshakeResult] AGV握手反馈失败{}", JSONUtil.toJsonStr(body));
            throw new RobRequestException(body.getMsg());
        }
    }

    @Override
    public void handshakeResult() {
        while (true) {
            boolean ing = redisUtil.hasKey(ChangShaRedisKey.MOM_AVG_HANDSHAKE_ING);
            log.info("MOMAGVServiceImpl [handshakeResult] ing{}", ing);
            if (ing) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    log.error("MOMAGVServiceImpl [handshakeResult]" + e.toString());
                }
            } else {
                log.info("MOMAGVServiceImpl [handshakeResult] else");
                this.handshakeOk();
                return;
            }
        }
    }

    @Override
    public String handshakeTcpHandle(String str) {
        String[] split = str.split(";");
        String code = split[1];

        String resultCode = " ";
        if ("X".equals(code)) {
            boolean ing = redisUtil.hasKey(ChangShaRedisKey.MOM_AVG_HANDSHAKE);
            if (ing) {
                resultCode = "1";
            } else {
                resultCode = "0";
            }
        }
        if ("0".equals(code)) {
            redisUtil.del(ChangShaRedisKey.MOM_AVG_HANDSHAKE_ING);
        }
        if ("1".equals(code)) {
            redisUtil.set(ChangShaRedisKey.MOM_AVG_HANDSHAKE_ING, " ");
        }
        return "##R003;" + resultCode + ";@#";
    }



    @Override
    public void packingComplete(List<MomQrCodeVo>serialNos, String ContainerListNo,String pointModel,String point) {
        if(CollectionUtils.isEmpty(serialNos) || StringUtils.isEmpty(ContainerListNo)){
            log.error("MOMAGVServiceImpl [packingComplete] 中控-->Mom 发送装框完成信息失败，物料信息：{}，料框编号:{}",serialNos.toString(),ContainerListNo);
            return;
        }
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        Map<String, String> maps = momReqSys.getMaps();
        //装箱参数定义
        MomPackingVo packingVo = new MomPackingVo();
        packingVo.setReqId(redisUniqueID.getkey());
        packingVo.setReqSys(maps.get(orderCode));
        packingVo.setFacility(MomReqContent.FACILITY);

        //设备编号，定死
        packingVo.setDeviceID("12306");

        packingVo.setContainerListNo(ContainerListNo);
        packingVo.setActualCompleteDate(dateUtil.dateFormatToStingYmdHms(new Date()));
        packingVo.setProgressType("1");
        //数量根据料点模式判断
        if("NG".equals(pointModel)){
            packingVo.setNGQuantity(String.valueOf(serialNos.size()));
            packingVo.setQuantity(String.valueOf(0));
        }else{
            packingVo.setNGQuantity(String.valueOf(0));
            packingVo.setQuantity(String.valueOf(serialNos.size()));
        }
        //系统中的工位建模编码
        if(MomConstant.ORDER_DZ_1972.equals(line.getOrderNo())){
            packingVo.setStationNo("240");
        }
        if(MomConstant.ORDER_DZ_1973.equals(line.getOrderNo())){
            packingVo.setStationNo("245");
        }

        List<String> collect = serialNos.stream().map(s -> s.getWorkOrderNo()).collect(Collectors.toList());
        List<String> MomOrders = new ArrayList<>();
        for (String s : collect) {
            if(MomOrders.contains(s)){
                continue;
            }
            MomOrders.add(s);
        }
        List<MomQrCodeVo>list=new ArrayList<>();
        for (String momOrder : MomOrders) {
            Integer fieldNum = listUtil.getFieldNum(collect, momOrder);
            MomQrCodeVo momQrCodeVo = new MomQrCodeVo();
            momQrCodeVo.setSerialNo("");
            momQrCodeVo.setSequenceNo("");
            momQrCodeVo.setParamRsrv1("");
            momQrCodeVo.setParamRsrv2("");
            momQrCodeVo.setParamRsrv3("");
            momQrCodeVo.setWorkOrderNo(momOrder);
            momQrCodeVo.setOrderQty(String.valueOf(fieldNum));
            list.add(momQrCodeVo);
        }
        //填充序列号信息
        packingVo.setSnList(list);
        MomReportHeader<MomPackingVo>header = new MomReportHeader<>();
        header.setVersion(MomVersion.VERSION);
        header.setTaskId(redisUniqueID.getUUID());
        header.setTaskType(MomTaskType.MOM_Packing);
        header.setReported(packingVo);
        Gson gson = new Gson();
        String json = gson.toJson(header);
        String code = "";
        ResultVo body = new ResultVo();
        try{
            log.info("MOMAGVServiceImpl [exportFullBox] 请求MOM 装框完成反馈 接口地址：{} ,参数：{}", momRequestPath.ipPortPath,json);
            body = momBaseService.sendPost(momRequestPath.ipPortPath,json,ResultVo.class);
            log.info("MOMAGVServiceImpl [exportFullBox] 请求MOM 装框完成反馈 接口地址：{} ,响应信息：{}", momRequestPath.ipPortPath, gson.toJson(body));
            code = body.getCode();
        }catch(Throwable throwable){
            code = "-1";
            throwable.printStackTrace();
        }finally {
            //无论成功还是失败，保留日志通讯记录
            MomCommunicationLog communicationLog = new MomCommunicationLog();
            communicationLog.setType(MomEnum.CommunicationLogType.OUT.val());
            communicationLog.setBusinessType(MomTaskType.MOM_Packing);
            communicationLog.setResultState(code);
            communicationLog.setRequestContent(json);
            communicationLog.setResponseContent(gson.toJson(body));
            communicationLog.setCreateTime(new Date());
            momCommunicationLogService.save(communicationLog);
            redisUtil.del(orderCode + "##R004" + point + "copy");
        }

    }

}
