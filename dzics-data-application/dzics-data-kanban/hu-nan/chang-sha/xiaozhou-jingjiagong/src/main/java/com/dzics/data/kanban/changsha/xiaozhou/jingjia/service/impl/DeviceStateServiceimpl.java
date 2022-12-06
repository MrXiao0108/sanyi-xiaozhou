package com.dzics.data.kanban.changsha.xiaozhou.jingjia.service.impl;

import com.dzics.data.kanban.changsha.xiaozhou.jingjia.model.dto.MachiningMessageStatus;
import com.dzics.data.pub.db.dao.DzEquipmentDao;
import com.dzics.data.pub.model.dao.DeviceStateDo;
import com.dzics.data.pub.service.DeviceStateService;
import com.dzics.data.pub.service.kanban.ConvertDeviceStateService;
import com.dzics.data.pub.util.TcpStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DeviceStateServiceimpl implements DeviceStateService<List<MachiningMessageStatus>>, ConvertDeviceStateService<List<MachiningMessageStatus>,List<DeviceStateDo>> {

    @Autowired
    private DzEquipmentDao dzEquipmentDao;
    @Autowired
    private TcpStringUtil tcpStringUtil;

    @Override
    public List<MachiningMessageStatus> getDeivceState(String orderNo, String lineNo) {
        LocalDate now = LocalDate.now();
//        获取设备状态
        List<DeviceStateDo> dd = dzEquipmentDao.getMachiningMessageStatusTwo(lineNo, orderNo, now);
//        设置翻译转化设置状态
        List<MachiningMessageStatus> machiningMessageStatuses = this.toMachiningMessageStatus(dd);
        return machiningMessageStatuses;
    }

    /**
     * 转化设备状态
     *
     * @param dataList 设备状态指令信息
     * @return
     */
    @Override
    public List<MachiningMessageStatus> toMachiningMessageStatus(List<DeviceStateDo> dataList) {
        List<MachiningMessageStatus> list = new ArrayList<>();
        for (DeviceStateDo data : dataList) {
            int i = data.getDeType();
            if (i == 3 || i == 2) {
                MachiningMessageStatus status = new MachiningMessageStatus();
                status.setEquimentId(String.valueOf(data.getEquipmentId()));
                status.setEquipmentNo(data.getEquipmentNo());
                status.setEquipmentType(data.getDeType());
                if (i == 3) {//机器人
                    String jointLocation = tcpStringUtil.getCmdNameV2("A501", data.getA501());
                    status.setConnectState(tcpStringUtil.getCmdNameV2("A561", data.getA561()));
                    status.setOperatorMode(tcpStringUtil.getCmdNameV2("A562", data.getA562()));
                    status.setRunStatus(tcpStringUtil.getCmdNameV2("A563", data.getA563()));
                    status.setSpeedRatio(tcpStringUtil.getCmdNameV2("A521", data.getA521()));
                    status.setMachiningTime(tcpStringUtil.getCmdNameV2("A802", data.getA802()));
                    status.setAlarmStatus(tcpStringUtil.getCmdNameV2("A566", data.getA566()));
                    if(!StringUtils.isEmpty(jointLocation)){
                        String[] split = jointLocation.split(",");
                        for(int j = 0;j<split.length;j++){
                            if(j==0){
                                status.setJ1(split[0]);
                            }
                            if(j==1){
                                status.setJ2(split[1]);
                            }
                            if(j==2){
                                status.setJ3(split[2]);
                            }
                            if(j==3){
                                status.setJ4(split[3]);
                            }
                            if(j==4){
                                status.setJ5(split[4]);
                            }
                            if(j==5){
                                status.setJ6(split[5]);
                            }
                        }
                    }
                } else {
                    //机床
                    String realTimeLocation = tcpStringUtil.getCmdNameV2("B501", data.getB501());
                    status.setConnectState(tcpStringUtil.getCmdNameV2("B561", data.getB561()));
                    status.setOperatorMode(tcpStringUtil.getCmdNameV2("B565", data.getB565()));
                    status.setRunStatus(tcpStringUtil.getCmdNameV2("B562", data.getB562()));
                    status.setAlarmStatus(tcpStringUtil.getCmdNameV2("B569", data.getB569()));
                    status.setFeedSpeed(tcpStringUtil.getCmdNameV2("B541", data.getB541()));
                    status.setSpeedOfMainShaft(tcpStringUtil.getCmdNameV2("B551", data.getB551()));
                    status.setSpindleLoad(tcpStringUtil.getCmdNameV2("B801", data.getB801()));
                    if (!StringUtils.isEmpty(realTimeLocation)) {
                        String[] split = realTimeLocation.split(",");
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
                }
                list.add(status);
            }

        }
        return list;
    }
}
