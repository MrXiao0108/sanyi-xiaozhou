package com.dzics.data.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.business.service.LogService;
import com.dzics.data.common.base.enums.UserIdentityEnum;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.UnderlineTool;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.logms.db.dao.DzEquipmentStateLogDao;
import com.dzics.data.logms.db.dao.SysRealTimeLogsDao;
import com.dzics.data.logms.model.dto.SysOperationLoggingVo;
import com.dzics.data.logms.model.dto.SysloginVo;
import com.dzics.data.logms.model.entity.DzEquipmentStateLog;
import com.dzics.data.logms.model.entity.SysLoginLog;
import com.dzics.data.logms.model.entity.SysOperationLogging;
import com.dzics.data.logms.model.entity.SysRealTimeLogs;
import com.dzics.data.logms.model.vo.SelectEquipmentStateVo;
import com.dzics.data.logms.service.DzEquipmentStateLogService;
import com.dzics.data.logms.service.SysLoginLogService;
import com.dzics.data.logms.service.SysOperationLoggingService;
import com.dzics.data.pub.db.dao.DzProductionLineDao;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Service
@Slf4j
public class LogServiceImpl implements LogService {

    @Autowired
    private DzicsUserService sysUserServiceDao;
    @Autowired
    private SysLoginLogService sysLoginLogService;
    @Autowired
    private SysOperationLoggingService operationLoggingService;
    @Autowired
    private DzEquipmentStateLogService equipmentStateLogService;
    @Autowired
    private DzEquipmentStateLogDao dzEquipmentStateLogMapper;
    @Autowired
    private DzProductionLineDao lineDao;
    @Autowired
    private SysRealTimeLogsDao realTimeLogsDao;


    @Override
    public Result<List<DzEquipmentStateLog>> list(String sub, PageLimit pageLimit, SelectEquipmentStateVo stateVo) {
        String userOrgCode = sysUserServiceDao.getUserOrgCode(sub);
        DzProductionLine dzProductionLine = lineDao.selectById(stateVo.getLineId());
        QueryWrapper<DzEquipmentStateLog> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no",stateVo.getOrderNo());
        wrapper.eq("line_no",dzProductionLine.getLineNo());
        if(stateVo.getStartTime()!=null){
            wrapper.ge("create_time",stateVo.getStartTime());
        }
        if(stateVo.getEndTime()!=null){
            wrapper.le("create_time",DateUtil.dayjiaday(stateVo.getEndTime(),1));

        }
        if(!StringUtils.isEmpty(stateVo.getEquipmentNo())){
            wrapper.like("equipment_no",stateVo.getEquipmentNo());
        }
        if(!StringUtils.isEmpty(stateVo.getEquipmentType())){
            wrapper.eq("equipment_type",stateVo.getEquipmentType());
        }
        if(userOrgCode!=null){
            wrapper.eq("org_code",userOrgCode);
        }
        if(!StringUtils.isEmpty(pageLimit.getType())){
            if("DESC".equals(pageLimit.getType())){
                wrapper.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if("ASC".equals(pageLimit.getType())){
                wrapper.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            }
        }
        if(pageLimit.getPage() != -1){
            PageHelper.startPage(pageLimit.getPage(),pageLimit.getLimit());
        }
        List<DzEquipmentStateLog> dzEquipmentStateLogs = dzEquipmentStateLogMapper.selectList(wrapper);
        PageInfo<DzEquipmentStateLog> info=new PageInfo<>(dzEquipmentStateLogs);
        return new Result(CustomExceptionType.OK,info.getList(),info.getTotal());
    }

    /**
     * 登录日志查询
     *
     * @param pageLimit
     * @param sysloginVo
     * @param sub
     * @param code
     * @return
     */
    @Override
    public Result<SysLoginLog> queryLogin(PageLimit pageLimit, SysloginVo sysloginVo, String sub, String code) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode()) {
            code = null;
        }

        QueryWrapper<SysLoginLog> wpOperL = new QueryWrapper<>();

        if (!StringUtils.isEmpty(sysloginVo.getUserName())) {
            wpOperL.likeRight("user_name", sysloginVo.getUserName());
        }
        if (!StringUtils.isEmpty(sysloginVo.getLoginStatus())) {
            wpOperL.eq("login_status", sysloginVo.getLoginStatus());
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(code)) {
            wpOperL.eq("org_code", code);
        }
        if(sysloginVo.getStartTime()!=null){
            wpOperL.ge("create_time",sysloginVo.getStartTime());
        }
        if(sysloginVo.getEndTime()!=null){
            wpOperL.le("create_time",DateUtil.dayjiaday(sysloginVo.getEndTime(),1));
        }
        if(!StringUtils.isEmpty(pageLimit.getType())){
            if("DESC".equals(pageLimit.getType())){
                wpOperL.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if("ASC".equals(pageLimit.getType())){
                wpOperL.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            }
        }else{
            wpOperL.orderByDesc("create_time");
        }
        if(pageLimit.getPage() != -1){
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        List<SysLoginLog> list = sysLoginLogService.list(wpOperL);
        PageInfo<SysLoginLog> sysLoginLogPageInfo = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, sysLoginLogPageInfo.getList(), sysLoginLogPageInfo.getTotal());
    }

    @Override
    public Result<List<SysOperationLogging>> queryOperLog(PageLimit pageLimit, String sub, String code, SysOperationLoggingVo sysOperationLoggingVo) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode()) {
            code = null;
        }
        QueryWrapper<SysOperationLogging> wpOperL = new QueryWrapper<>();
        wpOperL.select("id");
        wpOperL.orderByDesc("id");
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(sysOperationLoggingVo.getTitle())) {
            wpOperL.likeRight("title", sysOperationLoggingVo.getTitle());
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(sysOperationLoggingVo.getOperDesc())) {
            wpOperL.likeRight("oper_desc", sysOperationLoggingVo.getOperDesc());
        }
        if (sysOperationLoggingVo.getStatus() != null && (sysOperationLoggingVo.getStatus() == 0 || sysOperationLoggingVo.getStatus() == 1)) {
            wpOperL.eq("status", sysOperationLoggingVo.getStatus());
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(code)) {
            wpOperL.eq("org_code", code);
        }
        if(sysOperationLoggingVo.getStartTime()!=null){
            wpOperL.ge("oper_date",sysOperationLoggingVo.getStartTime());
        }
        if(sysOperationLoggingVo.getEndTime()!=null){
            wpOperL.le("oper_date",sysOperationLoggingVo.getEndTime());
        }
        if(!org.apache.commons.lang3.StringUtils.isEmpty(pageLimit.getType())){
            if("DESC".equals(pageLimit.getType())){
                wpOperL.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if("ASC".equals(pageLimit.getType())){
                wpOperL.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            }
        }
        if(pageLimit.getPage() != -1){
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        List<SysOperationLogging> list = operationLoggingService.list(wpOperL);
        PageInfo<SysOperationLogging> sysOperationLoggingPageInfo = new PageInfo<>(list);
        if (sysOperationLoggingPageInfo.getList().isEmpty()) {
            return new Result(CustomExceptionType.OK, sysOperationLoggingPageInfo.getList(), sysOperationLoggingPageInfo.getTotal());
        }
//       再次查询
        List<Long> collect = sysOperationLoggingPageInfo.getList().stream().map(dd -> dd.getId()).collect(Collectors.toList());
        QueryWrapper<SysOperationLogging> loggingQueryWrapper = new QueryWrapper<>();
        loggingQueryWrapper.in("id", collect);
        if(!org.apache.commons.lang3.StringUtils.isEmpty(pageLimit.getType())){
            if("DESC".equals(pageLimit.getType())){
                loggingQueryWrapper.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if("ASC".equals(pageLimit.getType())){
                loggingQueryWrapper.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            }else{
                loggingQueryWrapper.orderByDesc("id");
            }
        }else{
            loggingQueryWrapper.orderByDesc("id");
        }

        List<SysOperationLogging> list1 = operationLoggingService.list(loggingQueryWrapper);
        return new Result(CustomExceptionType.OK, list1, sysOperationLoggingPageInfo.getTotal());
    }


    /**
     * 删除设备运行日志
     * */
    @Override
    public void delEquimentLog(Integer i) {
        long time = System.currentTimeMillis() - ((long)i * (24 * 3600 * 1000));
        Date date = new Date(time);
        QueryWrapper<DzEquipmentStateLog> wrapper = new QueryWrapper<>();
        wrapper.le("create_time", date);
        equipmentStateLogService.remove(wrapper);
    }

    @Override
    public List<SysRealTimeLogs> getIndexWarnLog(String orderId) {
        DzProductionLine line = lineDao.selectOne(new QueryWrapper<DzProductionLine>().eq("order_id",orderId));
        PageHelper.startPage(1,20);
        //TODO 不知是否命中分库分表策略
        List<SysRealTimeLogs> timestamp_time = realTimeLogsDao.selectList(new QueryWrapper<SysRealTimeLogs>()
                .eq("order_code",line.getOrderNo()).eq("line_no",line.getLineNo()).eq("message_type",2).orderByDesc("timestamp_time"));
        PageInfo pageInfo = new PageInfo<>(timestamp_time);
        List<SysRealTimeLogs>list = pageInfo.getList();
        for (SysRealTimeLogs timeLogs : list) {
            //数据分割后的集合（单独一条完整数据）
            timeLogs.setMessage(timeLogs.getMessage().replace("[",""));
            timeLogs.setMessage(timeLogs.getMessage().replace("]",""));
            timeLogs.setMessage(timeLogs.getMessage().trim());
            String[] details = timeLogs.getMessage().split(",");
            //报警代码
            String AlarmNo = details[0];
            //报警类型
            String AlarmType = details[1];
            //轴编号
            String AxisNo = details[2];
            //报警内容长度
            String MsgLength = details[3];
            //报警时间
            String Datetime = details[4];
            //报警内容
            String AlarmMsg = details[5];
            timeLogs.setMessage("报警代码："+AlarmNo+",报警内容："+AlarmMsg+",报警时间："+Datetime);
        }
        list.sort(Comparator.comparing(SysRealTimeLogs::getTimestampTime).reversed());
        return list;
    }


//    /**
//     * DeviceType : 2
//     * Message : A561|11#A563|2#A565|0#A562|0#A802|33.465#A521|0#A501|[0,0,0,0,30,0]#A502|[2085.705,0,2160,0.5,0,0.866]#
//     * OrderCode : DZ-1882
//     * ClientId : DZROBOT
//     * DeviceCode : 01
//     * QueueName : dzics-dev-gather-v1-queue
//     * Timestamp : 2021-01-21 15:00:49.1387
//     * LineNo : 1
//     * MessageId : 661f0350-b6ab-4336-9170-e56c3a42e16f
//     */
//    @Override
//    public void rootBackMq() throws Throwable {
//        List<SysCommunicationLog> list = sysCommunicationLogService.list();
//        if(list!=null){
//            for (SysCommunicationLog sysCommunicationLog : list) {
//                String devicetype = sysCommunicationLog.getDevicetype();
//                String message = sysCommunicationLog.getMessage();
//                String ordercode = sysCommunicationLog.getOrdercode();
//                String clientid = sysCommunicationLog.getClientid();
//                String devicecode = sysCommunicationLog.getDevicecode();
//                String queuename = sysCommunicationLog.getQueuename();
//                Date timestamp = sysCommunicationLog.getTimestamp();
//                String lineno = sysCommunicationLog.getLineno();
//                String messageid = sysCommunicationLog.getMessageid();
//
//                RabbitmqMessage rabbitmqMessage  = new RabbitmqMessage();
//                rabbitmqMessage.setDeviceType(devicetype);
//                rabbitmqMessage.setMessage(message);
//                rabbitmqMessage.setOrderCode(ordercode);
//                rabbitmqMessage.setClientId(clientid);
//                rabbitmqMessage.setDeviceCode(devicecode);
//                rabbitmqMessage.setQueueName(queuename);
//                rabbitmqMessage.setTimestamp(String.valueOf(timestamp));
//                rabbitmqMessage.setLineNo(lineno);
//                rabbitmqMessage.setMessageId(messageid);
//                String msg = JSONObject.toJSONString(rabbitmqMessage);
//                analysisStateCousomer.analysisNumState(msg,Long.valueOf(AmqpHeaders.DELIVERY_TAG),new Channel() {
//                    @Override
//                    public void addShutdownListener(ShutdownListener shutdownListener) {
//
//                    }
//
//                    @Override
//                    public void removeShutdownListener(ShutdownListener shutdownListener) {
//
//                    }
//                    @Override
//                    public ShutdownSignalException getCloseReason() {
//                        return null;
//                    }
//
//                    @Override
//                    public void notifyListeners() {
//
//                    }
//
//                    @Override
//                    public boolean isOpen() {
//                        return false;
//                    }
//
//                    @Override
//                    public int getChannelNumber() {
//                        return 0;
//                    }
//
//                    @Override
//                    public Connection getConnection() {
//                        return null;
//                    }
//
//                    @Override
//                    public void close() throws IOException, TimeoutException {
//
//                    }
//
//                    @Override
//                    public void close(int i, String s) throws IOException, TimeoutException {
//
//                    }
//
//                    @Override
//                    public void abort() throws IOException {
//
//                    }
//
//                    @Override
//                    public void abort(int i, String s) throws IOException {
//
//                    }
//
//                    @Override
//                    public void addReturnListener(ReturnListener returnListener) {
//
//                    }
//
//                    @Override
//                    public ReturnListener addReturnListener(ReturnCallback returnCallback) {
//                        return null;
//                    }
//
//                    @Override
//                    public boolean removeReturnListener(ReturnListener returnListener) {
//                        return false;
//                    }
//
//                    @Override
//                    public void clearReturnListeners() {
//
//                    }
//
//                    @Override
//                    public void addConfirmListener(ConfirmListener confirmListener) {
//
//                    }
//
//                    @Override
//                    public ConfirmListener addConfirmListener(ConfirmCallback confirmCallback, ConfirmCallback confirmCallback1) {
//                        return null;
//                    }
//
//                    @Override
//                    public boolean removeConfirmListener(ConfirmListener confirmListener) {
//                        return false;
//                    }
//
//                    @Override
//                    public void clearConfirmListeners() {
//
//                    }
//
//                    @Override
//                    public Consumer getDefaultConsumer() {
//                        return null;
//                    }
//
//                    @Override
//                    public void setDefaultConsumer(Consumer consumer) {
//
//                    }
//
//                    @Override
//                    public void basicQos(int i, int i1, boolean b) throws IOException {
//
//                    }
//
//                    @Override
//                    public void basicQos(int i, boolean b) throws IOException {
//
//                    }
//
//                    @Override
//                    public void basicQos(int i) throws IOException {
//
//                    }
//
//                    @Override
//                    public void basicPublish(String s, String s1, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
//
//                    }
//
//                    @Override
//                    public void basicPublish(String s, String s1, boolean b, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
//
//                    }
//
//                    @Override
//                    public void basicPublish(String s, String s1, boolean b, boolean b1, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
//
//                    }
//
//                    @Override
//                    public AMQP.Exchange.DeclareOk exchangeDeclare(String s, String s1) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Exchange.DeclareOk exchangeDeclare(String s, BuiltinExchangeType builtinExchangeType) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Exchange.DeclareOk exchangeDeclare(String s, String s1, boolean b) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Exchange.DeclareOk exchangeDeclare(String s, BuiltinExchangeType builtinExchangeType, boolean b) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Exchange.DeclareOk exchangeDeclare(String s, String s1, boolean b, boolean b1, Map<String, Object> map) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Exchange.DeclareOk exchangeDeclare(String s, BuiltinExchangeType builtinExchangeType, boolean b, boolean b1, Map<String, Object> map) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Exchange.DeclareOk exchangeDeclare(String s, String s1, boolean b, boolean b1, boolean b2, Map<String, Object> map) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Exchange.DeclareOk exchangeDeclare(String s, BuiltinExchangeType builtinExchangeType, boolean b, boolean b1, boolean b2, Map<String, Object> map) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public void exchangeDeclareNoWait(String s, String s1, boolean b, boolean b1, boolean b2, Map<String, Object> map) throws IOException {
//
//                    }
//
//                    @Override
//                    public void exchangeDeclareNoWait(String s, BuiltinExchangeType builtinExchangeType, boolean b, boolean b1, boolean b2, Map<String, Object> map) throws IOException {
//
//                    }
//
//                    @Override
//                    public AMQP.Exchange.DeclareOk exchangeDeclarePassive(String s) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Exchange.DeleteOk exchangeDelete(String s, boolean b) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public void exchangeDeleteNoWait(String s, boolean b) throws IOException {
//
//                    }
//
//                    @Override
//                    public AMQP.Exchange.DeleteOk exchangeDelete(String s) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Exchange.BindOk exchangeBind(String s, String s1, String s2) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Exchange.BindOk exchangeBind(String s, String s1, String s2, Map<String, Object> map) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public void exchangeBindNoWait(String s, String s1, String s2, Map<String, Object> map) throws IOException {
//
//                    }
//
//                    @Override
//                    public AMQP.Exchange.UnbindOk exchangeUnbind(String s, String s1, String s2) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Exchange.UnbindOk exchangeUnbind(String s, String s1, String s2, Map<String, Object> map) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public void exchangeUnbindNoWait(String s, String s1, String s2, Map<String, Object> map) throws IOException {
//
//                    }
//
//                    @Override
//                    public AMQP.Queue.DeclareOk queueDeclare() throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Queue.DeclareOk queueDeclare(String s, boolean b, boolean b1, boolean b2, Map<String, Object> map) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public void queueDeclareNoWait(String s, boolean b, boolean b1, boolean b2, Map<String, Object> map) throws IOException {
//
//                    }
//
//                    @Override
//                    public AMQP.Queue.DeclareOk queueDeclarePassive(String s) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Queue.DeleteOk queueDelete(String s) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Queue.DeleteOk queueDelete(String s, boolean b, boolean b1) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public void queueDeleteNoWait(String s, boolean b, boolean b1) throws IOException {
//
//                    }
//
//                    @Override
//                    public AMQP.Queue.BindOk queueBind(String s, String s1, String s2) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Queue.BindOk queueBind(String s, String s1, String s2, Map<String, Object> map) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public void queueBindNoWait(String s, String s1, String s2, Map<String, Object> map) throws IOException {
//
//                    }
//
//                    @Override
//                    public AMQP.Queue.UnbindOk queueUnbind(String s, String s1, String s2) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Queue.UnbindOk queueUnbind(String s, String s1, String s2, Map<String, Object> map) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Queue.PurgeOk queuePurge(String s) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public GetResponse basicGet(String s, boolean b) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public void basicAck(long l, boolean b) throws IOException {
//
//                    }
//
//                    @Override
//                    public void basicNack(long l, boolean b, boolean b1) throws IOException {
//
//                    }
//
//                    @Override
//                    public void basicReject(long l, boolean b) throws IOException {
//
//                    }
//
//                    @Override
//                    public String basicConsume(String s, Consumer consumer) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, DeliverCallback deliverCallback, CancelCallback cancelCallback) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, DeliverCallback deliverCallback, ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, DeliverCallback deliverCallback, CancelCallback cancelCallback, ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, boolean b, Consumer consumer) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, boolean b, DeliverCallback deliverCallback, CancelCallback cancelCallback) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, boolean b, DeliverCallback deliverCallback, ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, boolean b, DeliverCallback deliverCallback, CancelCallback cancelCallback, ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, boolean b, Map<String, Object> map, Consumer consumer) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, boolean b, Map<String, Object> map, DeliverCallback deliverCallback, CancelCallback cancelCallback) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, boolean b, Map<String, Object> map, DeliverCallback deliverCallback, ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, boolean b, Map<String, Object> map, DeliverCallback deliverCallback, CancelCallback cancelCallback, ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, boolean b, String s1, Consumer consumer) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, boolean b, String s1, DeliverCallback deliverCallback, CancelCallback cancelCallback) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, boolean b, String s1, DeliverCallback deliverCallback, ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, boolean b, String s1, DeliverCallback deliverCallback, CancelCallback cancelCallback, ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, boolean b, String s1, boolean b1, boolean b2, Map<String, Object> map, Consumer consumer) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, boolean b, String s1, boolean b1, boolean b2, Map<String, Object> map, DeliverCallback deliverCallback, CancelCallback cancelCallback) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, boolean b, String s1, boolean b1, boolean b2, Map<String, Object> map, DeliverCallback deliverCallback, ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public String basicConsume(String s, boolean b, String s1, boolean b1, boolean b2, Map<String, Object> map, DeliverCallback deliverCallback, CancelCallback cancelCallback, ConsumerShutdownSignalCallback consumerShutdownSignalCallback) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public void basicCancel(String s) throws IOException {
//
//                    }
//
//                    @Override
//                    public AMQP.Basic.RecoverOk basicRecover() throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Basic.RecoverOk basicRecover(boolean b) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Tx.SelectOk txSelect() throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Tx.CommitOk txCommit() throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Tx.RollbackOk txRollback() throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public AMQP.Confirm.SelectOk confirmSelect() throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public long getNextPublishSeqNo() {
//                        return 0;
//                    }
//
//                    @Override
//                    public boolean waitForConfirms() throws InterruptedException {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean waitForConfirms(long l) throws InterruptedException, TimeoutException {
//                        return false;
//                    }
//
//                    @Override
//                    public void waitForConfirmsOrDie() throws IOException, InterruptedException {
//
//                    }
//
//                    @Override
//                    public void waitForConfirmsOrDie(long l) throws IOException, InterruptedException, TimeoutException {
//
//                    }
//
//                    @Override
//                    public void asyncRpc(Method method) throws IOException {
//
//                    }
//
//                    @Override
//                    public Command rpc(Method method) throws IOException {
//                        return null;
//                    }
//
//                    @Override
//                    public long messageCount(String s) throws IOException {
//                        return 0;
//                    }
//
//                    @Override
//                    public long consumerCount(String s) throws IOException {
//                        return 0;
//                    }
//
//                    @Override
//                    public CompletableFuture<Command> asyncCompletableRpc(Method method) throws IOException {
//                        return null;
//                    }
//                });
//            }
//        }
//
//    }

}
