package com.dzics.data.appoint.changsha.mom.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrderPathMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 工序物料表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
@Mapper
@Repository
public interface MomOrderPathMaterialDao extends BaseMapper<MomOrderPathMaterial> {

}
