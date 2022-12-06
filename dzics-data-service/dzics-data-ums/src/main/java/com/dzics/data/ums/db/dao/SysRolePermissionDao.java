package com.dzics.data.ums.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.ums.model.entity.SysPermission;
import com.dzics.data.ums.model.entity.SysRolePermission;
import com.dzics.data.common.base.mybatis.DzicsBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * <p>
 * 角色权限表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Mapper
public interface SysRolePermissionDao extends DzicsBaseMapper<SysRolePermission> {

    @Cacheable(cacheNames = "rolePermissionService.listRolePermissionCode", key = "#username+#useOrgCode")
    List<String> listRolePermissionCode(@Param("list") List<String> list, String username, String useOrgCode);

    @Cacheable(cacheNames = "rolePermissionService.listRolePermission", key = "#joinKey")
    List<SysPermission> listRolePermission(List<String> list, String joinKey);

    default Integer selectByPerId(Long delPermission) {
        QueryWrapper<SysRolePermission> wpRoPermi = new QueryWrapper<>();
        wpRoPermi.eq("permission_id",delPermission);
        return selectCount(wpRoPermi);

    }
}
