package com.dzics.data.appoint.changsha.mom.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.appoint.changsha.mom.model.entity.MomWorkReportSort;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2022-05-30
 */
@Mapper
public interface MomWorkReportSortDao extends BaseMapper<MomWorkReportSort> {

    default MomWorkReportSort getByOrderId(Long orderId, Long lineId) {
        PageHelper.startPage(1, 1);
        QueryWrapper<MomWorkReportSort> wp = new QueryWrapper<>();
        wp.eq("order_id", orderId);
        wp.eq("line_id", lineId);
        wp.orderByAsc("create_time");
        List<MomWorkReportSort> dzWorkReportSorts = selectList(wp);
        PageInfo<MomWorkReportSort> info = new PageInfo<>(dzWorkReportSorts);
        if (info.getTotal() > 0) {
            return info.getList().get(0);
        }
        return null;
    }

    default void deleteByProTaskId(String proTaskOrderId) {
        QueryWrapper<MomWorkReportSort> wp = new QueryWrapper<>();
        wp.eq("pro_task_order_id", proTaskOrderId);
        delete(wp);
    }
}
