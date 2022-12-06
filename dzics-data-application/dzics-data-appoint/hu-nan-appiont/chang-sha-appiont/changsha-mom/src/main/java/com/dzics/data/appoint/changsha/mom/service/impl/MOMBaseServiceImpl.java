package com.dzics.data.appoint.changsha.mom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.appoint.changsha.mom.config.MapConfig;
import com.dzics.data.appoint.changsha.mom.config.MomRequestPath;
import com.dzics.data.appoint.changsha.mom.config.param.MOMReqSys;
import com.dzics.data.appoint.changsha.mom.model.constant.MomReqContent;
import com.dzics.data.appoint.changsha.mom.model.constant.MomVersion;
import com.dzics.data.appoint.changsha.mom.model.dto.*;
import com.dzics.data.appoint.changsha.mom.model.dto.agv.MomResultSearch;
import com.dzics.data.appoint.changsha.mom.model.dto.agv.SearchNoRes;
import com.dzics.data.appoint.changsha.mom.model.vo.ResultVo;
import com.dzics.data.appoint.changsha.mom.service.MOMBaseService;
import com.dzics.data.appoint.changsha.mom.util.MomTaskType;
import com.dzics.data.appoint.changsha.mom.util.RedisUniqueID;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.RobRequestException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.vo.Result;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author: van
 * @since: 2022-07-07
 */
@Slf4j
@Service
public class MOMBaseServiceImpl implements MOMBaseService {

    @Autowired
    private RedisUniqueID redisUniqueID;
    @Autowired
    private MomRequestPath momRequestPath;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MapConfig mapConfig;
    @Autowired
    private MOMReqSys momReqSys;
    @Value("${order.code}")
    private String orderCode;

    @Override
    public MaterialFrameRes getStringPalletType(String innerGroupId, String groupId, String orderNo, String lineNo, String sourceNo, String paramRsrv1) {
        RequestHeaderVo<MaterialFrame> requestHeaderVo = new RequestHeaderVo<>();
        requestHeaderVo.setTaskId(redisUniqueID.getUUID());
        requestHeaderVo.setTaskType(MomTaskType.QUERY_MATERIAL);
        requestHeaderVo.setVersion(MomVersion.VERSION);
        MaterialFrame materialFrame = new MaterialFrame();

        Map<String, String> maps = momReqSys.getMaps();
        materialFrame.setReqSys(maps.get(orderCode));
        materialFrame.setFacility(MomReqContent.FACILITY);
        materialFrame.setPointNo(sourceNo);
        materialFrame.setParamRsrv1(paramRsrv1);
        requestHeaderVo.setReported(materialFrame);
        Gson gsonSearch = new Gson();
        String reqJson = gsonSearch.toJson(requestHeaderVo);
        log.info("请求MOM 查询料框参数接口 地址：{} ,参数：{}", momRequestPath.ipPortPath, reqJson);
        MomResult body = this.sendPost(momRequestPath.ipPortPath, reqJson, MomResult.class);
        log.info("请求MOM 查询料框参数接口 地址：{}  , 返回信息：{}", momRequestPath.ipPortPath, JSONObject.toJSONString(body));
        if (body == null) {
            throw new RobRequestException(CustomResponseCode.ERR60);
        }
        if (!MomReqContent.MOM_CODE_OK.equals(body.getCode())) {
            log.error("请求MOM查询料框接口错误 body: {}", body);
            throw new RobRequestException(body.getMsg());
        }
        MaterialFrameRes returnData = body.getReturnData();
        if (returnData == null) {
            log.error("返回结果集 returnData 为空 body :{}", body);
            throw new RobRequestException(CustomResponseCode.ERR61);
        }
        String palletType = returnData.getPalletType();
        if (StringUtils.isEmpty(palletType)) {
            log.error("查询料框接口：MOM返回料框类型为空：body ：{}", body);
            throw new RobRequestException(CustomResponseCode.ERR62);
        }
        return returnData;
    }

    @Override
    public MomResultSearch getSanyMomNextSpecNo(SearchDzdcMomSeqenceNo dzdcMomSeqenceNo) {
        String url = "";
        String orderNo = dzdcMomSeqenceNo.getOrderCode();
        String lineNo = dzdcMomSeqenceNo.getLineNo();
        try {
            Map<String, String> mapIps = mapConfig.getMaps();
            String plcIp = mapIps.get(orderCode);
            if (CollectionUtils.isNotEmpty(mapIps) && !org.springframework.util.StringUtils.isEmpty(plcIp)) {
                Result body = this.searechOprSequenceNo(dzdcMomSeqenceNo);
                int code = body.getCode();
                if (code == 0) {
                    MomResultSearch momResultSearch = JSONObject.parseObject(JSONObject.toJSONString(body.getData()), MomResultSearch.class);
                    return momResultSearch;
                }
                log.info("请求 MOM 获取下个工序号 到单岛 订单:{} ,产线：{} , URL：{},请求参数:{},响应参数：{}", orderNo, lineNo, url, JSONObject.toJSONString(dzdcMomSeqenceNo), JSONObject.toJSONString(body));
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR80);
            } else {
                log.error("机器人请求单岛 IP 配置不存在orderNo : {}, lineNo: {} , mapIps: {}", orderNo, lineNo, mapIps);
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
            }
        } catch (Throwable throwable) {
            log.error("机器人请求单岛 订单:{} ,产线：{} , 到单岛 URL：{},请求参数:{},错误信息：{}", orderNo, lineNo, url, JSONObject.toJSONString(dzdcMomSeqenceNo), throwable.getMessage(), throwable);
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR80);
        }
    }

    @Override
    public boolean updatePointPallet(String innerGroupId, String groupId, String lineNo, String orderCode, String externalCode, String palletNo) {
        PutFeedingPoint putFeedingPoint = new PutFeedingPoint();

        Map<String, String> maps = momReqSys.getMaps();
        putFeedingPoint.setReqSys(maps.get(orderCode));
        putFeedingPoint.setFacility(MomReqContent.FACILITY);
        putFeedingPoint.setPalletNo(palletNo);
        putFeedingPoint.setSourceNo(externalCode);
        putFeedingPoint.setPointState("1");
        putFeedingPoint.setPalletState("0");
        putFeedingPoint.setPointFlag("0");
        RequestHeaderVo headerVo = new RequestHeaderVo();
        headerVo.setTaskId(redisUniqueID.getUUID());
        headerVo.setTaskType(MomTaskType.UPDATE_FEEDING_POINT_INFORMATION);
        headerVo.setVersion(MomVersion.VERSION);
        headerVo.setReported(putFeedingPoint);
        Gson gson = new Gson();
        String reqJson = gson.toJson(headerVo);
        log.info("订单：{},产线：{},请求MOM更新 料点状态 | 料框状态 接口 地址：{} ,参数：{}", orderCode, lineNo, momRequestPath.ipPortPath, reqJson);
        ResultVo body = this.sendPost(momRequestPath.ipPortPath, reqJson, ResultVo.class);
        log.info("订单：{},产线：{},请求MOM更新 料点状态 | 料框状态 接口 地址：{} ,响应信息：{}", orderCode, lineNo, momRequestPath.ipPortPath, gson.toJson(body));
        if (body == null || !MomReqContent.MOM_CODE_OK.equals(body.getCode())) {
            log.error("订单：{},产线：{} 调用MOM更新投料点信息异常 resBody:{}", orderCode, lineNo, body);
            throw new RobRequestException(CustomResponseCode.ERR75);
        }
        return true;
    }

    /**
     * 发送请求
     * 查询料框参数接口
     */
    @Override
    public <T> T sendPost(String url, String reqJson, Class<T> responseType) {
        try {
            ResponseEntity<T> forEntity = restTemplate.postForEntity(url, reqJson, responseType);
            return forEntity.getBody();
        } catch (Throwable throwable) {
            log.error("MOMBaseServiceImpl [sendPost]：{}", throwable.getMessage());
            ResponseEntity<Object> objectResponseEntity = restTemplate.postForEntity(url, reqJson, Object.class);
            log.info("xxx:{}", objectResponseEntity.getBody());
            return null;
        }
    }

    @Override
    public Result searechOprSequenceNo(SearchDzdcMomSeqenceNo momSeqenceNo) {
        SearchNo searchNo = new SearchNo();
        searchNo.setReqSys(MomReqContent.REQ_SYS);
        searchNo.setFacility(MomReqContent.FACILITY);
        searchNo.setWipOrderNo(momSeqenceNo.getWipOrderNo());
        searchNo.setSequenceNo(MomReqContent.SEQUENCENO);
        searchNo.setOprSequenceNo(momSeqenceNo.getOprSequenceNo());
        SeacrhHeader seacrhHeader = new SeacrhHeader();
        seacrhHeader.setTaskType(MomTaskType.nextOprSeqNo);
        seacrhHeader.setReported(searchNo);
        seacrhHeader.setVersion(MomVersion.VERSION);
        seacrhHeader.setTaskId(redisUniqueID.getUUID());
        Gson gson = new Gson();
        String reqJson = gson.toJson(seacrhHeader);
        ResponseEntity<MomResultSearch> entity = restTemplate.postForEntity(momRequestPath.ipPortPath, reqJson, MomResultSearch.class);
        MomResultSearch body = entity.getBody();
        log.info("请求MOM 下个工序工序号 地址：{}  ,请求参数:{}, 返回信息：{}", momRequestPath.ipPortPath, reqJson, gson.toJson(body));
        if (body == null) {
            throw new RobRequestException(CustomResponseCode.ERR60);
        }
        if (!MomReqContent.MOM_CODE_OK.equals(body.getCode())) {
            log.error("请求MOM查询料框接口错误 body: {}", body);
            throw new RobRequestException(body.getMsg());
        }
        SearchNoRes noRes = body.getReturnData();
        if (noRes == null) {
            log.error("返回下个工序号 returnData 为空 body :{}", body);
            throw new RobRequestException(CustomResponseCode.ERR61);
        }
        String nextOprSeqNo = noRes.getNextOprSeqNo();
        if (org.springframework.util.StringUtils.isEmpty(nextOprSeqNo)) {
            log.error("查询下个工序号：MOM返回下个工序号为空：body ：{}", body);
            throw new RobRequestException(CustomResponseCode.ERR62);
        }

        return Result.ok(body);
    }
}
