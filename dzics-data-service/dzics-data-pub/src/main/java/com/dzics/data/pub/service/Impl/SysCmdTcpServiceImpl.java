package com.dzics.data.pub.service.Impl;


import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.dto.CmdTcp;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.UnderlineTool;
import com.dzics.data.pub.db.dao.SysCmdTcpDao;
import com.dzics.data.pub.model.dto.TcpDescValue;
import com.dzics.data.pub.model.entity.SysCmdTcp;
import com.dzics.data.pub.model.vo.AddAndUpdCmdTcpVo;
import com.dzics.data.pub.model.vo.CmdTcpItemVo;
import com.dzics.data.pub.model.vo.SysCmdTcpItemVo;
import com.dzics.data.pub.service.SysCmdTcpService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.dzics.data.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * tcp 指令标识表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Service
@Slf4j
@SuppressWarnings(value = "ALL")
public class SysCmdTcpServiceImpl extends ServiceImpl<SysCmdTcpDao, SysCmdTcp> implements SysCmdTcpService {
    @Autowired
    SysCmdTcpDao sysCmdTcpMapper;

    //指令---------------------------------
    @Autowired
    private RedisUtil redisUtil;


    @Override
    public String convertTcp(String cmd, String value) {
        if (org.springframework.util.StringUtils.isEmpty(value)) {
            return null;
        }
        String key = RedisKey.TCP_CMD_PREFIX + cmd;
        List<TcpDescValue> cmdTcp = redisUtil.lGet(key, 0, -1);
        if (CollectionUtils.isEmpty(cmdTcp)) {
//                查询指令
            cmdTcp = getCmdTcp(cmd, value);
            redisUtil.lSet(key, cmdTcp);
            for (TcpDescValue tcpDescValue : cmdTcp) {
                String deviceItemValue = tcpDescValue.getDeviceItemValue();
                String tcpDescription = tcpDescValue.getTcpDescription();
                if (value.equals(deviceItemValue)) {
                    return tcpDescription;
                }
            }
        } else {
            for (TcpDescValue tcpDescValue : cmdTcp) {
                String deviceItemValue = tcpDescValue.getDeviceItemValue();
                String tcpDescription = tcpDescValue.getTcpDescription();
                if ("ERROR".equals(tcpDescription)) {
                    return value;
                }
                if (value.equals(deviceItemValue)) {
                    return tcpDescription;
                }
            }

        }
        return null;
    }

    @Override
    public Result<SysCmdTcp> add(String sub, AddAndUpdCmdTcpVo cmdTcpVo) {
        //指令名称和指令值去重判断
        String tcpName = cmdTcpVo.getTcpName();
        String tcpValue = cmdTcpVo.getTcpValue();
        List<SysCmdTcp> cmdTcps = sysCmdTcpMapper.selectList(new QueryWrapper<SysCmdTcp>().eq("tcp_name", tcpName));
        if (cmdTcps.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_20);
        }
        List<SysCmdTcp> sysCmdTcps = sysCmdTcpMapper.selectList(new QueryWrapper<SysCmdTcp>().eq("tcp_value", tcpValue));
        if (sysCmdTcps.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_21);
        }
        SysCmdTcp sysCmdTcp = new SysCmdTcp();
        sysCmdTcp.setTcpName(cmdTcpVo.getTcpName());
        sysCmdTcp.setTcpValue(cmdTcpVo.getTcpValue());
        sysCmdTcp.setTcpType(cmdTcpVo.getTcpType());
        sysCmdTcp.setTcpDescription(cmdTcpVo.getTcpDescription());
        sysCmdTcp.setDeviceType(cmdTcpVo.getDeviceType());
        sysCmdTcp.setCmdName(cmdTcpVo.getTcpName().toLowerCase());
        sysCmdTcpMapper.insert(sysCmdTcp);
        return new Result(CustomExceptionType.OK, sysCmdTcp);
    }

    @Transactional
    @Override
    public Result del(Integer id) {
        SysCmdTcp sysCmdTcp = sysCmdTcpMapper.selectById(id);
        if (sysCmdTcp == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_6);
        }
        if (sysCmdTcp.getCmdName() != null) {
            //删除指令子集
            sysCmdTcpMapper.delete(new QueryWrapper<SysCmdTcp>().eq("group_type", sysCmdTcp.getCmdName()));
        }
        //删除指令
        int i = sysCmdTcpMapper.deleteById(id);
        return new Result(CustomExceptionType.OK, Message.OK_2);
    }


    @Override
    public Result update(AddAndUpdCmdTcpVo cmdTcpVo) {
        if (cmdTcpVo.getId() == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_5);
        }
        SysCmdTcp sysCmdTcp = sysCmdTcpMapper.selectById(cmdTcpVo.getId());
        if (sysCmdTcp == null) {
            log.error("修改指令,id不存在:{}", cmdTcpVo.getId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_6);
        }
        if (!sysCmdTcp.getTcpName().equals(cmdTcpVo.getTcpName())) {
            List<SysCmdTcp> sysCmdTcps = sysCmdTcpMapper.selectList(new QueryWrapper<SysCmdTcp>().eq("tcp_name", cmdTcpVo.getTcpName()));
            if (sysCmdTcps.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_20);
            }
        }
        if (!sysCmdTcp.getTcpValue().equals(cmdTcpVo.getTcpValue())) {
            List<SysCmdTcp> sysCmdTcps = sysCmdTcpMapper.selectList(new QueryWrapper<SysCmdTcp>().eq("tcp_value", cmdTcpVo.getTcpValue()));
            if (sysCmdTcps.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_21);
            }
        }
        BeanUtils.copyProperties(cmdTcpVo, sysCmdTcp);
        sysCmdTcp.setCmdName(cmdTcpVo.getTcpName().toLowerCase());
        int insert = sysCmdTcpMapper.updateById(sysCmdTcp);
        if (insert > 0) {
            return new Result(CustomExceptionType.OK, sysCmdTcp);
        }
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_3);
    }

    @Override
    public Result getById(Integer id) {
        SysCmdTcp sysCmdTcp = sysCmdTcpMapper.selectById(id);
        return new Result(CustomExceptionType.OK, sysCmdTcp);
    }

    //指令值---------------------------------
    @Override
    public Result<SysCmdTcp> listItem(PageLimit pageLimit, String id) {
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        String field = !StringUtils.isEmpty(pageLimit.getField()) ? UnderlineTool.humpToLine(pageLimit.getField()) : pageLimit.getField();

        List<SysCmdTcpItemVo> list = sysCmdTcpMapper.listItem(field, pageLimit.getType(), id);
        PageInfo<SysCmdTcpItemVo> info = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Override
    public Result<SysCmdTcp> addItem(CmdTcpItemVo cmdTcpItemVo) {
        SysCmdTcp sysCmdTcp = sysCmdTcpMapper.selectById(cmdTcpItemVo.getId());
        if (sysCmdTcp == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_6);
        }
        Integer size = sysCmdTcpMapper.examine(sysCmdTcp.getCmdName(), cmdTcpItemVo.getDeviceItemValue(), cmdTcpItemVo.getTcpDescription());
        if (size > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_22);
        }
        SysCmdTcp item = new SysCmdTcp();
        item.setDeviceItemValue(cmdTcpItemVo.getDeviceItemValue());
        item.setTcpDescription(cmdTcpItemVo.getTcpDescription());
        item.setGroupType(sysCmdTcp.getCmdName());
        sysCmdTcpMapper.insert(item);
        String key = RedisKey.UNKNOWN_TCP + sysCmdTcp.getTcpValue() + cmdTcpItemVo.getDeviceItemValue();
        Object o = redisUtil.get(key);
        if (o != null && o.toString().equals(RedisKey.SELECT_MYSQL_TRUE)) {
            //如若redis标识了这条指令 为未知数据，现在添加了要解除指令为已知数据，需要去数据查询一遍
            redisUtil.set(key, RedisKey.SELECT_MYSQL_FALSE);
            //添加新指令到缓存
            redisUtil.set(RedisKey.TCP_CMD_PREFIX + sysCmdTcp.getTcpValue() + cmdTcpItemVo.getDeviceItemValue(), cmdTcpItemVo.getTcpDescription());
        }
        //刷新指令到redis
        this.selectCmdTcpToRedis();
        return new Result(CustomExceptionType.OK, item);
    }

    @Override
    public Result delItem(String id) {
        sysCmdTcpMapper.deleteById(id);
        //刷新指令到redis
        this.selectCmdTcpToRedis();
        return new Result(CustomExceptionType.OK, Message.OK_2);
    }

    @Override
    public Result<SysCmdTcp> updateItem(CmdTcpItemVo cmdTcpItemVo) {
        SysCmdTcp sysCmdTcp = sysCmdTcpMapper.selectById(cmdTcpItemVo.getId());
        if (sysCmdTcp == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_6);
        }
        if (!sysCmdTcp.getDeviceItemValue().equals(cmdTcpItemVo.getDeviceItemValue())) {
            List<SysCmdTcp> sysCmdTcps = sysCmdTcpMapper.selectList(new QueryWrapper<SysCmdTcp>().eq("group_type", sysCmdTcp.getGroupType()).eq("device_item_value", cmdTcpItemVo.getDeviceItemValue()));
            if (sysCmdTcps.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_23);
            }
        }
        if (!sysCmdTcp.getTcpDescription().equals(cmdTcpItemVo.getTcpDescription())) {
            List<SysCmdTcp> sysCmdTcps = sysCmdTcpMapper.selectList(new QueryWrapper<SysCmdTcp>().eq("group_type", sysCmdTcp.getGroupType()).eq("tcp_description", cmdTcpItemVo.getTcpDescription()));
            if (sysCmdTcps.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_24);
            }
        }
        sysCmdTcp.setDeviceItemValue(cmdTcpItemVo.getDeviceItemValue());
        sysCmdTcp.setTcpDescription(cmdTcpItemVo.getTcpDescription());
        sysCmdTcpMapper.updateById(sysCmdTcp);
        //刷新指令到redis
        this.selectCmdTcpToRedis();
        return new Result(CustomExceptionType.OK, sysCmdTcp);
    }

    @Override
    public Result<SysCmdTcp> getByIdItem(String id) {
        SysCmdTcp byId = this.getById(id);
        return Result.ok(byId);
    }

    @Override
    public Result<List<SysCmdTcp>> list(PageLimitBase pageLimit, String tcpName, String tcpValue, Integer tcpType, String tcpDescription, Integer deviceType) {
        QueryWrapper<SysCmdTcp> wrapper = new QueryWrapper<>();
        if (tcpType != null) {
            wrapper.like("tcp_type", tcpType);
        }
        if (tcpName != null) {
            wrapper.like("tcp_name", tcpName);
        }
        if (deviceType != null) {
            wrapper.like("device_type", deviceType);
        }
        if (tcpValue != null && !tcpValue.trim().equals("")) {
            wrapper.like("tcp_value", tcpValue.trim());
        }
        if (tcpDescription != null && !tcpDescription.trim().equals("")) {
            wrapper.like("tcp_description", tcpDescription.trim());
        }
        wrapper.isNotNull("tcp_value");
        if (!StringUtils.isEmpty(pageLimit.getType())) {
            if ("DESC".equals(pageLimit.getType())) {
                wrapper.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if ("ASC".equals(pageLimit.getType())) {
                wrapper.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            }
        }
        if(pageLimit.getPage() != -1){
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        List<SysCmdTcp> sysCmdTcps = sysCmdTcpMapper.selectList(wrapper);
        PageInfo<SysCmdTcp> info = new PageInfo<>(sysCmdTcps);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }


    /**
     * 查询所有指令缓存到redis
     *
     * @return
     */
    public void selectCmdTcpToRedis() {
        List<CmdTcp> cmdTcpList = sysCmdTcpMapper.getCmdTcpList();
        for (CmdTcp ctp : cmdTcpList) {
            try {
                redisUtil.set(ctp.getTcpValue() + ctp.getDeviceItemValue(), ctp.getTcpDescription());
                log.info("指令存储ok------:{}", ctp.toString());
            } catch (Exception e) {
                log.error("初始化指令到redis失败:{}", ctp.toString());
            }

        }
    }

    @Override
    public List<TcpDescValue> getCmdTcp(String cmd, String cmdValue) {
        String cmdName = sysCmdTcpMapper.getTcpValue(cmd);
        if (org.springframework.util.StringUtils.isEmpty(cmdName)) {
            log.warn(" 查询指令不存在:cmd: {},cmdName：{}", cmd, cmdName);
            List<TcpDescValue> objects = new ArrayList<>();
            TcpDescValue descValue = new TcpDescValue();
            descValue.setDeviceItemValue("ERROR");
            descValue.setTcpDescription("ERROR");
            objects.add(descValue);
            return objects;
        }
        List<TcpDescValue> descValue = sysCmdTcpMapper.getGroupType(cmdName);
        if (CollectionUtils.isEmpty(descValue)) {
            log.warn("cmdName:{} 查询指令不存在:descValue: {}", cmdName, descValue);
            List<TcpDescValue> objects = new ArrayList<>();
            TcpDescValue descValueX = new TcpDescValue();
            descValueX.setDeviceItemValue("ERROR");
            descValueX.setTcpDescription("ERROR");
            objects.add(descValueX);
            return objects;
        }
        return descValue;
    }
}
