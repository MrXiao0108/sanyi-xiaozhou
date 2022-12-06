package com.dzics.data.acquisition.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.common.base.enums.CmdStateClassification;
import com.dzics.data.common.base.model.custom.DzTcpDateID;
import com.dzics.data.common.base.model.dao.WorkNumberName;
import com.dzics.data.common.base.model.dto.CmdTcp;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.acquisition.service.AccWorkpieceDataService;
import com.dzics.data.common.base.model.constant.ProductDefault;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.pdm.db.dao.DzWorkpieceDataDao;
import com.dzics.data.pdm.model.entity.DzWorkpieceData;
import com.dzics.data.pms.service.DzProductService;
import com.dzics.data.pub.util.TcpStringUtil;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AccWorkpieceDataServiceImpl implements AccWorkpieceDataService {

    @Autowired
    private TcpStringUtil tcpStringUtil;
    @Autowired
    private DzProductService dzProductService;
    @Autowired
    private DzWorkpieceDataDao workpieceDataDao;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * {"MessageId":"5541cab4-a440-497c-b048-f80929f84251","QueueName":"dzics-dev-gather-v1-checkout-equipment","ClientId":"DZROBOT","OrderCode":"DZ-1875","LineNo":"1","DeviceType":"3","DeviceCode":"01","Message":"A809|[9f709fea3d994f9c8ea0154ee00c4f65,1017,4,A1,0,5,8952.071,0,7415.005,0,3467.137,0,68.565,1,9999.999,0]","Timestamp":"2022-01-05 10:12:37.4722"}
     * {"MessageId":"5541cab4-a440-497c-b048-f80929f84251","QueueName":"dzics-dev-gather-v1-checkout-equipment","ClientId":"DZROBOT","OrderCode":"DZ-1882","LineNo":"1","DeviceType":"2","DeviceCode":"01","Message":"A809|[9f709fea3d994f9c8ea0154ee00c4f65,1030,4,0,4,8952.071,0,7415.005,0,3467.137,0,68.565,1]","Timestamp":"2021-02-08 17:21:37.4722"}
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * {"MessageId":"5541cab4-a440-497c-b048-f80929f84251","QueueName":"dzics-dev-gather-v1-checkout-equipment",
     * "ClientId":"DZROBOT","OrderCode":"DZ-1882","LineNo":"1","DeviceType":"2","DeviceCode":"01",
     * "Message":"A809|[9f709fea3d994f9c8ea0154ee00c4f65,
     * 1030,
     * 4,
     * 0
     * ,4
     * ,8952.071,0,
     * 7415.005,0,
     * 3467.137,0,
     * 68.565,1]",
     * "Timestamp":"2021-02-08 17:21:37.4722"}
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * 产品条码，产品ID，工位编号，机床编号，总状态(0=NG,1=OK,100)，检测项数量，检测值1，检测1状态，检测值2，检测2状态，检测值3，检测3状态，检测值4，检测4状态
     * A809|[4c1e55c02ca04480975b9b0d4bb4a79f,1030,2,0,4,5835.125,1,6922.305,1,1744.856,0,8869.579,0]
     *
     * @param cmd
     */
    @Override
    public synchronized DzWorkpieceData handleCheckout(RabbitmqMessage cmd) {
        log.info("AccWorkpieceDataServiceImpl [handleCheckout] 检测设备数据：{}", JSONObject.toJSONString(cmd));
        Map<String, Object> map = tcpStringUtil.analysisCmdV2(cmd);
        if (CollectionUtils.isNotEmpty(map)) {
            // 底层设备上传时间时间
            Long senDate = (Long) map.get(CmdStateClassification.DATA_STATE_TIME.getCode());
            // 分类唯一属性值
            DzTcpDateID tcpDateId = (DzTcpDateID) map.get(CmdStateClassification.TCP_ID.getCode());
            if (tcpDateId == null) {
                log.error("分类唯一属性值不存在：DzTcpDateID：{}", tcpDateId);
                return null;
            }
            String lineNum = tcpDateId.getProductionLineNumber();
            String deviceType = tcpDateId.getDeviceType();
            String deviceNum = tcpDateId.getDeviceNumber();
            String orderNumber = tcpDateId.getOrderNumber();
            String lineType = getLintType(orderNumber);
            List<CmdTcp> cmdTcps = (List<CmdTcp>) map.get(CmdStateClassification.TCP_CHECK_EQMENT.getCode());
            if (CollectionUtils.isNotEmpty(cmdTcps)) {
                CmdTcp cmdTcp = cmdTcps.get(0);
                String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(cmdTcp.getDeviceItemValue(), "[]");
                String tcpValue = cmdTcp.getTcpValue();
                if (!StringUtils.isEmpty(deviceItemValue) && !StringUtils.isEmpty(tcpValue)) {
                    String[] split = deviceItemValue.split(",");
                    if (split.length > 6) {
                        String producBarcodeX = split[0];
                        String name = "";
                        String productNoSave = "";
                        String producBarcode = producBarcodeX;
                        String s1 = split[1];
                        String productNo = StringUtils.isEmpty(s1) ? ProductDefault.modelNumber : s1; // 之前是产品编号 1030 三一需求变更，是订单号
                        redisUtil.set(RedisKey.CHECK_PRODUCT + orderNumber + lineNum, productNo);
                        String workNumber = split[2];
                        String machineNumber = split[3];
                        Integer allState = Integer.valueOf(split[4]);
                        Integer itemNumber = Integer.valueOf(split[5]);
                        Date dateCj = new Date(senDate);
                        LocalDate nowLocalDate = DateUtil.dataToLocalDate(dateCj);
                        DzWorkpieceData dzWorkpieceData = new DzWorkpieceData();
                        WorkNumberName workNumberName = dzProductService.getProductNo(productNo);
                        dzWorkpieceData.setProductNo(productNo);
                        dzWorkpieceData.setName(workNumberName.getProductName());
                        dzWorkpieceData.setEquipmentNo(deviceNum);
                        dzWorkpieceData.setEquipmentType(Integer.valueOf(deviceType));
                        dzWorkpieceData.setCheckDate(nowLocalDate);
                        dzWorkpieceData.setDetectorTime(dateCj);
                        dzWorkpieceData.setCreateTime(new Date());
                        dzWorkpieceData.setProducBarcode(producBarcode);

                        dzWorkpieceData.setWorkNumber(workNumber);
                        dzWorkpieceData.setMachineNumber(machineNumber);
                        dzWorkpieceData.setOrderNo(orderNumber);
                        dzWorkpieceData.setLineNo(lineNum);
                        dzWorkpieceData.setOutOk(allState);
                        Integer falg = 6;
                        for (int i = 0; i < itemNumber; i++) {
                            String dataVal = split[falg];
                            falg = falg + 1;
                            Integer isQualified = Integer.valueOf(split[falg]);
                            falg = falg + 1;
                            if (i == 0) {
                                dzWorkpieceData.setDetect01(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk01(isQualified);
                            }
                            if (i == 1) {
                                dzWorkpieceData.setDetect02(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk02(isQualified);
                            }
                            if (i == 2) {
                                dzWorkpieceData.setDetect03(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk03(isQualified);
                            }
                            if (i == 3) {
                                dzWorkpieceData.setDetect04(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk04(isQualified);
                            }
                            if (i == 4) {
                                dzWorkpieceData.setDetect05(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk05(isQualified);
                            }
                            if (i == 5) {
                                dzWorkpieceData.setDetect06(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk06(isQualified);
                            }
                            if (i == 6) {
                                dzWorkpieceData.setDetect07(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk07(isQualified);
                            }
                            if (i == 7) {
                                dzWorkpieceData.setDetect08(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk08(isQualified);
                            }
                            if (i == 8) {
                                dzWorkpieceData.setDetect09(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk09(isQualified);
                            }
                            if (i == 9) {
                                dzWorkpieceData.setDetect10(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk10(isQualified);
                            }
                            if (i == 10) {
                                dzWorkpieceData.setDetect11(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk11(isQualified);
                            }
                            if (i == 11) {
                                dzWorkpieceData.setDetect12(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk12(isQualified);
                            }
                            if (i == 12) {
                                dzWorkpieceData.setDetect13(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk13(isQualified);
                            }
                            if (i == 13) {
                                dzWorkpieceData.setDetect14(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk14(isQualified);
                            }
                            if (i == 14) {
                                dzWorkpieceData.setDetect15(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk15(isQualified);
                            }
                            if (i == 15) {
                                dzWorkpieceData.setDetect16(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk16(isQualified);
                            }
                            if (i == 16) {
                                dzWorkpieceData.setDetect17(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk17(isQualified);
                            }
                            if (i == 17) {
                                dzWorkpieceData.setDetect18(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk18(isQualified);
                            }
                            if (i == 18) {
                                dzWorkpieceData.setDetect19(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk19(isQualified);
                            }
                            if (i == 19) {
                                dzWorkpieceData.setDetect20(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk20(isQualified);
                            }
                            if (i == 20) {
                                dzWorkpieceData.setDetect21(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk21(isQualified);
                            }
                            if (i == 21) {
                                dzWorkpieceData.setDetect22(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk22(isQualified);
                            }
                            if (i == 22) {
                                dzWorkpieceData.setDetect23(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk23(isQualified);
                            }
                            if (i == 23) {
                                dzWorkpieceData.setDetect24(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk24(isQualified);
                            }
                            if (i == 24) {
                                dzWorkpieceData.setDetect25(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk25(isQualified);
                            }
                            if (i == 25) {
                                dzWorkpieceData.setDetect26(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk26(isQualified);
                            }
                            if (i == 26) {
                                dzWorkpieceData.setDetect27(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk27(isQualified);
                            }
                            if (i == 27) {
                                dzWorkpieceData.setDetect28(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk28(isQualified);
                            }
                        }
                        workpieceDataDao.insert(dzWorkpieceData);
                        return dzWorkpieceData;
                    } else {
                        log.error("检测数据数组长度小于5：split：{}", split.toString());
                    }
                } else {
                    log.warn("指令数据信息无内容：device_item_value：{}，tcp_value：{}", deviceItemValue, tcpValue);
                }
            } else {
                log.warn("检测数据指令值无数据：cmdTcps：{}", cmdTcps);
            }
        } else {
            log.warn("处理检测设备数据解析Map为为空：data：{}", map);
        }
        return null;
    }

    /**
     * 产线类型(2米活塞杆=2HSG，3米活塞杆=3HSG，2米缸筒=2GT，3米钢筒=3GT)
     *
     * @param orderNumber
     * @return
     */
    private String getLintType(String orderNumber) {
        if ("DZ-1871".equals(orderNumber) || "DZ-1872".equals(orderNumber) || "DZ-1873".equals(orderNumber) || "DZ-1874".equals(orderNumber) || "DZ-1875".equals(orderNumber)
                || "DZ-1876".equals(orderNumber) || "DZ-1877".equals(orderNumber) || "DZ-1955".equals(orderNumber)) {
            return "2MHSG";
        } else if ("DZ-1878".equals(orderNumber) || "DZ-1879".equals(orderNumber) || "DZ-1880".equals(orderNumber) || "DZ-1956".equals(orderNumber)) {
            return "3MHSG";
        } else if ("DZ-1887".equals(orderNumber) || "DZ-1888".equals(orderNumber) || "DZ-1889".equals(orderNumber)) {
            return "2MGT";
        } else if ("DZ-1890".equals(orderNumber) || "DZ-1891".equals(orderNumber)) {
            return "3MGT";
        } else {
            return "未知";
        }
    }
}
