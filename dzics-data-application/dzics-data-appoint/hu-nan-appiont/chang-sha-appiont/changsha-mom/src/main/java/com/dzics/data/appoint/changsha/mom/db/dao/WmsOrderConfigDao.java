package com.dzics.data.appoint.changsha.mom.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.GetOrderCfig;
import com.dzics.data.appoint.changsha.mom.model.entity.WmsOrderConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-07
 */
@Mapper
public interface WmsOrderConfigDao extends BaseMapper<WmsOrderConfig> {

    List<GetOrderCfig> getCfg(@Param("field") String field, @Param("type") String type);
}
