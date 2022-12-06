package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.acquisition.service.AccProNumService;
import com.dzics.data.common.base.enums.CmdStateClassification;
import com.dzics.data.common.base.model.custom.DzTcpDateID;
import com.dzics.data.common.base.model.dto.CmdTcp;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.pdm.model.dao.UpValueDevice;
import com.dzics.data.pdm.model.entity.DzEquipmentProNum;
import com.dzics.data.pdm.model.entity.DzEquipmentProNumDetails;
import com.dzics.data.pdm.model.entity.DzLineShiftDay;
import com.dzics.data.pdm.service.DzEquipmentProNumDetailsService;
import com.dzics.data.pdm.service.DzEquipmentProNumService;
import com.dzics.data.pdm.service.DzLineShiftDayService;
import com.dzics.data.pub.model.dto.EqIdOrgCode;
import com.dzics.data.pub.service.DzEquipmentService;
import com.dzics.data.pub.util.TcpStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AccProNumServiceImpl implements AccProNumService {
    @Autowired
    private TcpStringUtil tcpStringUtil;
    @Autowired
    private DzEquipmentService dzEquipmentService;
    @Autowired
    private DzEquipmentProNumDetailsService detailsService;
    @Autowired
    private DzEquipmentProNumService proNumService;
    @Autowired
    private DzLineShiftDayService lineShiftDayService;
    /**
     * 处理生产数据
     * @param cmd
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public DzEquipmentProNum analysisNum(RabbitmqMessage cmd) {
        Map<String, Object> map = tcpStringUtil.analysisCmdV2(cmd);
        if (CollectionUtils.isNotEmpty(map)) {
//            底层设备上传时间时间
            Long senDate = (Long) map.get(CmdStateClassification.DATA_STATE_TIME.getCode());
//            分类唯一属性值
            DzTcpDateID tcpDateId = (DzTcpDateID) map.get(CmdStateClassification.TCP_ID.getCode());
            if (tcpDateId == null) {
                log.error("分类唯一属性值不存在：DzTcpDateID：{}", tcpDateId);
                return null;
            }
            String lineNum = tcpDateId.getProductionLineNumber();
            String deviceType = tcpDateId.getDeviceType();
            String deviceNum = tcpDateId.getDeviceNumber();
            String orderNumber = tcpDateId.getOrderNumber();
//            成品数量
            List<CmdTcp> cpData = (List<CmdTcp>) map.get(CmdStateClassification.CP_DATA.getCode());
//            合格数量
            List<CmdTcp> okData = (List<CmdTcp>) map.get(CmdStateClassification.Ok_DATA.getCode());
//            毛坯数量
            List<CmdTcp> mpData = (List<CmdTcp>) map.get(CmdStateClassification.MP_DATA.getCode());

            List<CmdTcp> workPiece = (List<CmdTcp>) map.get(CmdStateClassification.TCP_ROB_WORK_PIECE.getCode());
//            定义当前时间 当前日期
            Date nowDate = new Date(senDate);
            LocalTime localTime = DateUtil.dataToLocalTime(nowDate).withNano(0);
            int hour = localTime.getHour();
            LocalDate nowLocalDate = DateUtil.dataToLocalDate(nowDate);
//            数据系统编码
            EqIdOrgCode eqIdOrgCode = dzEquipmentService.getDeviceOrgCode(lineNum, deviceNum, deviceType, orderNumber);
            if (eqIdOrgCode == null) {
                log.warn("当前设备不存在：orderNumber：{},lineNum:{},deviceNum:{},deviceType:{},eqIdOrgCode:{}", orderNumber, lineNum, deviceNum, deviceType, eqIdOrgCode);
                return null;
            }
            String orgCode = eqIdOrgCode.getOrgCode();
            String deviceId = eqIdOrgCode.getDeviceId();
//           上次设备数据
            UpValueDevice upValueDevice = detailsService.getUpValueDevice(lineNum, deviceNum, deviceType, orderNumber);
//           定义当前设备数据
            UpValueDevice nowValueDevice = null;
//         查看当前班次记录是否存在，存在则返回当前班次数据
            DzEquipmentProNum dzEqProNum = null;
            DzEquipmentProNumDetails details = null;
//            默认更新班次生产数据
            boolean falg = true;
//            数据设值是否更新
            if (CollectionUtils.isNotEmpty(cpData) || CollectionUtils.isNotEmpty(mpData) || CollectionUtils.isNotEmpty(okData)) {
//            当前班次定义
                DzLineShiftDay lineShiftDays = lineShiftDayService.getLingShifuDay(deviceId,lineNum, deviceNum, deviceType, orderNumber, nowLocalDate, localTime);
                if (lineShiftDays != null) {
////                    查看当前班次记录是否存在，存在则返回当前班次数据
                    dzEqProNum = proNumService.getDzDayEqProNum(orderNumber,deviceId,lineShiftDays.getId(), "", "", "", hour);
                    if (dzEqProNum == null) {
                        dzEqProNum = new DzEquipmentProNum();
                        dzEqProNum.setEquimentId(deviceId);
                        dzEqProNum.setDayId(lineShiftDays.getId());
                        dzEqProNum.setProductType("");
                        dzEqProNum.setWorkData(nowLocalDate);
                        dzEqProNum.setWorkMouth(nowLocalDate.getYear() + "-" + (nowLocalDate.getMonth().getValue() > 10 ? nowLocalDate.getMonth().getValue() : "0" + nowLocalDate.getMonth().getValue()));
                        dzEqProNum.setWorkYear(nowLocalDate.getYear());
                        dzEqProNum.setOrderNo(orderNumber);
                        dzEqProNum.setLineNo(lineNum);
                        dzEqProNum.setBatchNumber("");
                        dzEqProNum.setModelNumber("");
                        dzEqProNum.setRoughNum(0L);
                        dzEqProNum.setQualifiedNum(0L);
                        dzEqProNum.setNowNum(0L);
                        dzEqProNum.setTotalNum(0L);
                        dzEqProNum.setBadnessNum(0L);
                        dzEqProNum.setOrgCode(orgCode);
                        dzEqProNum.setDelFlag(false);
                        dzEqProNum.setWorkHour(hour);
                        falg = false;
                    }
                } else {
//                    当前班次数据不存在时，这里返回，相当于现在存储的是有班次之前的数量值，一直到有班次是，中间的差额数量会计算到下次有班次的的记录中。
//                    如果当前班次数量不存在时间过长，或生产数量一个周期则数据会错误。
                    log.warn("当前排班数据不存在丢弃数据：lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, cmd);
                    return null;
                }
            } else {
                log.debug("生产数据未发生变化：lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, cmd);
                return null;
            }
            details = new DzEquipmentProNumDetails();
            nowValueDevice = new UpValueDevice();
            details.setWorkNum(0L);
            details.setRoughNum(0L);
            details.setQualifiedNum(0L);
            if (CollectionUtils.isNotEmpty(cpData)) {
//                成品数量本次有值
                log.debug("本次数据有成品数量 :lineNum:{},deviceNum:{},deviceType:{},cpData:{}", lineNum, deviceNum, deviceType, cpData);
//              上次班次成品的值
                Long proNumNowNum = dzEqProNum.getNowNum();
//                上次总数值
                Long upTotalNum = upValueDevice.getTotalNum();
                Long workNum = upValueDevice.getWorkNum();
//                当前值
                Long wkN = Long.valueOf(cpData.get(0).getDeviceItemValue());
//               增加的值
                Long now = calculateQuantity(wkN, workNum);
//                累计总数值
                Long histyTotal = upTotalNum + now;
//                当前班次累计值
                Long proMowNum = proNumNowNum + now;
                dzEqProNum.setNowNum(proMowNum);
                dzEqProNum.setTotalNum(histyTotal);
//                重新设置
                nowValueDevice.setTotalNum(histyTotal);
                nowValueDevice.setWorkNum(wkN);
                details.setWorkNum(wkN);
            } else {
                nowValueDevice.setTotalNum(upValueDevice.getTotalNum());
                nowValueDevice.setWorkNum(upValueDevice.getWorkNum());
            }
            if (CollectionUtils.isNotEmpty(mpData)) {
                log.debug("本次数据有毛坯数量 :lineNum:{},deviceNum:{},deviceType:{},mpData:{}", lineNum, deviceNum, deviceType, mpData);
//              上次班次毛坯的值
                Long proNumRoughNum = dzEqProNum.getRoughNum();
//              上次发送毛坯总数值
                Long upTotalRoughNum = upValueDevice.getTotalRoughNum();
                Long roughNum = upValueDevice.getRoughNum();
//                当前值
                Long wkN = Long.valueOf(mpData.get(0).getDeviceItemValue());
//                本次需要增加的值
                Long now = calculateQuantity(wkN, roughNum);
//                累计总数值
                Long histyTotal = upTotalRoughNum + now;
//                当前班次累计值
                Long nowNumRoughNum = proNumRoughNum + now;
                dzEqProNum.setRoughNum(nowNumRoughNum);
//                重新设置
                nowValueDevice.setTotalRoughNum(histyTotal);
                nowValueDevice.setRoughNum(wkN);
                details.setRoughNum(wkN);
            } else {
                nowValueDevice.setTotalRoughNum(upValueDevice.getTotalRoughNum());
                nowValueDevice.setRoughNum(upValueDevice.getRoughNum());
            }
            if (CollectionUtils.isNotEmpty(okData)) {
                log.debug("本次数据有合格数量 :lineNum:{},deviceNum:{},deviceType:{},okData:{}", lineNum, deviceNum, deviceType, okData);
//                当前班次值
                Long proQualifiedNum = dzEqProNum.getQualifiedNum();
//                历史总数
                Long totalQualifiedNum = upValueDevice.getTotalQualifiedNum();
                Long qualifiedNum = upValueDevice.getQualifiedNum();
//                当前值
                Long wkN = Long.valueOf(okData.get(0).getDeviceItemValue());
//               增加的值
                long now = calculateQuantity(wkN, qualifiedNum);
//                累计总数值
                Long histyTotal = totalQualifiedNum + now;
//                当前班次累计值
                Long nowqnum = proQualifiedNum + now;
//                设置班次新增后值
                dzEqProNum.setQualifiedNum(nowqnum);
//                重新设置
                nowValueDevice.setTotalQualifiedNum(histyTotal);
                nowValueDevice.setQualifiedNum(wkN);
                details.setQualifiedNum(wkN);
            } else {
                nowValueDevice.setTotalQualifiedNum(upValueDevice.getTotalQualifiedNum());
                nowValueDevice.setQualifiedNum(upValueDevice.getQualifiedNum());
            }
            if (falg) {
                dzEqProNum.setBadnessNum(dzEqProNum.getRoughNum() - dzEqProNum.getQualifiedNum());
                proNumService.updateDzEqProNum(dzEqProNum);
            } else {
                dzEqProNum.setBadnessNum(dzEqProNum.getRoughNum() - dzEqProNum.getQualifiedNum());
                proNumService.saveDzEqProNum(dzEqProNum);
            }
            details.setDeviceType(deviceType);
            details.setLineNo(lineNum);
            details.setOrderNo(orderNumber);
            details.setEquipmentNo(deviceNum);
            details.setTotalNum(nowValueDevice.getTotalNum());
            details.setTotalQualifiedNum(nowValueDevice.getTotalQualifiedNum());
            details.setTotalRoughNum(nowValueDevice.getTotalRoughNum());
            details.setDelFlag(false);
            details.setOrgCode(orgCode);
//          存储记录详情
            detailsService.updateByOrderLine(details);
//          缓存当前数据记录值
            detailsService.saveUpValueDevice(lineNum, deviceNum, deviceType, orderNumber, nowValueDevice);
            log.debug("保存当前数据详情& 设置当前生产数量缓存 :lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
            if (CollectionUtils.isNotEmpty(workPiece)) {
                dzEqProNum.setCmdTcpList(workPiece);
            }
            return dzEqProNum;
        } else {
            log.warn("处理生成数据解析Map为为空：data：{}", map);
        }
        return null;
    }


    /**
     * @param nowQuantity 当前数量
     * @param upQuantity  上次数量
     * @return 返回增加的数量值
     */
    private long calculateQuantity(Long nowQuantity, Long upQuantity) {
        long increment = 0;
        if (upQuantity.compareTo(0L) == 0) {
            return increment;
        }
        if (nowQuantity.compareTo(upQuantity) == -1) {
            increment = nowQuantity;
        } else if (nowQuantity.compareTo(upQuantity) == 1) {
            increment = nowQuantity - upQuantity;
        }
        return increment;
    }
}
