package com.dzics.data.tool.db.dao;

import com.dzics.data.common.base.mybatis.DzicsBaseMapper;
import com.dzics.data.tool.model.entity.DzToolInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 刀具表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Mapper
public interface DzToolInfoDao extends DzicsBaseMapper<DzToolInfo> {

    /**
     * 根据设备id和刀具组编号  查询未绑定该设备的刀具
     * @param equipmentId
     * @param groupNo
     * @return
     */
    List<DzToolInfo> getToolByEqIdAndGroupNo(@Param("equipmentId") Long equipmentId,
                                             @Param("groupNo") Integer groupNo,
                                             @Param("toolGroupsId")Long toolGroupsId);
}
