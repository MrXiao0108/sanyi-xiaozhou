package com.dzics.data.appoint.changsha.mom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.dto.IssueOrderInformation;
import com.dzics.data.appoint.changsha.mom.model.dto.mom.OprSequence;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrderPath;
import com.dzics.data.appoint.changsha.mom.model.vo.ResultDto;

import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * 订单工序组工序组 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
public interface MomOrderPathService extends IService<MomOrderPath> {

    /**
     * 根据订单主键 获取 获取工序信息
     *
     * @param proTaskOrderId Mom订单ID
     * @return MomOrderPath
     */
    MomOrderPath getproTaskOrderId(String proTaskOrderId);

    /**
     * 保存订单工序信息
     *
     * @param task：json转实体类
     * @param msg：json原数据
     * @return ResultDto
     */
    ResultDto saveOrderOprSequence(IssueOrderInformation<OprSequence> task,String msg);

    /**
     * wipOrderNoMap
     *
     * @param coll: wipOrderNo 集合
     * @return 目标数据
     * @author van
     * @date 2022/8/18
     */
    Map<String, MomOrderPath> wipOrderNoMapByWipOrderNo(Collection<String> coll);

    /**
     * 获取当前岛内最新订单的工序信息
     *
     * @param orderId：订单ID
     * @return MomOrderPath
     */
    MomOrderPath getNewByMomOrder(String orderId);
}
