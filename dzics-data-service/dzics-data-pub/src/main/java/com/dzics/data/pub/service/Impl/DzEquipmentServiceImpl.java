package com.dzics.data.pub.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.vo.MachiningJC;
import com.dzics.data.common.base.model.vo.MachiningMessageStatus;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.db.dao.DzEquipmentDao;
import com.dzics.data.pub.model.dao.DeviceStateDo;
import com.dzics.data.pub.model.dto.EqIdOrgCode;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.vo.DeviceMessage;
import com.dzics.data.pub.model.vo.EquipmentStateDo;
import com.dzics.data.pub.model.vo.JCEquiment;
import com.dzics.data.pub.model.vo.PutEquipmentDataStateVo;
import com.dzics.data.pub.service.DzEquipmentService;
import com.dzics.data.pub.service.kanban.ConvertDeviceStateService;
import com.dzics.data.pub.util.TcpStringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 设备表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Service
@Slf4j
public class DzEquipmentServiceImpl extends ServiceImpl<DzEquipmentDao, DzEquipment> implements DzEquipmentService, ConvertDeviceStateService<List<MachiningMessageStatus>, List<DeviceStateDo>> {
    @Autowired
    private TcpStringUtil tcpStringUtil;
    @Autowired
    private DzEquipmentDao dzEquipmentMapper;

    @Override
    public List<MachiningMessageStatus> toMachiningMessageStatus(List<DeviceStateDo> dataList) {
        List<MachiningMessageStatus> list = new ArrayList<>();
        for (DeviceStateDo data : dataList) {
            int i = data.getDeType();
            MachiningMessageStatus status = new MachiningMessageStatus();
            status.setEquimentId(String.valueOf(data.getEquipmentId()));
            status.setEquipmentNo(data.getEquipmentNo());
            status.setEquipmentType(data.getDeType());
            status.setX("0.000");
            status.setY("0.000");
            status.setZ("0.000");
            if (i == EquiTypeEnum.JQR.getCode()) {//机器人
                String   str = tcpStringUtil.getCmdNameV2("A502", data.getA502());
                if (!StringUtils.isEmpty(str)) {
                    String[] split = str.split(",");
                    for (int j = 0; j < split.length; j++) {
                        if (j == 0) {
                            status.setX(split[0]);
                        }
                        if (j == 1) {
                            status.setY(split[1]);
                        }
                        if (j == 2) {
                            status.setZ(split[2]);
                        }
                    }
                }
                String a563 = data.getA563();
                String a566 = data.getA566();
                String a561 = data.getA561();
                String a567 = data.getA567();
                String alarmStatus = tcpStringUtil.getCmdNameV2("A566", a566);
                String connectState = tcpStringUtil.getCmdNameV2("A561", a561);
                String runSate = getRunSate(a563, a566, a561, "1", a567);
                status.setConnectState(connectState);
                status.setOperatorMode(tcpStringUtil.getCmdNameV2("A562", data.getA562()));
                status.setRunStatus(runSate);
                status.setSpeedRatio(tcpStringUtil.getCmdNameV2("A521", data.getA521()));
                status.setMachiningTime(tcpStringUtil.getCmdNameV2("A802", data.getA802()));
                status.setAlarmStatus(alarmStatus);
                list.add(status);
            } else if (i == EquiTypeEnum.JC.getCode()) {
                String  str = tcpStringUtil.getCmdNameV2("B501", data.getB501());
                if (!StringUtils.isEmpty(str)) {
                    String[] split = str.split(",");
                    for (int j = 0; j < split.length; j++) {
                        if (j == 0) {
                            status.setX(split[0]);
                        }
                        if (j == 1) {
                            status.setY(split[1]);
                        }
                        if (j == 2) {
                            status.setZ(split[2]);
                        }
                    }
                }
                String b562 = data.getB562();
                String b569 = data.getB569();
                String b561 = data.getB561();
                String connectState = tcpStringUtil.getCmdNameV2("B561", b561);
                String alarmStatus = tcpStringUtil.getCmdNameV2("B569", b569);
                String runSate = getRunSate(b562, b569, b561, "3", "");
                status.setConnectState(connectState);
                status.setOperatorMode(tcpStringUtil.getCmdNameV2("B565", data.getB565()));
                status.setRunStatus(runSate);
                status.setFeedSpeed(tcpStringUtil.getCmdNameV2("B541", data.getB541()));
                status.setSpeedOfMainShaft(tcpStringUtil.getCmdNameV2("B551", data.getB551()));
                status.setAlarmStatus(alarmStatus);
                list.add(status);
            } else if (i == EquiTypeEnum.CHJ.getCode()) {
                String h562 = data.getH562();
                String h565 = data.getH565();
                String h561 = data.getH561();
                String connectState = tcpStringUtil.getCmdNameV2("H561", h561);
                String alarmStatus = tcpStringUtil.getCmdNameV2("H565", h565);
                String runSate = getRunSate(h562, h565, h561, "3", "");
                status.setConnectState(connectState);
                status.setAlarmStatus(alarmStatus);
                status.setRunStatus(runSate);
                status.setOperatorMode(tcpStringUtil.getCmdNameV2("H566", data.getH566()));
                status.setMovementSpeed(tcpStringUtil.getCmdNameV2("H706", data.getH706()));//淬火机 移动速度  mm/s
                status.setWorkpieceSpeed(tcpStringUtil.getCmdNameV2("H707", data.getH707()));//工件转速 Rad/min
                status.setCoolantTemperature(tcpStringUtil.getCmdNameV2("H801", data.getH801()));//冷却液温度 ℃
                status.setCoolantPressure(tcpStringUtil.getCmdNameV2("H804", data.getH804()));//冷却液压力 MPa
                status.setCoolantFlow(tcpStringUtil.getCmdNameV2("H805", data.getH805()));//冷却液流量 L/s
                list.add(status);
            }


        }
        return list;
    }

    String getRunSate(String status, String alarm, String connState, String stateType, String standby) {
        //  1作业 2：待机 3：故障 4：关机
        String workState = "关机";
        if (!StringUtils.isEmpty(status)) {
            if ("1".equals(connState)) {
//                            连机
                if ("1".equals(alarm)) {
//                                报警，设置故障
                    workState = "故障";
                } else {
                    if ("1".equals(standby)) {
                        workState = "待机";
                    } else {
                        if (stateType.equals(status)) {
                            workState = "作业 ";
                        } else {
                            workState = "待机";
                        }
                    }

                }
            }
        }
        return workState;
    }

    @Override
    public Result getEquipmentState(PageLimit pageLimit, String lineId) {
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        List<EquipmentStateDo> list = dzEquipmentMapper.getEquipmentState(lineId);
        PageInfo<EquipmentStateDo> info = new PageInfo<>(list);
        return Result.ok(info.getList(), info.getTotal());
    }

    @Override
    public Result putEquipmentDataState(PutEquipmentDataStateVo stateVo) {
        String name = stateVo.getName();
        stateVo.setName(humpToLine2(name));
        Boolean b = dzEquipmentMapper.putEquipmentDataState(stateVo);
        return Result.ok(stateVo);
    }

    @Override
    public DzEquipment getTypeLingEqNo(String deviceNum, String lineNum, String deviceTypeStr, String orderNumber) {
        return getTypeLingEqNov1(deviceNum, lineNum, deviceTypeStr, orderNumber);
    }

    @Override
    public DzEquipment updateByLineNoAndEqNoPush(DzEquipment upDzDqState) {
        return upDzDqState;
    }

    @Override
    public DzEquipment getTypeLingEqNoPush(String deviceNum, String lineNum, String deviceTypeStr, String orderNumber) {
        DzEquipment typeLingEqNo = getTypeLingEqNov1(deviceNum, lineNum, deviceTypeStr, orderNumber);
        if (typeLingEqNo != null) {
            typeLingEqNo.setB527(typeLingEqNo.getCleanTime());
        }
        return typeLingEqNo;
    }

    /**
     * @param lineNum     产线序号
     * @param deviceNum   设备序号
     * @param deviceType  设备类型
     * @param orderNumber
     * @return
     */
    @Override
    public EqIdOrgCode getDeviceOrgCode(String lineNum, String deviceNum, String deviceType, String orderNumber) {
        DzEquipment typeLingEqNo = getTypeLingEqNov1(deviceNum, lineNum, deviceType, orderNumber);
        if (typeLingEqNo != null) {
            String id = typeLingEqNo.getId();
            String orgCode = typeLingEqNo.getOrgCode();
            EqIdOrgCode idOrgCode = new EqIdOrgCode();
            idOrgCode.setOrgCode(orgCode);
            idOrgCode.setDeviceId(id);
            return idOrgCode;
        }
        return null;
    }


    @Override
    public String getDeviceId(String orderCode, String lineNo, String deviceCode, String deviceType) {
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
        wrapper.eq("order_no", orderCode);
        wrapper.eq("line_no", lineNo);
        wrapper.eq("equipment_no", deviceCode);
        wrapper.eq("equipment_type", deviceType);
        DzEquipment dzEquipment = dzEquipmentMapper.selectOne(wrapper);
        if (dzEquipment != null) {
            return dzEquipment.getId().toString();
        }
        return null;
    }

    @Override
    public Integer getTypeLingEqNoDeviceSignalValue(String orderNumber, String lineNum, String deviceType, String deviceNum) {
        QueryWrapper<DzEquipment> wp = new QueryWrapper<>();
        wp.eq("order_no", orderNumber);
        wp.eq("line_no", lineNum);
        wp.eq("equipment_no", deviceNum);
        wp.eq("equipment_type", deviceType);
        wp.select("signal_value");
        DzEquipment one = getOne(wp);
        if (one != null && one.getSignalValue() != null) {
            return one.getSignalValue();
        }
        return 1;
    }

    @Override
    public Long upDownSum(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber) {
        DzEquipment typeLingEqNo = getTypeLingEqNo(deviceNum, lineNum, deviceTypeStr, orderNumber);
        return typeLingEqNo != null ? (typeLingEqNo.getDownSum() == null ? 0L : typeLingEqNo.getDownSum()) : null;
    }

    @Override
    public Long upDateDownSum(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber, Long dowmSum) {
        return dowmSum;
    }

    @Override
    public Long upDownSumTime(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber) {
        DzEquipment typeLingEqNo = getTypeLingEqNo(deviceNum, lineNum, deviceTypeStr, orderNumber);
        return typeLingEqNo != null ? (typeLingEqNo.getDownTime() == null ? 0L : typeLingEqNo.getDownTime()) : null;
    }

    @Override
    public Long upDateDownTime(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber, Long downTime) {
        return downTime;
    }

    @Override
    public Result<DzEquipment> getEquipmentByLineId(String sub, Long id) {
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
        wrapper.select("id", "equipment_name", "equipment_type");
        wrapper.eq("line_id", id);
        List<DzEquipment> dzEquipments = dzEquipmentMapper.selectList(wrapper);
        return new Result(CustomExceptionType.OK, dzEquipments, dzEquipments.size());
    }

    @Override
    public int updateByLineNoAndEqNo(DzEquipment dzEquipment) {
        if (StringUtils.isEmpty(dzEquipment.getLineNo()) ||
                StringUtils.isEmpty(dzEquipment.getEquipmentNo()) ||
                dzEquipment.getEquipmentType() == null ||
                StringUtils.isEmpty(dzEquipment.getOrderNo())) {
            log.warn("更新设备状态参数错误：dzeq:{}", dzEquipment);
        }
        QueryWrapper<DzEquipment> wp = new QueryWrapper<>();
        wp.eq("line_no", dzEquipment.getLineNo());
        wp.eq("equipment_no", dzEquipment.getEquipmentNo());
        wp.eq("equipment_type", dzEquipment.getEquipmentType());
        wp.eq("order_no", dzEquipment.getOrderNo());
        dzEquipment.setIsShow(null);
        dzEquipment.setEquipmentName(null);
        dzEquipment.setNickName(null);
        int update = dzEquipmentMapper.update(dzEquipment, wp);
        return update;
    }

    @Override
    public List<JCEquiment> listjcjqr() {
        LocalDate localDate = LocalDate.now();
        return dzEquipmentMapper.listjcjqr(localDate);
    }

    @Override
    public List<DzEquipment> getRunStaTimeIsNotNull() {
        QueryWrapper<DzEquipment> wp = new QueryWrapper<>();
        wp.select("equipment_no", "equipment_type", "order_no", "line_no");
        wp.isNotNull("start_run_time");
        return dzEquipmentMapper.selectList(wp);
    }

    @Override
    public DzEquipment listjcjqrdeviceid(Long deviceId, LocalDate localDate) {
        return dzEquipmentMapper.listjcjqrdeviceid(deviceId, localDate);
    }


    @Override
    public List<DzEquipment> getDeviceOrderNoLineNo(String orderNo, String lineNo) {
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        wrapper.eq("line_no", lineNo);
        return list(wrapper);
    }


    @Override
    public int updateByLineNoAndEqNoDownTime(DzEquipment dzEquipment) {
        if (StringUtils.isEmpty(dzEquipment.getLineNo()) ||
                StringUtils.isEmpty(dzEquipment.getEquipmentNo()) ||
                dzEquipment.getEquipmentType() == null ||
                StringUtils.isEmpty(dzEquipment.getOrderNo())) {
            log.warn("更新设备状态参数错误：dzeq:{}", dzEquipment);
        }
        QueryWrapper<DzEquipment> wp = new QueryWrapper<>();
        wp.eq("line_no", dzEquipment.getLineNo());
        wp.eq("equipment_no", dzEquipment.getEquipmentNo());
        wp.eq("equipment_type", dzEquipment.getEquipmentType());
        wp.eq("order_no", dzEquipment.getOrderNo());
        dzEquipment.setIsShow(null);
        int update = dzEquipmentMapper.update(dzEquipment, wp);
        return update;
    }

    public DzEquipment getTypeLingEqNov1(String eqNo, String lineNoe, String type, String orderNumber) {
        QueryWrapper<DzEquipment> wp = new QueryWrapper<>();
        wp.eq("equipment_no", eqNo);
        wp.eq("line_no", lineNoe);
        wp.eq("equipment_type", type);
        wp.eq("order_no", orderNumber);
        return getOne(wp);
    }

    @Override
    public MachiningJC getEquimentState(String lineNo, String orderNum, LocalDate now) {
        List<DeviceStateDo> dd = dzEquipmentMapper.getMachiningMessageStatusTwo(lineNo, orderNum, now);
        List<MachiningMessageStatus> status = this.toMachiningMessageStatus(dd);
        MachiningJC machiningjc = new MachiningJC();
        if (CollectionUtils.isNotEmpty(status)) {
            machiningjc.setMachiningMessageStatus(status);
        }
        return machiningjc;
    }


    @Override
    public Result getDevcieLineId(String sub, String lineId) {
        List<DeviceMessage> deviceMessages = dzEquipmentMapper.getDevcieLineId(lineId);
        return Result.OK(deviceMessages);
    }

    @Override
    public List<DzEquipment> getDzEquipments(String orderNo, String lineNo, EquiTypeEnum jc) {
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
        wrapper.eq("order_no", orderNo);
        wrapper.eq("line_no", lineNo);
        wrapper.eq("equipment_type", EquiTypeEnum.JC.getCode());
        List<DzEquipment> dzEquipments = list(wrapper);
        return dzEquipments;
    }

    @Override
    public String getDeviceNo(String eqNo, String orderNo, String lineNo) {
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
        wrapper.eq("equipment_no",eqNo);
        wrapper.eq("order_no",orderNo);
        wrapper.eq("line_no",lineNo);
        DzEquipment one = getOne(wrapper);
        return one.getEquipmentName();
    }

    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 驼峰转下划线,效率比上面高
     */
    public static String humpToLine2(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
