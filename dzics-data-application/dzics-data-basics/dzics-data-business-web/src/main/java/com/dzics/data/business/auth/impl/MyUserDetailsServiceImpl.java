package com.dzics.data.business.auth.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.ums.model.entity.SysRole;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import com.dzics.data.ums.service.SysRolePermissionService;
import com.dzics.data.ums.service.SysUserRoleService;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.redis.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings(value = "ALL")
public class MyUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    @Lazy
    private DzicsUserService sysUserServiceDao;
    @Autowired
    @Lazy
    private RedisUtil redisUtilSetValue;

    @Autowired
    @Lazy
    private SysUserRoleService userRoleService;
    @Autowired
    @Lazy
    private SysRolePermissionService rolePermissionService;

    /**
     * 登录是查询账号，权限验证是查询账号
     *
     * @param username
     * @return
     */
    @Override
    public MyUserDetailsImpl loadUserByUsername(String username) {
        //加载基础用户信息
        SysUser sysUser = sysUserServiceDao.getByUserName(username);
        MyUserDetailsImpl details = new MyUserDetailsImpl();
        details.setUsername(sysUser.getUsername());
        details.setAccountNonExpired(sysUser.getStatus().intValue() == 1 ? true : false);
        details.setAccountNonLocked(sysUser.getStatus().intValue() == 1 ? true : false);
        details.setCredentialsNonExpired(sysUser.getStatus().intValue() == 1 ? true : false);
        details.setEnabled(sysUser.getStatus().intValue() == 1 ? true : false);
        details.setPassword(sysUser.getPassword());
        details.setSecret(sysUser.getSecret());
        details.setCode(sysUser.getCode());
        details.setRefSecret(sysUser.getRefSecret());
        List<SysRole> roleList = userRoleService.listRoleCode(sysUser.getId(), sysUser.getUseOrgCode(), sysUser.getUsername());
        List<String> roleCodes = roleList.stream().map(v -> v.getRoleCode()).collect(Collectors.toList());
        List<String> authorties = new ArrayList<>();
        if (!roleList.isEmpty()) {
            List<String> roleIds = roleList.stream().map(v -> v.getRoleId()).collect(Collectors.toList());
            authorties = rolePermissionService.listRolePermissionCode(roleIds, sysUser.getUsername(), sysUser.getUseOrgCode());
        }
        //角色是一个特殊的权限，ROLE_前缀
        roleCodes = roleCodes.stream().map(rc -> "ROLE_" + rc).collect(Collectors.toList());
        authorties.addAll(roleCodes);
        details.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", authorties)));
        return details;
    }

    @CacheEvict(value = "sysUserService.getByUserName", key = "#username")
    public boolean updateSercity(String sub, String secrity, String refSecrity) {
        QueryWrapper<SysUser> accountUserQueryWrapper = new QueryWrapper<>();
        accountUserQueryWrapper.eq("username", sub);
        SysUser accountUser = new SysUser();
        accountUser.setSecret(secrity);
        if (!StringUtils.isEmpty(refSecrity)) {
            accountUser.setRefSecret(refSecrity);
        }
        boolean update = sysUserServiceDao.update(accountUser, accountUserQueryWrapper);
        redisUtilSetValue.del(RedisKey.USER_NAME_AND_USER_TYPE + sub);
        redisUtilSetValue.del(RedisKey.USERPERSIONPFXKEY + sub);
        return update;
    }

    @CacheEvict(value = "sysUserService.getByUserName", key = "#username")
    public boolean updateSercityRef(String username, String refSecret, String secret, String successToken) {
        QueryWrapper<SysUser> accountUserQueryWrapper = new QueryWrapper<>();
        accountUserQueryWrapper.eq("username", username);
        SysUser accountUser = new SysUser();
        accountUser.setSecret(secret);
        accountUser.setRefSecret(refSecret);
        boolean update = sysUserServiceDao.update(accountUser, accountUserQueryWrapper);
        redisUtilSetValue.del(RedisKey.USER_NAME_AND_USER_TYPE + username);
        redisUtilSetValue.del(RedisKey.USERPERSIONPFXKEY + username);
        return update;
    }
}
