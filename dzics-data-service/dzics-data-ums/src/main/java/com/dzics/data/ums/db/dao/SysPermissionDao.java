package com.dzics.data.ums.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.ums.model.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Mapper
public interface SysPermissionDao extends BaseMapper<SysPermission> {

    SysPermission selPermissionCode(@Param("perms") String perms);
}
