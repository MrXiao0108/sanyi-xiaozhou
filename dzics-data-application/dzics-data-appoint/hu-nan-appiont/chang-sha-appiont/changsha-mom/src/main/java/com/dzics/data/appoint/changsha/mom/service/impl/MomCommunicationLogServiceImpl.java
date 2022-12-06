package com.dzics.data.appoint.changsha.mom.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.config.mq.MOMConfig;
import com.dzics.data.appoint.changsha.mom.db.dao.MomCommunicationLogDao;
import com.dzics.data.appoint.changsha.mom.model.entity.MomCommunicationLog;
import com.dzics.data.appoint.changsha.mom.model.vo.MomCommunicationLogVo;
import com.dzics.data.appoint.changsha.mom.mq.RabbitMQService;
import com.dzics.data.appoint.changsha.mom.service.MomCommunicationLogService;
import com.dzics.data.common.base.vo.Result;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * MOM通讯日志 服务实现类
 * </p>
 *
 * @author van
 * @since 2022-08-19
 */
@Slf4j
@Service
public class MomCommunicationLogServiceImpl extends ServiceImpl<MomCommunicationLogDao, MomCommunicationLog> implements MomCommunicationLogService {

    @Autowired
    private RabbitMQService rabbitMQService;

    @Override
    public void addForMQ(String type, String businessType, String resultState, String requestContent, String responseContent) {
        MomCommunicationLog model = new MomCommunicationLog();
        model.setType(type);
        model.setBusinessType(businessType);
        model.setResultState(resultState);
        model.setRequestContent(requestContent);
        model.setResponseContent(responseContent);
        rabbitMQService.sendMsg(MOMConfig.TOPIC_EXCHANGE, MOMConfig.LOG_COMMUNICATION_ROUTING, JSONUtil.toJsonStr(model));
    }

    @Override
    public void addHandle(String msg) {
        MomCommunicationLog momCommunicationLog = JSONUtil.toBean(msg, MomCommunicationLog.class);
        this.save(momCommunicationLog);
        log.info("MomCommunicationLogServiceImpl [addHandle] add{}", JSONUtil.toJsonStr(momCommunicationLog));
    }

    @Override
    public Result<List<MomCommunicationLog>> page(MomCommunicationLogVo vo) {
        PageHelper.offsetPage(vo.getPage(), vo.getLimit());
        LambdaQueryWrapper<MomCommunicationLog> query = Wrappers.<MomCommunicationLog>lambdaQuery();
        if (StringUtils.hasText(vo.getBusinessType())) {
            query.eq(MomCommunicationLog::getBusinessType, vo.getBusinessType());
        }
        if (StringUtils.hasText(vo.getRequestContent())) {
            query.like(MomCommunicationLog::getRequestContent, vo.getRequestContent());
        }
        query.orderByDesc(MomCommunicationLog::getCreateTime);
        List<MomCommunicationLog> list = this.list(query);
        return Result.ok(list);
    }
}
