package com.dzics.data.tool.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.tool.model.entity.DzToolGroups;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 刀具组表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Mapper
public interface DzToolGroupsDao extends BaseMapper<DzToolGroups> {

    List<DzToolGroups> getToolGroupsList(@Param("field") String field, @Param("type") String type, @Param("groupNo") String groupNo, @Param("useOrgCode") String useOrgCode);

    long getToolGroupsList_COUNT(@Param("field") String field, @Param("type") String type, @Param("groupNo") String groupNo);
}
