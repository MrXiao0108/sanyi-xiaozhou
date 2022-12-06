package com.dzics.data.appoint.changsha.mom.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrderPath;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 订单工序组工序组 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
@Repository
@Mapper
public interface MomOrderPathDao extends BaseMapper<MomOrderPath> {

    default MomOrderPath getProTaskOrderPath(String proTaskOrderId) {
        QueryWrapper<MomOrderPath> wp = new QueryWrapper<>();
        wp.eq("mom_order_id", proTaskOrderId);
        MomOrderPath paths = selectOne(wp);
        return paths;
    }

    /**
     * 处理订单转发时，查询最新当前岛工位信息，以便订单转发进行工位变更
     *
     * @param orderId
     * @return MomOrderPath
     */
    MomOrderPath getNewByMomOrder(@Param("orderId")String orderId);

    /**
     * 是否有正在进行中、暂停中、变更中的订单
     *
     * @param lineId：产线ID
     * @param stations
     * @return int
     */
    int getIsRunning(@Param("lineId") String lineId, @Param("stations")List<String>stations);
}
