package com.dzics.data.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.ums.model.entity.SysPermission;
import com.dzics.data.ums.model.entity.SysRolePermission;
import com.dzics.data.ums.service.SysRolePermissionService;
import com.dzics.data.ums.db.dao.SysRolePermissionDao;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色权限表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionDao, SysRolePermission> implements SysRolePermissionService {


    @Override
    public List<String> listRolePermissionCode(List<String> collect, String username, String useOrgCode) {
        return baseMapper.listRolePermissionCode(collect,username,useOrgCode);
    }

    @Override
    public List<SysPermission> listRolePermission(List<String> collect, String joinKey) {
        return baseMapper.listRolePermission(collect,joinKey);
    }

    @Override
    public void removeRoleId(String roleId) {
        QueryWrapper<SysRolePermission> wpRoPermi = new QueryWrapper<>();
        wpRoPermi.eq("role_id",roleId);
        baseMapper.delete(wpRoPermi);
    }

    @Override
    public Integer selectByPerId(Long delPermission) {
        QueryWrapper<SysRolePermission> wpRoPermi = new QueryWrapper<>();
        wpRoPermi.eq("permission_id",delPermission);
        return baseMapper.selectCount(wpRoPermi);

    }


}
