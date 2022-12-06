package com.dzics.data.appoint.changsha.mom.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.appoint.changsha.mom.model.dto.CallMaterial;
import com.dzics.data.appoint.changsha.mom.model.entity.MomWaitCallMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 等待叫料的订单 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-10
 */
@Mapper
@Repository
public interface MomWaitCallMaterialDao extends BaseMapper<MomWaitCallMaterial> {

    List<CallMaterial> getWorkStation(@Param("proTaskId") String proTaskId, @Param("stationCode") String stationCode);

}
