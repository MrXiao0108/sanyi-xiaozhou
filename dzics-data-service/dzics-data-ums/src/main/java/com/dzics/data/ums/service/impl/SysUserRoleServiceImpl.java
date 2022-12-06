package com.dzics.data.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.ums.model.entity.SysRole;
import com.dzics.data.ums.model.entity.SysUserRole;
import com.dzics.data.ums.service.SysUserRoleService;
import com.dzics.data.common.base.enums.BasicsRole;
import com.dzics.data.ums.db.dao.SysRoleDao;
import com.dzics.data.ums.db.dao.SysUserRoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@SuppressWarnings(value = "ALL")
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleDao, SysUserRole> implements SysUserRoleService {

    @Autowired
    private SysUserRoleDao userRoleDao;
    @Autowired
    private SysRoleDao sysRoleDao;

    @Override
    public List<SysRole> listRoleCode(String id, String useOrgCode, String username) {
        return userRoleDao.getListRoleCode(id, useOrgCode);
    }

    @Override
    public List<String> listRoleId(Long id, String useOrgCode, Integer code) {
        return userRoleDao.listRoleId(id, useOrgCode,code);
    }

    @Override
    public List<SysUserRole> listRoles(Long id) {
        QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        return userRoleDao.selectList(queryWrapper);

    }

    @Override
    public List<String> getRoleId(Long userId) {
        QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<SysUserRole> userRoles = userRoleDao.selectList(queryWrapper);
        return userRoles.stream().map(usro -> usro.getRoleId()).collect(Collectors.toList());
    }

    @Override
    public List<SysRole> listOrgCodeBasicsRole(String useOrgCode) {
        QueryWrapper<SysRole> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper.eq("org_code", useOrgCode);
        roleQueryWrapper.eq("basics_role", BasicsRole.JC.getCode());
        roleQueryWrapper.eq("status",1 );
        return sysRoleDao.selectList(roleQueryWrapper);
    }

    @Override
    public void removeUserId(Long delUser) {
        QueryWrapper<SysUserRole> wpUsRo = new QueryWrapper<>();
        wpUsRo.eq("user_id", delUser);
        userRoleDao.delete(wpUsRo);
    }

    @Override
    public Integer countRoleUser(Long delRole) {
        QueryWrapper<SysUserRole> wpUsRo = new QueryWrapper<>();
        wpUsRo.eq("role_id", delRole);
        return userRoleDao.selectCount(wpUsRo);

    }

}
