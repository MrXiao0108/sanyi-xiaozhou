package com.dzics.data.acquisition.service.impl;

import com.dzics.data.common.base.model.custom.EqMentStatus;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.acquisition.service.AccToolService;
import com.dzics.data.acquisition.service.RedisToolInfoListService;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.service.DzEquipmentService;
import com.dzics.data.common.base.dto.GetToolInfoDataDo;
import com.dzics.data.common.base.dto.ToolDataDo;
import com.dzics.data.tool.model.entity.DzToolCompensationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AccToolServiceImpl implements AccToolService {

    @Autowired
    private RedisToolInfoListService redisToolInfoListService;
    @Autowired
    private DzEquipmentService dzEquipmentService;

    @Override
    public GetToolInfoDataDo getEqToolInfoList(RabbitmqMessage rabbitmqMessage) {
        String timestamp = rabbitmqMessage.getTimestamp();
        String orderNumber = rabbitmqMessage.getOrderCode();
        String lineNum = rabbitmqMessage.getLineNo();
        String deviceNum = rabbitmqMessage.getDeviceCode();
        String deviceTypeStr = rabbitmqMessage.getDeviceType();
        String msg = rabbitmqMessage.getMessage();
        if (msg == null) {
            return null;
        }
        //查询设备
        DzEquipment upDzDqState = dzEquipmentService.getTypeLingEqNo(deviceNum, lineNum, deviceTypeStr, orderNumber);
        if (ObjectUtils.isEmpty(upDzDqState)) {
            log.error("AccToolServiceImpl [getEqToolInfoList] ObjectUtils.isEmpty(upDzDqState) deviceNum{}, lineNum{}, deviceTypeStr{}, orderNumber{}",
                    deviceNum, lineNum, deviceTypeStr, orderNumber);
        }
        String id = upDzDqState.getId();
        //解析数据
        List<DzToolCompensationData> data = new ArrayList<>();
        try {
            List<DzToolCompensationData> res = new ArrayList<>();
            String[] split1 = msg.split("#");
            for (String str : split1) {
                String[] split = str.split("\\|");
                String cmd = split[0];//B803 或  B804
                String cmdValue = split[1];
                String replace = cmdValue.replace("[", "");
                String[] valueList = replace.split("]");//得到的指令值
                if (EqMentStatus.CMD_CUTTING_TOOL_FILE.equals(cmd)) {//刀具寿命
                    data = getLife(id, valueList);
                } else if (EqMentStatus.CMD_CUTTING_TOOL_INFO.equals(cmd)) {//刀具信息
                    data = getGeometry(id, valueList);
                }
                if (data != null && data.size() > 0) {
                    res = redisToolInfoListService.updateCompensationDataList(data);
                } else {
                    return null;
                }
            }
            //封装数据返回
            GetToolInfoDataDo toolInfoDataDo = getToolInfoDataDo(upDzDqState, res);
            toolInfoDataDo.setOrderNo(orderNumber);
            toolInfoDataDo.setLineNo(lineNum);
            return toolInfoDataDo;
        } catch (Exception e) {
            log.error("解析刀具指令异常,异常指令：{}", msg);
        }
        return null;
    }

    /**
     * 解析刀具信息数据
     * 01,         11.001,     11.002,      12.003,      13.004,        1.101,      1.210,      2.121,      3.112,         1
     * 刀具号			X轴（形状） Y轴（形状）  Z轴（形状）  半径（形状）   X轴（磨损） Y轴（磨损） Z轴（磨损）  半径（磨损）  方向
     *
     * @return
     */
    public List<DzToolCompensationData> getGeometry(String eqId, String[] strList) {
        List<DzToolCompensationData> list = redisToolInfoListService.getCompensationDataList(eqId);
        for (String str : strList) {
            String[] strings = str.split(",");
            Integer toolNo = Integer.valueOf(strings[0]);//刀具号
            for (DzToolCompensationData data : list) {
                //寻找刀具 刀组和设备一样的记录
                if (data.getToolNo().intValue() == toolNo.intValue()) {
                    data.setToolGeometryX(new BigDecimal(strings[1]));
                    data.setToolGeometryY(new BigDecimal(strings[2]));
                    data.setToolGeometryZ(new BigDecimal(strings[3]));
                    data.setToolGeometryRadius(new BigDecimal(strings[4]));
                    data.setToolWearX(new BigDecimal(strings[5]));
                    data.setToolWearY(new BigDecimal(strings[6]));
                    data.setToolWearZ(new BigDecimal(strings[7]));
                    data.setToolWearRadius(new BigDecimal(strings[8]));
                    data.setToolNoseDirection(Integer.valueOf(strings[9]));
                }
            }
        }
        return list;
    }

    /**
     * 解析刀具寿命
     *
     * @return strings    01,    1,      100,   200,    2
     * 刀具号 刀具组   寿命 使用次数  类型
     */
    public List<DzToolCompensationData> getLife(String eqId, String[] strList) {
        List<DzToolCompensationData> list = redisToolInfoListService.getCompensationDataList(eqId);
        for (String str : strList) {
            String[] strings = str.split(",");
            Integer toolNo = Integer.valueOf(strings[0]);//刀具号
            Integer groupNo = Integer.valueOf(strings[1]);//刀组号
            for (DzToolCompensationData data : list) {
                //寻找刀具 刀组和设备一样的记录
                if (data.getToolNo().intValue() == toolNo.intValue()
                        && groupNo.intValue() == data.getGroupNo().intValue()) {
                    data.setToolLife(Integer.valueOf(strings[2]));//寿命
                    data.setToolLifeCounter(Integer.valueOf(strings[3]));//使用量
                    data.setToolLifeCounterType(Integer.valueOf(strings[4]));//寿命记数类型
                }
            }
        }
        return list;
    }

    /**
     * 封装刀具信息返回
     */
    public GetToolInfoDataDo getToolInfoDataDo(DzEquipment equipment, List<DzToolCompensationData> res) {
        GetToolInfoDataDo getToolInfoDataDo = new GetToolInfoDataDo();
        getToolInfoDataDo.setEquipmentId(equipment.getId());
        getToolInfoDataDo.setEquipmentName(equipment.getEquipmentName());
        getToolInfoDataDo.setEquipmentNo(equipment.getEquipmentNo());
        //设备绑定的刀具信息集合
        List<ToolDataDo> toolDataDos = new ArrayList<>();
        for (DzToolCompensationData data : res) {
            if (data.getEquipmentId() != null && data.getEquipmentId().equals(equipment.getId())) {
                ToolDataDo toolDataDo = new ToolDataDo();
                toolDataDo.setToolLife(data.getToolLife());
                toolDataDo.setToolLifeCounter(data.getToolLifeCounter());
                if (data.getToolNo() != null) {
                    if (data.getToolNo().intValue() < 10) {
                        toolDataDo.setToolNo("T0" + data.getToolNo().toString());
                    } else {
                        toolDataDo.setToolNo("T" + data.getToolNo().toString());
                    }
                }
                toolDataDos.add(toolDataDo);
            }
        }
        //填充设备绑定的刀具信息集合
        getToolInfoDataDo.setToolDataList(toolDataDos);
        return getToolInfoDataDo;
    }
}
