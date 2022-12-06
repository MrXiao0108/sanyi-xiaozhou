package com.dzics.data.appoint.changsha.mom.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dzics.data.appoint.changsha.mom.config.MomRequestPath;
import com.dzics.data.appoint.changsha.mom.config.param.MOMReqSys;
import com.dzics.data.appoint.changsha.mom.db.dao.MomOrderDao;
import com.dzics.data.appoint.changsha.mom.db.dao.MomOrderPathDao;
import com.dzics.data.appoint.changsha.mom.db.dao.MomWaitWorkReportDao;
import com.dzics.data.appoint.changsha.mom.enums.MomEnum;
import com.dzics.data.appoint.changsha.mom.model.constant.MomProgressType;
import com.dzics.data.appoint.changsha.mom.model.constant.MomReqContent;
import com.dzics.data.appoint.changsha.mom.model.constant.MomVersion;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrderPath;
import com.dzics.data.appoint.changsha.mom.model.entity.MomWaitWorkReport;
import com.dzics.data.appoint.changsha.mom.model.vo.MomReportHeader;
import com.dzics.data.appoint.changsha.mom.model.vo.ResultVo;
import com.dzics.data.appoint.changsha.mom.model.vo.WorkReportVo;
import com.dzics.data.appoint.changsha.mom.service.*;
import com.dzics.data.appoint.changsha.mom.service.impl.sendhttp.HttpWorkReportImpl;
import com.dzics.data.appoint.changsha.mom.util.MomTaskType;
import com.dzics.data.appoint.changsha.mom.util.RedisUniqueID;
import com.dzics.data.common.base.model.constant.QrCode;
import com.dzics.data.common.base.model.dto.position.SendPosition;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.entity.DzWorkStationManagement;
import com.dzics.data.pub.service.workreporting.WorkReportingService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Classname WorkReportingServiceImpl
 * @Description 销轴粗加工MOM 报工
 * @Date 2022/6/16 17:13
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class WorkReportingCjServiceImpl implements WorkReportingService<Boolean, SendPosition> {

    @Autowired
    private CachingApi cachingApi;
    @Autowired
    private MomOrderDao momOrderDao;
    @Autowired
    private RedisUniqueID redisUniqueID;
    @Autowired
    private MomOrderPathDao pathDao;
    @Autowired
    private HttpWorkReportImpl workReportImpl;
    @Autowired
    private MomRequestPath momRequestPath;
    @Autowired
    private MomWaitWorkReportDao reportDao;
    @Autowired
    private MomWaitWorkReportService momWaitWorkReportService;
    @Autowired
    private MOMReqSys momReqSys;
    @Value("${order.code}")
    private String orderCode;
    @Autowired
    private MomCommunicationLogService momCommunicationLogService;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private ManageModeService manageModeService;

//    TODO 此方法，报工订单会存在漏报情况，待调整
    /**
     * @param parms 报工数据
     * @return 报工结果
     * @Description 销轴抛光 MOM 报工
     */
    @Override
    public Boolean sendWorkReportingData(SendPosition parms, String momWaitWorkReportId) {
        DzWorkStationManagement stationManagement = JSONObject.parseObject(parms.getStationJsonA(), DzWorkStationManagement.class);
        String stationCode = stationManagement.getStationCode();
        //TODO 部署测试时和机器人确定该工位编号
        List<String> list = Arrays.asList("25");
        if (list.contains(stationCode)) {
//        如果需要报工找当前在做的订单
            DzProductionLine line = cachingApi.getOrderIdAndLineId();
            MomOrder momOrder = new MomOrder();
            if (StringUtils.hasText(momWaitWorkReportId)) {
                MomWaitWorkReport momWaitWorkReport = momWaitWorkReportService.getById(momWaitWorkReportId);
                momOrder = momOrderService.getById(momWaitWorkReport.getOrderId());
            } else {
                momOrder = momOrderDao.getOrderPro(line.getOrderId(), line.getId());
            }
            if (momOrder == null) {
                log.error("当前不存在生产中的订单:{}", JSONObject.toJSONString(line));
                return false;
            }
            log.info("WorkReportingCjServiceImpl [sendWorkReportingData] wiporderno{}", momOrder.getWiporderno());
//        封装参数 发送报工请求
            String b = WorkReport(momOrder,
                    parms.getProductionTime(),
                    parms.getOutInputType(),
                    parms.getQrCode(),
                    stationManagement.getDzStationCode());
            if ("success".equals(b)) {
                return true;
            } else {
                MomWaitWorkReport report = new MomWaitWorkReport();
                if (StringUtils.hasText(momWaitWorkReportId)) {
                    report = momWaitWorkReportService.getById(momWaitWorkReportId);
                    report.setMomResponse(b);
                    momWaitWorkReportService.updateById(report);
                } else {
                    BeanUtils.copyProperties(parms, report);
                    report.setMomResponse(b);
                    int insert = reportDao.insert(report);
                }
                log.info("WorkReportingCjServiceImpl [sendWorkReportingData] 报工记录变动:{}", JSON.toJSONString(report));
            }
        }
//        else {
//            log.warn("当前工位:{}不需要报工", stationCode);
//        }
        return false;
    }

    public String WorkReport(MomOrder momOrder, Date productionTime, String outInputType, String qrCode, String baoGongBianMa) {
        MomOrderPath path = pathDao.getProTaskOrderPath(momOrder.getProTaskOrderId());
        MomReportHeader<WorkReportVo> report = new MomReportHeader<>();
        WorkReportVo vo = new WorkReportVo();
        vo.setReqId(redisUniqueID.getkey());

        Map<String, String> maps = momReqSys.getMaps();
        String reqSys = maps.get(orderCode);
        vo.setReqSys(reqSys);
        vo.setFacility(MomReqContent.FACILITY);
        vo.setWipOrderNo(momOrder.getWiporderno());
        vo.setSequenceNo(path.getSequenceNo());
        vo.setOprSequenceNo(path.getOprSequenceNo());
        String TimeStr = DateUtil.dateFormatToStingYmdHms(productionTime);
        if (outInputType.equals(QrCode.QR_CODE_OUT)) {
            vo.setActualCompleteDate(TimeStr);
            vo.setActualStartDate("");
        } else {
            vo.setActualStartDate(TimeStr);
            vo.setActualCompleteDate("");
        }
        vo.setProgressType(outInputType.equals(QrCode.QR_CODE_IN) ?
                MomProgressType.START.getCode() : MomProgressType.COMPLETE.getCode());
        vo.setQuantity(BigDecimal.valueOf(1));
        vo.setNGQuantity(BigDecimal.valueOf(0));
        vo.setEmployeeNo("");
        vo.setProductNo(momOrder.getProductNo());
        boolean workNo = manageModeService.workNo();
        if (workNo) {
            vo.setSerialNo(MomProgressType.START.getCode().equals(vo.getProgressType()) ? "" : qrCode);
        } else {
            vo.setSerialNo("");
        }
        vo.setParamRsrv1("");
        vo.setParamRsrv2(baoGongBianMa);
        vo.setParamRsrv3("");
        vo.setParamRsrv4("");
        vo.setParamRsrv5("");
        vo.setKeyaccessoryList(new ArrayList<>());
        report.setReported(vo);
        report.setVersion(MomVersion.VERSION);
        report.setTaskType(MomTaskType.MOM_ORDER_REPORT);
        report.setTaskId(redisUniqueID.getUUID());
        Gson gson = new Gson();
        String reqJson = gson.toJson(report);
        log.info("向总控发送报工请求 reqJson:{}", reqJson);
        ResultVo body = null;
        String result = "";
        String resultState = "1";
        try {
            body = workReportImpl.sendPost(null, null, null, null, momRequestPath.ipPortPath, reqJson, ResultVo.class);
        } catch (Exception e) {
            return "报工异常: MOM生产进度反馈接口请求失败。";
        }
        log.info("向总控发送报工请求返回 body:{}", JSONObject.toJSONString(body));
        if (body != null && body.getCode() != null && body.getCode().equals(MomReqContent.MOM_CODE_OK)) {
            resultState = "0";
            result = "success";
        } else {
            result = JSON.toJSONString(body);
        }
        momCommunicationLogService.addForMQ(MomEnum.CommunicationLogType.IN.val(),
                MomEnum.CommunicationLog.BAO_GONG.val(),
                resultState,
                reqJson,
                JSONUtil.toJsonStr(body));
        return result;
    }
}
