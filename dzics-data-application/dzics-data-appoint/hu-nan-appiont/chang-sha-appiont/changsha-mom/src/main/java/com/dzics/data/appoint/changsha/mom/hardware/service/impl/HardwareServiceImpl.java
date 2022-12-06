package com.dzics.data.appoint.changsha.mom.hardware.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.appoint.changsha.mom.hardware.service.HardwareService;
import com.dzics.data.appoint.changsha.mom.model.constant.MomConstant;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrderQrCode;
import com.dzics.data.appoint.changsha.mom.mq.RabbitMQService;
import com.dzics.data.appoint.changsha.mom.service.CachingApi;
import com.dzics.data.appoint.changsha.mom.service.MomOrderQrCodeService;
import com.dzics.data.appoint.changsha.mom.service.MomOrderService;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.DzUdpType;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.model.constant.LogClientType;
import com.dzics.data.common.base.model.constant.MomProgressStatus;
import com.dzics.data.logms.model.entity.SysRealTimeLogs;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.entity.DzWorkStationManagement;
import com.dzics.data.pub.service.DzWorkStationManagementService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author: van
 * @since: 2022-07-12
 */
@Slf4j
@Service
public class HardwareServiceImpl implements HardwareService {

    @Autowired
    public RedissonClient redissonClient;
    @Autowired
    public CachingApi cachingApi;
    @Autowired
    public MomOrderService momOrderService;
    @Autowired
    private RabbitMQService rabbitMQService;
    @Autowired
    private MomOrderQrCodeService qrCodeService;
    @Autowired
    private DzWorkStationManagementService workStationManagementService;

    @Override
    public void udpHandle(String msg) {
        final String key = "dz:lock:data:021";
        RLock lock = redissonClient.getLock(key);
        try {
            lock.lock();
            if (StringUtils.isEmpty(msg)) {
                log.warn("UDP上发数据不存在：msg：{}", msg);
            } else {
                String[] split = msg.split(",");
                if (split.length > 3) {
                    String spl = split[1];
                    switch (spl) {
                        case DzUdpType.udpCmdControl:
                            log.info("收到机器人上发指令:{}", msg);
                            this.udpCmdControlFeedbackHandle(split);
                            break;
                        default:
                            log.warn("队列UDP信号不是别类型：DzUdpType:{} ,message: {}", spl, msg);
                            break;
                    }
                } else {
                    log.warn("UDP上发数据根据逗号分隔长度不应该小于3");
                }
            }
        } catch (Throwable e) {
            log.error("HardwareServiceImpl [udpHandle] 消息: {}, 异常{}", msg, e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void udpCmdControlFeedbackHandle(String[] split) {
        try {
            DzProductionLine line = cachingApi.getOrderIdAndLineId();
            // 指令类型 1200,1201,1202,
            String cmdTypeInner = split[2];
            // 指令值
            String value = split[3];
            if (DzUdpType.udpCmdControlInner.equals(cmdTypeInner)) {
                log.info("DzUdpType.udpCmdControlInner :{}", (Object) split);
                /*保存控制指令下发 回复日志，发送到队列触发到看板
                this.udpCmdControlInner(msgId, cmdTypeInner, value, orderNo, lineNo, null, null);*/
                return;
            }
            if (DzUdpType.udpCmdControlUp.equals(cmdTypeInner)) {
                log.info("DzUdpType.udpCmdControlUp:{}", (Object) split);
                // 订单号
                String orderNo = split[5];
                // 产线号
                String lineNo = split[6];
                //控制指令回传状态
                if (line == null) {
                    log.error("根据订单号：{},产线: {} ，获取订单产线不存在，无法更新订单状态", orderNo, lineNo);
                    return;
                }
                MomOrder monOrder = new MomOrder();
//                精加工
                if(MomConstant.ORDER_DZ_1974.equals(line.getOrderNo()) || MomConstant.ORDER_DZ_1975.equals(line.getOrderNo())){
                    String type = split[12];
                    if(StringUtils.isEmpty(type)){
                        log.error("HardwareServiceImpl [udpCmdControlFeedbackHandle] 机器人回复指令1201，解析后type值为null");
                        return;
                    }
                    List<DzWorkStationManagement> stationManagements = workStationManagementService.list
                            (new QueryWrapper<DzWorkStationManagement>()
                                    .eq("org_code",line.getOrgCode())
                                    .eq("order_id",line.getOrderId())
                                    .eq("line_id",line.getId()));
                    if(CollectionUtils.isEmpty(stationManagements)){
                        log.error("HardwareServiceImpl [udpCmdControlFeedbackHandle] 1201：查询工位信息不存在，org_code：{},order_id:{},line_id:{}",line.getOrgCode(),line.getOrderId(),line.getId());
                        throw new CustomException(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR49.getChinese());
                    }
                    String workStation = "";
                    if("A".equals(type)){
                        for (DzWorkStationManagement management : stationManagements) {
                            if(management.getStationName().contains("1") || management.getStationName().contains("A")){
                                workStation = management.getDzStationCode();
                            }
                        }
                    }else{
                        for (DzWorkStationManagement management : stationManagements) {
                            if(management.getStationName().contains("2") || management.getStationName().contains("B")){
                                workStation = management.getDzStationCode();
                            }
                        }
                    }
                     monOrder = momOrderService.getWorkingOrder(line.getOrderId(), line.getId(),workStation);
                }else {
                    //粗加工、抛光
                    monOrder = momOrderService.getOrderOperationResult(line.getOrderId(), line.getId(), MomProgressStatus.OperationResultLoading);
                }
                if (monOrder == null) {
                    log.error("根据订单: {} ,产线: {} ,执行中状态 order_operation_result: {} 获取订单不存在：monOrder：{}  ", orderNo, lineNo, MomProgressStatus.OperationResultLoading, monOrder);
                    return;
                }
                MomOrder monOrderUpdate = momOrderService.updateOrderStates(monOrder, value);
//                发送订单最新状态到前端页面
                /* deviceStatusPush.sendMomOrderRef(monOrderUpdate, 1);*/
//                发送控制成功日志
                this.udpCmdControlFeedbackRealTimeLogs(monOrder.getWiporderno(), cmdTypeInner, value, orderNo, lineNo, monOrderUpdate.getProgressStatus(), null);
                return;
            }
            //Q,5,1202,1,订单号,产线号,Mom订单号,二维码
            if (DzUdpType.udpCmdControlSum.equals(cmdTypeInner)) {
                // 订单号
                String orderNo = split[4];
                // 产线号
                String lineNo = split[5];
                // Mom订单号
                String wiperNo = split[6];
                // 二维码
                String qrCode = split[7];
                log.info("机器人上发二维码绑定订单指令:{}", (Object) split);
                //根据底层上发的订单号查询数据库中有没有当前订单号
                MomOrder momOrder = momOrderService.getMomOrder(wiperNo,line.getOrderId(),line.getId());
                if (momOrder == null) {
                    log.error("HardwareServiceImpl [udpCmdControlFeedbackHandle] UDP绑定二维码,获取生产订单不存在 岛:{} ,产线: {},ProgressStatus:{},monOrder：{} ", orderNo, lineNo, MomProgressStatus.LOADING, null);
                    return;
                }
                //判断当前二维码有没有绑定其他生产订单
                MomOrderQrCode qrMomOrder = qrCodeService.getQrMomOrder(qrCode,orderNo,lineNo);
                if(qrMomOrder!=null){
                    log.error("HardwareServiceImpl [udpCmdControlFeedbackHandle] 机器人上发数据：{}，当前产品二维码：{}为重复加工二维码",(Object)split,qrCode);
                    return;
                }
                //绑定二维码
                Integer sum = qrCodeService.bandMomOrderQrCode(qrCode, momOrder, orderNo, lineNo);
                //更新订单抓取数量
                momOrderService.updateOrderStateSum(orderNo, lineNo, momOrder, sum);
                return;
            }
            log.error("设备控制指令 返回数据格式异常指令类型:{},{},{},无法识别当前值：{} ", DzUdpType.udpCmdControlInner, DzUdpType.udpCmdControlUp, DzUdpType.udpCmdControlSum, cmdTypeInner);
        } catch (Exception e) {
            log.error("HardwareServiceImpl [udpCmdControlFeedbackHandle] 回传设备控制指令状态处理:{},异常：{}", split, e.getMessage());
        }
    }

    private void udpCmdControlFeedbackRealTimeLogs(String momOrderNo, String cmdInner, String value, String orderNo, String lineNo, String progressStatus, Integer outPut) {
        //机器人回复收到下发的控制指令
        SysRealTimeLogs timeLogs = new SysRealTimeLogs();
        timeLogs.setMessageId(UUID.randomUUID().toString().replaceAll("-", ""));
        timeLogs.setQueueName("dzics-dev-gather-v1-realTime-logs");
        timeLogs.setClientId(LogClientType.ROB_AGV);
        timeLogs.setOrderCode(orderNo);
        timeLogs.setLineNo(lineNo);
        timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
        timeLogs.setDeviceCode(FinalCode.Device_Code);
        timeLogs.setMessageType(1);
        String valueMsg = "";
        if (MomProgressStatus.DOWN.equals(progressStatus)) {
            valueMsg = "机器人回复订单: " + momOrderNo + "执行结果：未开始";
        }
        if (MomProgressStatus.LOADING.equals(progressStatus)) {
            valueMsg = "机器人回复订单: " + momOrderNo + "执行结果：执行中";
        }
        if (MomProgressStatus.SUCCESS.equals(progressStatus)) {
            valueMsg = "机器人回复订单: " + momOrderNo + "执行结果：完工";
        }
        if (MomProgressStatus.DELETE.equals(progressStatus)) {
            valueMsg = "机器人回复订单: " + momOrderNo + "执行结果：删除";
        }
        if (MomProgressStatus.CLOSE.equals(progressStatus)) {
            valueMsg = "机器人回复订单: " + momOrderNo + "执行结果：强制关闭";
        }
        if (MomProgressStatus.STOP.equals(progressStatus)) {
            valueMsg = "机器人回复订单: " + momOrderNo + "执行结果：暂停";
        }
        timeLogs.setMessage(valueMsg);
        timeLogs.setTimestampTime(new Date());
        rabbitMQService.sendRabbitmqLog(JSON.toJSONString(timeLogs));
    }
}
