package com.dzics.data.wrp.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.wrp.model.dao.WorkingFlowRes;
import com.dzics.data.wrp.model.entity.DzWorkingFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 工件制作流程记录 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-19
 */
@Mapper
@Repository
public interface DzWorkingFlowDao extends BaseMapper<DzWorkingFlow> {

    List<String> getWorkingFlowBigQrCode(@Param("orderId") String orderId, @Param("lineId") String lineId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<WorkingFlowRes> getWorkingFlow(@Param("list") List<String> list, @Param("orderId") String orderId, @Param("lineId") String lineId);
}
