package com.dzics.data.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.ums.model.entity.SysPermission;
import com.dzics.data.ums.model.entity.SysRolePermission;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * <p>
 * 角色权限表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
public interface SysRolePermissionService extends IService<SysRolePermission> {

    /**
     * 根据用户角色id 获取对应的权限
     *
     * @param collect    角色id
     * @param username
     * @param useOrgCode
     * @return
     */
    @Cacheable(cacheNames = "rolePermissionService.listRolePermissionCode", key = "#username+#useOrgCode")
    List<String> listRolePermissionCode(List<String> collect, String username, String useOrgCode);

    @Cacheable(cacheNames = "rolePermissionService.listRolePermission", key = "#joinKey")
    List<SysPermission> listRolePermission(List<String> collect, String joinKey);


    /**
     * @param roleId 根据角色id删除关系表
     */
    void removeRoleId(String roleId);

    Integer selectByPerId(Long delPermission);
}
