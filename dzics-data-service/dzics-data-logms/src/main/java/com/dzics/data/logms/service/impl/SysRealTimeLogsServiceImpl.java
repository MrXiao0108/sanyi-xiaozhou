package com.dzics.data.logms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.dto.log.ReatimLogRes;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.model.custom.EqMentStatus;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.logms.db.dao.SysRealTimeLogsDao;
import com.dzics.data.logms.model.entity.SysRealTimeLogs;
import com.dzics.data.logms.model.vo.WarnLogVo;
import com.dzics.data.logms.service.SysRealTimeLogsService;
import com.dzics.data.redis.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 设备运行告警日志 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Service
public class SysRealTimeLogsServiceImpl extends ServiceImpl<SysRealTimeLogsDao, SysRealTimeLogs> implements SysRealTimeLogsService {
    @Autowired
    private SysRealTimeLogsDao logsMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<SysRealTimeLogs> saveRealTimeLog(RabbitmqMessage rabbitmqMessage) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<SysRealTimeLogs>sysRealTimeLogs=new ArrayList<>();
        String message = rabbitmqMessage.getMessage();
        if (!StringUtils.isEmpty(message)) {
            String[] split = message.split("\\|");
            if (split.length < 2) {
                return null;
            }
            // 指令信息
            String cmd = split[0];
            if (cmd.equals(EqMentStatus.CMD_ROB_RUN_INFO)) {
                String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(split[1], "[");
                deviceItemValue = deviceItemValue.substring(0, deviceItemValue.length() - 1);
                String[] msg = deviceItemValue.split("TT");
                if (msg.length < 2) {
                    return null;
                }
                String msgType = msg[0];
                String messageSave = msg[1];
                SysRealTimeLogs realTimeLogs = new SysRealTimeLogs();
                realTimeLogs.setMessageId(rabbitmqMessage.getMessageId());
                realTimeLogs.setQueueName(rabbitmqMessage.getQueueName());
                realTimeLogs.setClientId(rabbitmqMessage.getClientId());
                realTimeLogs.setOrderCode(rabbitmqMessage.getOrderCode());
                realTimeLogs.setLineNo(rabbitmqMessage.getLineNo());
                realTimeLogs.setDeviceType(rabbitmqMessage.getDeviceType());
                realTimeLogs.setDeviceCode(rabbitmqMessage.getDeviceCode());
                realTimeLogs.setMessageType(Integer.valueOf(msgType));
                realTimeLogs.setMessage(messageSave);
                realTimeLogs.setTimestampTime(new Date());
                save(realTimeLogs);
                sysRealTimeLogs.add(realTimeLogs);
                return sysRealTimeLogs;
            }
            if (cmd.equals(EqMentStatus.CMD_CND_WARING_LOG)) {
                String msgData = split[1];
                //数据分割后的集合（单独一条完整数据）
                String [] msgList = msgData.split("\\[");
                for(int i = 1;i<msgList.length;i++){
                    String[] details = msgList[i].split(",");
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
                    //没有缓存则新增缓存
                    if(!redisUtil.hasKey(RedisKey.Alarm_TOOL_TIME+rabbitmqMessage.getOrderCode())){
                        redisUtil.set(RedisKey.Alarm_TOOL_TIME+rabbitmqMessage.getOrderCode(),String.valueOf(Datetime));
                    }
                    String timeWhere = String.valueOf(redisUtil.get(RedisKey.Alarm_TOOL_TIME + rabbitmqMessage.getOrderCode()));
                    //如果上发数据的时间小于等于缓存时间，跳过
                    if(simpleDateFormat.parse(Datetime).getTime()<=simpleDateFormat.parse(timeWhere).getTime()){
                        continue;
                    }
                    //缓存重置
                    redisUtil.del(RedisKey.Alarm_TOOL_TIME+rabbitmqMessage.getOrderCode());
                    redisUtil.set(RedisKey.Alarm_TOOL_TIME+rabbitmqMessage.getOrderCode(),String.valueOf(Datetime));
                    SysRealTimeLogs realTimeLogs = new SysRealTimeLogs();
                    realTimeLogs.setMessageId(rabbitmqMessage.getMessageId());
                    realTimeLogs.setQueueName(rabbitmqMessage.getQueueName());
                    realTimeLogs.setClientId(rabbitmqMessage.getClientId());
                    realTimeLogs.setOrderCode(rabbitmqMessage.getOrderCode());
                    realTimeLogs.setLineNo(rabbitmqMessage.getLineNo());
                    realTimeLogs.setDeviceType(rabbitmqMessage.getDeviceType());
                    realTimeLogs.setDeviceCode(rabbitmqMessage.getDeviceCode());
                    realTimeLogs.setMessageType(2);
                    realTimeLogs.setTimestampTime(simpleDateFormat.parse(Datetime));
                    realTimeLogs.setMessage("["+msgList[i]);
                    save(realTimeLogs);
                    //Message重置，后续推送看板
                    realTimeLogs.setMessage("报警代码:"+AlarmNo+",报警内容:"+AlarmMsg+",报警时间:"+Datetime);
                    sysRealTimeLogs.add(realTimeLogs);
                }
                return sysRealTimeLogs;
            }
        }
        return null;
    }

    @Override
    public List<ReatimLogRes> getReatimeLogsType(String orderNo, String lineNo, String logType, Integer size) {
        return logsMapper.getReatimeLogsType(orderNo, lineNo, logType, size);
    }

    @Override
    public List<ReatimLogRes> getReatimeLog(String orderNo, String lineNo, String deviceType, String deviceCode, Integer logtype, Integer size) {
        return logsMapper.getReatimeLog(orderNo, lineNo, deviceType, deviceCode, logtype, size);
    }



    @Override
    public void delJobExecutionLog(int delDay) {
        long time = System.currentTimeMillis() - ((long) delDay * (24 * 3600 * 1000));
        Timestamp timestamp = new Timestamp(time);
        logsMapper.delJobExecutionLog(timestamp);
    }

    @Override
    public void delJobStatusTraceLog(int delDay) {
        long time = System.currentTimeMillis() - ((long) delDay * (24 * 3600 * 1000));
        Timestamp timestamp = new Timestamp(time);
        logsMapper.delJobStatusTraceLog(timestamp);
    }

    @Override
    public Result<List<SysRealTimeLogs>> getBackreatimelog(WarnLogVo warnLogVo) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //默认展示一周的数据
        if(StringUtils.isEmpty(warnLogVo.getBeginTime()) && StringUtils.isEmpty(warnLogVo.getEndTime())){
            warnLogVo.setEndTime(simpleDateFormat.format(new Date()));
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(simpleDateFormat.parse(warnLogVo.getEndTime()));
            calendar.add(Calendar.DATE,-7);
            warnLogVo.setBeginTime(simpleDateFormat.format(calendar.getTime()));
        }
        warnLogVo.setEquipmentType(String.valueOf(warnLogVo.getEquipmentType()));
        if(warnLogVo.getPage()!=-1){
            PageHelper.startPage(warnLogVo.getPage(), warnLogVo.getLimit());
        }
        List<SysRealTimeLogs> realtimeLog = logsMapper.getBackReatimeLog(warnLogVo.getOrderNo(),warnLogVo.getEquipmentCode(),
                warnLogVo.getMessage(),warnLogVo.getBeginTime(),warnLogVo.getEndTime(), warnLogVo.getEquipmentType(),
                warnLogVo.getField(), warnLogVo.getType());
        PageInfo pageInfo = new PageInfo<>(realtimeLog);
        return Result.ok(pageInfo.getList());
    }
}
