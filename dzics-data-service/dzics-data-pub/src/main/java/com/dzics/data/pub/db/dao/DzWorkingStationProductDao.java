package com.dzics.data.pub.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pub.model.dao.LocationArtifactsDo;
import com.dzics.data.pub.model.entity.DzWorkingStationProduct;
import com.dzics.data.pub.model.vo.locationartifacts.LocationArtifactsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 工位-工件关联关系表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-28
 */
@Mapper
public interface DzWorkingStationProductDao extends BaseMapper<DzWorkingStationProduct> {


    List<LocationArtifactsDo> locationArtifactsList(LocationArtifactsVo locationArtifactsVo);
}
