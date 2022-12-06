package com.dzics.data.logms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.UnderlineTool;
import com.dzics.data.logms.db.dao.SysCommunicationLogDao;
import com.dzics.data.logms.model.dto.CommuLogPrm;
import com.dzics.data.logms.model.entity.SysCommunicationLog;
import com.dzics.data.logms.model.vo.HeaderClom;
import com.dzics.data.logms.model.vo.TcpLogProDetection;
import com.dzics.data.logms.service.SysCommunicationLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * <p>
 * 通信日志 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-08
 */
@Service
public class SysCommunicationLogServiceImpl extends ServiceImpl<SysCommunicationLogDao, SysCommunicationLog> implements SysCommunicationLogService {

    @Autowired
    private SysCommunicationLogService sysCommunicationLogService;

    @Override
    public void delCommunicationLog(Integer i, String orderNo, String lineNo, String device_type, String device_code) {
        long time = System.currentTimeMillis() - ((long) i * (24 * 3600 * 1000));
        Date date = new Date(time);
        QueryWrapper<SysCommunicationLog> wrapper = new QueryWrapper<>();
        wrapper.eq("order_code", orderNo);
        wrapper.eq("line_no", lineNo);
        wrapper.eq("device_type", device_type);
        wrapper.eq("device_code", device_code);
        wrapper.le("send_timestamp", date);
        remove(wrapper);
    }

    @Override
    public Result<List<SysCommunicationLog>> communicationLog(PageLimit pageLimit, CommuLogPrm commuLogPrm) {
        QueryWrapper<SysCommunicationLog> wp = new QueryWrapper<>();
        if (!StringUtils.isEmpty(commuLogPrm.getQueuename())) {
            wp.eq("queue_name", commuLogPrm.getQueuename());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getOrderNo())) {
            wp.eq("order_code", commuLogPrm.getOrderNo());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getLineNo())) {
            wp.eq("line_no", commuLogPrm.getLineNo());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getDevicecode())) {
            wp.eq("device_code", commuLogPrm.getDevicecode());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getDevicetype())) {
            wp.eq("device_type", commuLogPrm.getDevicetype());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getStartTime())) {
            wp.gt("send_timestamp", commuLogPrm.getStartTime());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getEndTime())) {
            long time = commuLogPrm.getEndTime().getTime() + (long) 24 * (3600 * 1000);
            wp.lt("send_timestamp", new Date(time));
        }
        if (!StringUtils.isEmpty(pageLimit.getType())) {
            if ("DESC".equals(pageLimit.getType())) {
                wp.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if ("ASC".equals(pageLimit.getType())) {
                wp.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else {
                wp.orderByDesc("send_timestamp");
            }
        } else {
            wp.orderByDesc("send_timestamp");
        }
        if(pageLimit.getPage() != -1){
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        List<SysCommunicationLog> list = sysCommunicationLogService.list(wp);
        PageInfo<SysCommunicationLog> pageInfo = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, pageInfo.getList(), pageInfo.getTotal());
    }


    @Override
    public Result communicationLogTcp(PageLimit pageLimit, CommuLogPrm commuLogPrm) {

        QueryWrapper<SysCommunicationLog> wp = new QueryWrapper<>();
        if (!StringUtils.isEmpty(commuLogPrm.getQueuename())) {
            wp.eq("queue_name", commuLogPrm.getQueuename());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getOrderNo())) {
            wp.eq("order_code", commuLogPrm.getOrderNo());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getLineNo())) {
            wp.eq("line_no", commuLogPrm.getLineNo());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getDevicetype())) {
            wp.eq("device_type", commuLogPrm.getDevicetype());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getDevicecode())) {
            wp.eq("device_code", commuLogPrm.getDevicecode());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getStartTime())) {
            wp.gt("send_timestamp", commuLogPrm.getStartTime());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getEndTime())) {
            long time = commuLogPrm.getEndTime().getTime() + (long) 24 * (3600 * 1000);
            wp.lt("send_timestamp", new Date(time));
        }
        if (!StringUtils.isEmpty(pageLimit.getType())) {
            if ("DESC".equals(pageLimit.getType())) {
                wp.orderByDesc(pageLimit.getField());
            } else if ("ASC".equals(pageLimit.getType())) {
                wp.orderByAsc(pageLimit.getField());
            }
        }
        if(pageLimit.getPage() != -1){
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        List<Map<String, Object>> list = sysCommunicationLogService.listMaps(wp);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        List<Map<String, Object>> maps = pageInfo.getList();
        TcpLogProDetection proDetection = new TcpLogProDetection();
        List<HeaderClom> tableColumn = proDetection.getTableColumn();
//        定义动态获取的所有需要添加的表头
        Map<String, String> tabClom = new HashMap<>();
        for (Map<String, Object> communicationLog : maps) {
            for (Map.Entry<String, Object> stringObjectEntry : communicationLog.entrySet()) {
                String key = stringObjectEntry.getKey();
                Object obj = stringObjectEntry.getValue();
                if (key.equals("message")) {
                    String message = obj.toString();
                    List<String> tcpCmd = Arrays.asList(message.split("#"));
                    for (String tcp : tcpCmd) {
                        String[] tcpAndValue = tcp.split("\\|");
                        tabClom.put(tcpAndValue[0], tcpAndValue[0]);
                    }
                }
            }

        }
//        添加指令需要添加的表头
        for (Map.Entry<String, String> stringStringEntry : tabClom.entrySet()) {
            String key = stringStringEntry.getKey();
            HeaderClom clom = new HeaderClom();
            clom.setColData(key);
            clom.setColName(key);
            tableColumn.add(clom);
        }
        List<Map<String, Object>> tableData = new ArrayList<>();
        for (int i = 0; i < maps.size(); i++) {
//            元数据
            Map<String, Object> map = new HashMap<>();
            for (Map.Entry<String, String> stringStringEntry : tabClom.entrySet()) {
                map.put(stringStringEntry.getKey(), "");
            }
            Map<String, Object> tcpData = maps.get(i);
            for (Map.Entry<String, Object> stringObjectEntry : tcpData.entrySet()) {
                String key = stringObjectEntry.getKey();
                Object value = stringObjectEntry.getValue();
                if (!key.equals("message")) {
                    if (!key.equals("communication_key")) {
                        if (key.equals("DeviceType")) {
                            if (String.valueOf(EquiTypeEnum.JC.getCode()).equals(value)) {
                                map.put(key, EquiTypeEnum.JC.getDesc());
                            } else if (String.valueOf(EquiTypeEnum.JCSB.getCode()).equals(value)) {
                                map.put(key, EquiTypeEnum.JCSB.getDesc());
                            } else if (String.valueOf(EquiTypeEnum.JQR.getCode()).equals(value)) {
                                map.put(key, EquiTypeEnum.JQR.getDesc());
                            } else if (String.valueOf(EquiTypeEnum.XJ.getCode()).equals(value)) {
                                map.put(key, EquiTypeEnum.XJ.getDesc());
                            } else if (String.valueOf(EquiTypeEnum.EQCODE.getCode()).equals(value)) {
                                map.put(key, EquiTypeEnum.EQCODE.getDesc());
                            } else if (String.valueOf(EquiTypeEnum.AVG.getCode()).equals(value)) {
                                map.put(key, EquiTypeEnum.AVG.getDesc());
                            } else if (String.valueOf(EquiTypeEnum.CHJ.getCode()).equals(value)) {
                                map.put(key, EquiTypeEnum.CHJ.getDesc());
                            } else if (String.valueOf(EquiTypeEnum.JZJ.getCode()).equals(value)) {
                                map.put(key, EquiTypeEnum.JZJ.getDesc());
                            } else {
                                map.put(key, value);
                            }
                        } else {
                            map.put(key, value);
                        }
                    }
                } else {
                    String tcps = value.toString();
                    List<String> tcpCmd = Arrays.asList(tcps.split("#"));
                    for (String tcp : tcpCmd) {
                        String[] tcpAndValue = tcp.split("\\|");
                        if (tcpAndValue.length == 2) {
                            map.put(tcpAndValue[0], tcpAndValue[1]);
                        } else {
                            map.put(tcpAndValue[0], "");
                        }
                    }

                }

            }
            tableData.add(map);
        }
        proDetection.setTableColumn(tableColumn);
        proDetection.setTableData(tableData);
        return new Result(CustomExceptionType.OK, proDetection, pageInfo.getTotal());
    }
}
