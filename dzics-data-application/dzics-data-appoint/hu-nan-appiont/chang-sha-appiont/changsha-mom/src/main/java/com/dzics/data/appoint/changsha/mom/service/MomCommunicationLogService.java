package com.dzics.data.appoint.changsha.mom.service;

import com.dzics.data.appoint.changsha.mom.model.entity.MomCommunicationLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.vo.MomCommunicationLogVo;
import com.dzics.data.common.base.vo.Result;

import java.util.List;

/**
 * <p>
 * MOM通讯日志 服务类
 * </p>
 *
 * @author van
 * @since 2022-08-19
 */
public interface MomCommunicationLogService extends IService<MomCommunicationLog> {

    /**
     * 新增日志
     *
     * @param type:            通讯类型（1：mom下发，2：请求mom)
     * @param businessType:    业务类型
     * @param resultState:     处理结果
     * @param requestContent:  请求内容
     * @param responseContent: 响应内容
     * @author van
     * @date 2022/8/19
     */
    void addForMQ(String type, String businessType, String resultState, String requestContent, String responseContent);

    /**
     * 新增处理
     *
     * @param msg: 消息
     * @author van
     * @date 2022/8/19
     */
    void addHandle(String msg);

    /**
     * 分页查询
     *
     * @param vo: 查询条件
     * @return 查询结果
     * @author van
     * @date 2022/8/19
     */
    Result<List<MomCommunicationLog>> page(MomCommunicationLogVo vo);
}
