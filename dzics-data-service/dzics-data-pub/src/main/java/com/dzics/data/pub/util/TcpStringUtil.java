package com.dzics.data.pub.util;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.common.base.enums.CmdStateClassification;
import com.dzics.data.common.base.enums.EquiTypeCommonEnum;
import com.dzics.data.common.base.enums.RunStateEnum;
import com.dzics.data.common.base.model.custom.DzTcpDateID;
import com.dzics.data.common.base.model.custom.EqMentStatus;
import com.dzics.data.common.base.model.dto.CmdTcp;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.util.NumberUtils;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.pub.model.dto.TcpDescValue;
import com.dzics.data.pub.service.SysCmdTcpService;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Component
@SuppressWarnings(value = "ALL")
public class TcpStringUtil {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SysCmdTcpService cmdTcpService;


    public Map<String, Object> analysisCmdV2(RabbitmqMessage cmd) {
        Date date = DateUtil.stringDateToformatDate(cmd.getTimestamp());
        long time = date.getTime();
        List<CmdTcp> cmdTcp = this.getCmdTcp(cmd.getMessage());
        DzTcpDateID dzTcpDateId = new DzTcpDateID();
        dzTcpDateId.setOrderNumber(cmd.getOrderCode());
        dzTcpDateId.setProductionLineNumber(cmd.getLineNo());
        dzTcpDateId.setDeviceType(cmd.getDeviceType());
        dzTcpDateId.setDeviceNumber(cmd.getDeviceCode());
//        状态
        List<CmdTcp> state = new ArrayList<>();
//        运行状态
        List<CmdTcp> runState = new ArrayList<>();
//        成品数据
        List<CmdTcp> cpData = new ArrayList<>();
//        毛坯数量
        List<CmdTcp> mpData = new ArrayList<>();
//        合格数量
        List<CmdTcp> okData = new ArrayList<>();
//       脉冲数据
        List<CmdTcp> pulseSignal = new ArrayList<>();
//      设备是检测数据
        List<CmdTcp> checkData = new ArrayList<>();
//        设备生产工件名称工件编号
        List<CmdTcp> workPice = new ArrayList<>();
//        告警指令信息
        List<CmdTcp> alarmRecord = new ArrayList<>();
        CmdTcp a814 = null;
        for (CmdTcp ct : cmdTcp) {
            String tcpValue = ct.getTcpValue();
            String deviceItemValue = ct.getDeviceItemValue();
            String description = ct.getTcpDescription();
            if (tcpValue == null) {
                log.warn("解析指令 ct.getTcp_value() is null");
                continue;
            }
            if (StringUtils.isEmpty(deviceItemValue)) {
//                log.warn("指令：{},指令值:{}", tcpValue, deviceItemValue);
                continue;
            }
            if (StringUtils.isEmpty(description)) {
//                log.warn("指令描述无->指令：{},指令值:{},描述：{}", tcpValue, deviceItemValue, description);
            }
            switch (tcpValue) {
                case EqMentStatus.TCP_ROB_NEEDLE_DETECT:
                    checkData.add(ct);
                    continue;
                case EqMentStatus.TCP_PYLSE_SIGNAL:
                case EqMentStatus.TCP_PYLSE_SIGNAL_2:
                case EqMentStatus.TCP_ROB_PYLSE_SIGNAL:
                    pulseSignal.add(ct);
                    continue;
                case EqMentStatus.TCP_CL_CO_ST:
                    boolean numeric = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric) {
                        state.add(ct);
                    }
                    continue;
                case EqMentStatus.TCP_CL_ST_CNC:
                case EqMentStatus.TCP_CL_ST_ROB:
                case EqMentStatus.TCP_CL_ST_CHJ:
                case EqMentStatus.TCP_CL_ST_JZJ:
                    boolean numeric1 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric1) {
                        state.add(ct);
                    }
                    continue;
                case EqMentStatus.TCP_OPE_MODE_CNC:
                case EqMentStatus.TCP_OPE_MODE_ROB:
                case EqMentStatus.TCP_OPE_MODE_CHJ:
                case EqMentStatus.TCP_OPE_MODE_JZJ:
                    boolean numeric2 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric2) {
                        state.add(ct);
                    }
                    continue;
                case EqMentStatus.TCP_RUN_STATE_CNS:
                case EqMentStatus.TCP_RUN_STATE_ROB:
                case EqMentStatus.TCP_RUN_STATE_CHJ:
                case EqMentStatus.TCP_RUN_STATE_JZJ:
                    boolean numeric3 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric3) {
                        CmdTcp runStateTcp = new CmdTcp();
                        BeanUtils.copyProperties(ct, runStateTcp);
                        if (tcpValue.equals(EqMentStatus.TCP_RUN_STATE_CNS)) {
                            if (Integer.valueOf(deviceItemValue).intValue() != RunStateEnum.CNC_STOP.getCode()) {
                                runStateTcp.setDeviceItemValue(String.valueOf(RunStateEnum.CNC_START.getCode()));
                            }
                        } else {
                            if (Integer.valueOf(deviceItemValue).intValue() != EquiTypeCommonEnum.PRO.getCode()) {
                                runStateTcp.setDeviceItemValue(String.valueOf(EquiTypeCommonEnum.DOWN.getCode()));
                            }
                        }
                        runState.add(runStateTcp);
                        state.add(ct);
                    }
                    continue;
                case EqMentStatus.TCP_EMERGENCY_STATUS_CNS:
                case EqMentStatus.TCP_EMERGENCY_STATUS_ROB:
                case EqMentStatus.TCP_EMERGENCY_STATUS_CHJ:
                case EqMentStatus.TCP_EMERGENCY_STATUS_JZJ:
                    boolean numeric8 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric8) {
                        state.add(ct);
                    }
                    continue;
                case EqMentStatus.TCP_ALARM_STATUS_CNC:
                case EqMentStatus.TCP_ALARM_STATUS_ROB:
                case EqMentStatus.TCP_ALARM_STATUS_CHJ:
                case EqMentStatus.TCP_ALARM_STATUS_JZJ:
                    boolean numeric4 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric4) {
                        alarmRecord.add(ct);
                        state.add(ct);
                    }
                    continue;
                case EqMentStatus.TCP_WORKPIECE_COUNT_CNC:
                case EqMentStatus.TCP_WORKPIECE_COUNT_ROB:
                    boolean numeric5 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric5) {
                        cpData.add(ct);
                    }
                    continue;
                case EqMentStatus.TCP_WORKPIECE_COUNT_MP_ROB:
                    boolean numeric6 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric6) {
                        mpData.add(ct);
                    }
                    continue;
                case EqMentStatus.TCP_WORKPIECE_COUNT_HGP_ROB:
                    boolean numeric7 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric7) {
                        okData.add(ct);
                    }
                    continue;
                case EqMentStatus.CMD_ROB_WORKPIECE_TOTAL:
                    a814 = ct;
                    continue;
                case EqMentStatus.TCP_ABS_POS_CNC:
                case EqMentStatus.TCP_ABS_POS_ROB:
                case EqMentStatus.TCP_WORK_STATE_CNS:
                case EqMentStatus.TCP_WORK_STATE_CHJ:
                case EqMentStatus.TCP_WORK_STATE_JZJ:
                case EqMentStatus.TCP_ROB_WORK_PIECE:
                case EqMentStatus.CMD_CNC_RUN_TIME:
                case EqMentStatus.CMD_CNC_TOOL_NO:
                case EqMentStatus.CMD_ROB_PROCESS_TIME:
                case EqMentStatus.CMD_ROB_SPEED_RATIO:
                case EqMentStatus.TCP_HEAD_POSITION_UD_JZJ:
                case EqMentStatus.TCP_HEAD_POSITION_LR_JZJ:
                case EqMentStatus.TCP_CHJ_SPEED:
                case EqMentStatus.TCP_CHJ_WORKPIECE_SPEED:
                case EqMentStatus.TCP_CHJ_COOL_TEMP:
                case EqMentStatus.TCP_CHJ_COOL_PRESS:
                case EqMentStatus.TCP_CHJ_COOL_FLOW:
                case EqMentStatus.TCO_CNC_JIE_PAI:
                case EqMentStatus.CMD_CNC_CUTTING_TIME:
                case EqMentStatus.CMD_CNC_SPINDLE_SPEED:
                case EqMentStatus.CMD_CNC_FEED_SPEED:
                    state.add(ct);
                    continue;
                default:
                    log.debug("未识别类型跳过:lineNum:{},deviceNum:{},deviceType:{},tcpValue:{}", cmd.getLineNo(), cmd.getDeviceCode(), cmd.getDeviceType(), tcpValue);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put(CmdStateClassification.PYLSE_SIGNAL.getCode(), pulseSignal);
        map.put(CmdStateClassification.RUN_STATE.getCode(), runState);
        map.put(CmdStateClassification.CP_DATA.getCode(), cpData);
        map.put(CmdStateClassification.MP_DATA.getCode(), mpData);
        map.put(CmdStateClassification.Ok_DATA.getCode(), okData);
        map.put(CmdStateClassification.STATE.getCode(), state);
        map.put(CmdStateClassification.DATA_STATE_TIME.getCode(), time);
        map.put(CmdStateClassification.TCP_ID.getCode(), dzTcpDateId);
        map.put(CmdStateClassification.TCP_CHECK_EQMENT.getCode(), checkData);
        map.put(CmdStateClassification.TCP_ROB_WORK_PIECE.getCode(), workPice);
        map.put(CmdStateClassification.ALARM_RECPRD.getCode(), alarmRecord);
        map.put(CmdStateClassification.CMD_ROB_WORKPIECE_TOTAL.getCode(), a814);
        return map;
    }

    /**
     * 指令解析
     *
     * @param str 指令集
     *            0096174513145;01A561|11#A563|2#A565|0#A562|0#A802|158.325#A521|0#A501|[-2.86,1.773,7.687,0,30,0]#A502|[812.667,-40.599,1046.238,0.427,0.023,0.904]@#$
     * @return
     */
    public List<CmdTcp> getCmdTcp(String str) {
        List<CmdTcp> list = new ArrayList<>();
        //截取指令(指定长度~倒数第四位)
        List<String> strings = Arrays.asList(str.split("#"));
        for (String cmdKeyValue : strings) {
            try {
                CmdTcp cmdTcp = new CmdTcp();
                String[] split = cmdKeyValue.split("\\|");
                String sp2 = split[0];
                String sp1 = split[1];
                sp1 = sp1.replace("[", "").replace("]", "");
                String cmdNameV2 = getCmdNameV2(sp2, sp1);
                cmdTcp.setTcpValue(sp2);
                cmdTcp.setDeviceItemValue(sp1);
                cmdTcp.setTcpDescription(cmdNameV2 == null ? sp1 : cmdNameV2);
                list.add(cmdTcp);
            } catch (ArrayIndexOutOfBoundsException Exception) {
//                指令错误
            } catch (Exception e) {
                boolean b = redisUtil.hasKey(RedisKey.ERR_TCP_VALUE + cmdKeyValue);
                if (!b) {
                    redisUtil.set(RedisKey.ERR_TCP_VALUE + cmdKeyValue, cmdKeyValue, 60 * 10);
                    log.error("指令解析失败，异常指令: {},异常：{}", cmdKeyValue, e.getMessage(), e);
                }

            }
        }
        return list;
    }

    /**
     * @param cmd      指令数据 例如 A501
     * @param cmdValue 指令值 [-2.86,1.773,7.687,0,30,0]
     *                 0
     * @return 指令值描述
     */
    public String getCmdNameV2(String cmd, String cmdValue) {
        if (StringUtils.isEmpty(cmdValue)) {
            return "";
        }
        cmdValue = cmdValue.replace("[", "").replace("]", "");
        String key = RedisKey.TCP_CMD_PREFIX + cmd;
        List<TcpDescValue> cmdTcp = redisUtil.lGet(key, 0, -1);
        if (CollectionUtils.isEmpty(cmdTcp)) {
//                查询指令
            cmdTcp = cmdTcpService.getCmdTcp(cmd, cmdValue);
            redisUtil.lSet(key, cmdTcp);
            for (TcpDescValue tcpDescValue : cmdTcp) {
                String deviceItemValue = tcpDescValue.getDeviceItemValue();
                String tcpDescription = tcpDescValue.getTcpDescription();
                if (cmdValue.equals(deviceItemValue)) {
                    return tcpDescription;
                }
            }
        } else {
            for (TcpDescValue tcpDescValue : cmdTcp) {
                String deviceItemValue = tcpDescValue.getDeviceItemValue();
                String tcpDescription = tcpDescValue.getTcpDescription();
                if ("ERROR".equals(tcpDescription)) {
                    return cmdValue;
                }
                if (cmdValue.equals(deviceItemValue)) {
                    return tcpDescription;
                }
            }
        }
        return cmdValue;
    }
}
