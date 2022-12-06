package com.dzics.data.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.enums.*;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.CustomWarnException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.model.constant.ImgHeadBase64;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.StringToUpcase;
import com.dzics.data.common.util.UnderlineTool;
import com.dzics.data.common.util.md5.Md5Util;
import com.dzics.data.redis.util.RedisUtil;
import com.dzics.data.ums.db.dao.*;
import com.dzics.data.ums.model.dao.SwitchSiteDo;
import com.dzics.data.ums.model.dto.depart.ResSysDepart;
import com.dzics.data.ums.model.dto.depart.SwitchSite;
import com.dzics.data.ums.model.dto.permission.AddPermission;
import com.dzics.data.ums.model.dto.permission.UpdatePermission;
import com.dzics.data.ums.model.dto.role.AddRole;
import com.dzics.data.ums.model.dto.role.DisableEnabledRole;
import com.dzics.data.ums.model.dto.role.SelRole;
import com.dzics.data.ums.model.dto.role.UpdateRole;
import com.dzics.data.ums.model.dto.user.*;
import com.dzics.data.ums.model.entity.*;
import com.dzics.data.ums.model.vo.depart.ResDepart;
import com.dzics.data.ums.model.vo.role.ResSysRole;
import com.dzics.data.ums.model.vo.router.MenusInfo;
import com.dzics.data.ums.model.vo.router.RoutersInfo;
import com.dzics.data.ums.model.vo.user.UserInfo;
import com.dzics.data.ums.model.vo.user.UserListRes;
import com.dzics.data.ums.model.vo.user.UserMessage;
import com.dzics.data.ums.service.AuthRoleComService;
import com.dzics.data.ums.service.DzicsUserService;
import com.dzics.data.ums.util.ChildrenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Service
@Slf4j
@SuppressWarnings(value = "ALL")
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements DzicsUserService {

    @Autowired
    private AuthRoleComService authRoleCommon;
    @Autowired
    private SysRoleDao roleDao;
    @Autowired
    private SysDepartDao departDao;
    @Autowired
    private SysUserDepartDao userDepartDao;
    @Autowired
    private SysUserRoleDao userRoleDao;
    @Autowired
    private SysDepartPermissionDao departPermissionDao;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysRolePermissionDao rolePermissionDao;
    @Autowired
    private SysPermissionDao permissionDao;

    @Override
    public SysUser getByUserName(String sub) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>();
        queryWrapper.eq("username", sub);
        SysUser one = this.getOne(queryWrapper);
        if (one == null) {
            throw new CustomException(CustomExceptionType.USER_IS_NULL, CustomExceptionType.USER_IS_NULL.getTypeDesc() + ":" + sub);
        }
        String useOrgCode = one.getUseOrgCode();
        SysDepart byId = departDao.selectById(one.getAffiliationDepartId());
        if (StringUtils.isEmpty(useOrgCode)) {
            useOrgCode = byId.getOrgCode();
            one.setUseOrgCode(useOrgCode);
        }
//        归属站点编码
        one.setCode(byId.getOrgCode());
//        当前活动站点编码
        SysDepart byCode = departDao.getByCode(useOrgCode);
        one.setUseDepartId(byCode.getId());
        return one;
    }


    @Override
    public List<UserListRes> listUserOrgCode(String field, String type, String useOrgCode, String realname, String username, Integer status, Date createTime, Date endTime) {
        if (!StringUtils.isEmpty(field)) {
            field = UnderlineTool.humpToLine(field);
        }
        return baseMapper.listUserOrgCode(field, type, useOrgCode, realname, username, status, createTime, endTime);
    }

    @Override
    public Result put(String sub, PutUserInfoVo putUserInfoVo) {
        SysUser byUserName = getByUserName(sub);
        byUserName.setRealname(putUserInfoVo.getRealname());
        byUserName.setPhone(putUserInfoVo.getPhone());
        byUserName.setEmail(putUserInfoVo.getRealname());
        byUserName.setSex(putUserInfoVo.getSex());
        this.updateById(byUserName);
        return new Result(CustomExceptionType.OK, byUserName);
    }


    @Override
    public String getUserRoleName(Long id) {
        //查询用户角色
        List<String> roleNameList = roleDao.getRoleName(id);
        if (roleNameList.size() > 0) {
            String join = StringUtils.join(roleNameList, ',');
            return join;
        }
        return null;
    }

    @Override
    public Long listUsername(String username) {
        return baseMapper.listUsername(username);
    }

    public static boolean isImage(InputStream imageFile) {
        Image img = null;
        try {
            img = ImageIO.read(imageFile);
            if (img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            img = null;
        }
    }

    @Override
    public String getUserOrgCode(String sub) {
        SysUser byUserName = getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            return null;
        } else {
            return byUserName.getUseOrgCode();
        }

    }


    /**
     * 对字节数组字符串进行Base64解码并生成图片
     *
     * @param imgStr
     * @param imgFilePath
     * @return
     */
    public static boolean GenerateImage(String imgStr, String imgFilePath) {
        OutputStream out = null;
        if (imgStr == null) {
            return false;
        }

        try {
            byte[] bytes = Base64.getDecoder().decode(imgStr);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {
                    bytes[i] += 256;
                }
            }
            out = new FileOutputStream(imgFilePath);
            out.write(bytes);
            out.flush();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("管理文件流异常：{}", e.getMessage(), e);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Result addUser(RegisterVo registerVo, String sub) {
        Long count = listUsername(registerVo.getUsername());
        if (count > 0) {
            throw new CustomWarnException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR53);
        } else {
            SysUser byUserName = getByUserName(sub);
//            保存用户
            registerVo.setPassword(Md5Util.md5(registerVo.getPassword()));
            SysUser sysUser = new SysUser();
            BeanUtils.copyProperties(registerVo, sysUser);
            sysUser.setDelFlag(false);
            sysUser.setStatus(1);
            sysUser.setCreateBy(sub);
            sysUser.setOrgCode(byUserName.getUseOrgCode());
//            校验添加用户类型
            if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue()) {
                if (byUserName.getOrgCode().equals(byUserName.getUseOrgCode())) {
                    if (registerVo.getUserIdentity() == null || registerVo.getUserIdentity().intValue() == UserIdentityEnum.ORTER.getCode()) {
                        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR22);
                    }
                } else {
                    registerVo.setUserIdentity(UserIdentityEnum.ORTER.getCode());
                }
            }
//         设置校验添加用户归属站点
            if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue()) {
                if (registerVo.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode()) {
                    ResSysDepart sysDepart = departDao.getByParentId(0);
                    if (sysDepart == null) {
                        log.error("添加用户查询归属站点的父id 是 0的站点不存在");
                        throw new CustomException(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR0);
                    }
                    registerVo.setAffiliationDepartId(sysDepart.getId());
                    sysUser.setAffiliationDepartId(sysDepart.getId());
                    sysUser.setUseOrgCode(sysDepart.getOrgCode());
                } else if (registerVo.getUserIdentity().intValue() == UserIdentityEnum.DEPART.getCode()) {
                    if (registerVo.getAffiliationDepartId() == null) {
                        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR21);
                    }
                } else if (registerVo.getUserIdentity().intValue() == UserIdentityEnum.ORTER.getCode()) {
                    SysDepart byCode = departDao.getByCode(byUserName.getUseOrgCode());
                    registerVo.setAffiliationDepartId(String.valueOf(byCode.getId()));
                    sysUser.setAffiliationDepartId(byCode.getId());
                    sysUser.setUseOrgCode(byCode.getOrgCode());
                }
            } else {
                registerVo.setAffiliationDepartId(String.valueOf(byUserName.getAffiliationDepartId()));
                sysUser.setAffiliationDepartId(byUserName.getAffiliationDepartId());
                sysUser.setUseOrgCode(byUserName.getOrgCode());
            }
            sysUser.setAvatar(ImgHeadBase64.AVATAR);
            save(sysUser);
//            保存关联站点关系
            if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue()) {
                if (registerVo.getUserIdentity().intValue() == UserIdentityEnum.DEPART.getCode()) {
                    registerVo.setDepartId(Arrays.asList(registerVo.getAffiliationDepartId()));
                } else if (registerVo.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode()) {
                    QueryWrapper<SysDepart> wp = new QueryWrapper<>();
                    wp.eq("parent_id", 0);
                    SysDepart sysUserDepart = departDao.selectOne(wp);
                    if (CollectionUtils.isEmpty(registerVo.getDepartId())) {
                        registerVo.setDepartId(Arrays.asList(sysUserDepart.getId()));
                    } else {
                        registerVo.getDepartId().add(sysUserDepart.getId());
                    }
                } else {
                    SysDepart byCode = departDao.getByCode(byUserName.getUseOrgCode());
                    registerVo.setDepartId(Arrays.asList(String.valueOf(byCode.getId())));
                }
                List<SysUserDepart> userDeparts = registerVo.getDepartId().stream().map(depgl -> new SysUserDepart(sysUser.getId(), depgl, byUserName.getUseOrgCode(), false, byUserName.getUsername())).collect(Collectors.toList());
                userDepartDao.insertBatchSomeColumn(userDeparts);
            } else {
                SysUserDepart sysUserDepart = new SysUserDepart(sysUser.getId(), registerVo.getAffiliationDepartId(), byUserName.getUseOrgCode(), false, byUserName.getUsername());
                userDepartDao.insert(sysUserDepart);
            }
//            保存角色信息
            if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue()) {
//              大正用户新建用户
                if (registerVo.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode()) {
//                    新建大正用户
                    if (CollectionUtils.isNotEmpty(registerVo.getRoleIds())) {
                        List<SysUserRole> collect = registerVo.getRoleIds().stream().map(rid -> new SysUserRole(sysUser.getId(), rid, byUserName.getUseOrgCode(), false, sysUser.getUsername())).collect(Collectors.toList());
                        userRoleDao.insertBatchSomeColumn(collect);
                    }
                } else if (registerVo.getUserIdentity().intValue() == UserIdentityEnum.DEPART.getCode()) {
//                    新建站点用户
                    List<SysDepartPermission> departPermissions = departPermissionDao.listDepartIdPerMission(Long.valueOf(registerVo.getAffiliationDepartId()));
                    if (CollectionUtils.isEmpty(departPermissions)) {
                        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR20);
                    }
                    SysRole role = roleDao.getRole(registerVo.getAffiliationDepartId(), BasicsRole.JC.getCode());
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setUserId(sysUser.getId());
                    sysUserRole.setRoleId(role.getRoleId());
                    sysUserRole.setOrgCode(byUserName.getUseOrgCode());
                    sysUserRole.setDelFlag(false);
                    sysUserRole.setCreateBy(byUserName.getUsername());
                    userRoleDao.insert(sysUserRole);
                } else {
//                    新建站点子用户
                    if (CollectionUtils.isNotEmpty(registerVo.getRoleIds())) {
                        List<SysUserRole> collect = registerVo.getRoleIds().stream().map(rid -> new SysUserRole(sysUser.getId(), rid, byUserName.getUseOrgCode(), false, sysUser.getUsername())).collect(Collectors.toList());
                        userRoleDao.insertBatchSomeColumn(collect);
                    }
                }
            } else {
//            非大正 例如富华新建用户
                if (CollectionUtils.isNotEmpty(registerVo.getRoleIds())) {
                    List<SysUserRole> collect = registerVo.getRoleIds().stream().map(rid -> new SysUserRole(sysUser.getId(), rid, byUserName.getUseOrgCode(), false, sysUser.getUsername())).collect(Collectors.toList());
                    userRoleDao.insertBatchSomeColumn(collect);
                }

            }
            return new Result(CustomExceptionType.OK);
        }
    }

    /**
     * 编辑用户信息
     *
     * @param updateUser 用户信息
     * @param sub        操作账号
     * @return
     */

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result updateUser(UpdateUser updateUser, String sub) {
        SysUser byUserx = getByUserName(sub);
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(updateUser, sysUser);
        sysUser.setUpdateBy(byUserx.getUsername());
        sysUser.setId(updateUser.getId());
        updateById(sysUser);
        Date updateTime = new Date();
        //            更新站点关系
        SysUser user = getById(updateUser.getId());
        if (user.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode()) {
            userDepartDao.removeUserId(updateUser.getId());
            ResSysDepart byParentId = departDao.getByParentId(0);
            String departId = byParentId.getId();
            if (CollectionUtils.isEmpty(updateUser.getDepartId())) {
                updateUser.setDepartId(Arrays.asList(departId));
            } else {
                updateUser.getDepartId().add(departId);
            }
            List<SysUserDepart> userDeparts = updateUser.getDepartId().stream().map(depgl -> new SysUserDepart(updateUser.getId(), depgl, byUserx.getUseOrgCode(), false, byUserx.getUsername(), byUserx.getUsername(), updateTime)).collect(Collectors.toList());
            userDepartDao.insertBatchSomeColumn(userDeparts);
        }

//              更新角色信息
        if (byUserx.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue()) {
            if (user.getUserIdentity().intValue() != UserIdentityEnum.DEPART.getCode()) {
                userRoleDao.removeUserId(updateUser.getId());
                if (CollectionUtils.isNotEmpty(updateUser.getRoleIds())) {
                    List<SysUserRole> collect = updateUser.getRoleIds().stream().map(rid -> new SysUserRole(updateUser.getId(), rid, byUserx.getUseOrgCode(), false, sysUser.getUsername(), sysUser.getUsername(), updateTime)).collect(Collectors.toList());
                    userRoleDao.insertBatchSomeColumn(collect);
                }
            }
        } else {
//            非大正 例如富华新建用户
            userRoleDao.removeUserId(updateUser.getId());
            if (CollectionUtils.isNotEmpty(updateUser.getRoleIds())) {
                List<SysUserRole> collect = updateUser.getRoleIds().stream().map(rid -> new SysUserRole(updateUser.getId(), rid, byUserx.getUseOrgCode(), false, sysUser.getUsername(), sysUser.getUsername(), updateTime)).collect(Collectors.toList());
                userRoleDao.insertBatchSomeColumn(collect);
            }
        }
        redisUtil.del("userRoleService.listRoleCode::" + user.getUsername() + user.getUseOrgCode()
                , "rolePermissionService.listRolePermissionCode::" + user.getUsername() + user.getUseOrgCode());
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result<UserInfo> getInfo(String sub) {
        SysUser byUserName = getByUserName(sub);
        SysDepart sysDepart = departDao.getByCode(byUserName.getUseOrgCode());
        if (sysDepart == null) {
            throw new CustomException(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR19);
        }
        UserInfo userInfo = new UserInfo();
        List<SysRole> roleList = authRoleCommon.getSysRoles(byUserName, sysDepart);
        UserMessage userMessage = new UserMessage();
        if (CollectionUtils.isNotEmpty(roleList)) {
            List<String> roleIds = roleList.stream().map(role -> role.getRoleId()).collect(Collectors.toList());
            List<String> roleCodes = roleList.stream().map(role -> role.getRoleCode()).collect(Collectors.toList());
            String roleNames = roleList.stream().map(role -> role.getRoleName()).collect(Collectors.joining("|"));
            List<String> permissionCode = rolePermissionDao.listRolePermissionCode(roleIds, byUserName.getUsername(), byUserName.getUseOrgCode());
            userInfo.setRoles(roleCodes);
            userInfo.setPermissions(permissionCode);
            userMessage.setRoleName(roleNames);
        }
        ResDepart resDepart = new ResDepart();
        BeanUtils.copyProperties(sysDepart, resDepart);
        resDepart.setDepartId(String.valueOf(sysDepart.getId()));
        userMessage.setSysDepart(resDepart);
        BeanUtils.copyProperties(byUserName, userMessage);
        userMessage.setUserId(String.valueOf(byUserName.getId()));
        userMessage.setAffiliationDepartId(String.valueOf(byUserName.getAffiliationDepartId()));
        userInfo.setUser(userMessage);
        return new Result(CustomExceptionType.OK, userInfo);
    }


    @Override
    public Result<RoutersInfo> getRouters(String sub) {
        SysUser byUserName = getByUserName(sub);
        SysDepart sysDepart = departDao.getByCode(byUserName.getUseOrgCode());
        List<SysRole> roleList = authRoleCommon.getSysRoles(byUserName, sysDepart);
        if (CollectionUtils.isEmpty(roleList)) {
            return new Result(CustomExceptionType.OK_NO_DATA);
        }
        List<String> roleIds = roleList.stream().map(role -> role.getRoleId()).collect(Collectors.toList());
        QueryWrapper<SysRolePermission> rolePermissionQueryWrapper = new QueryWrapper<>();
        rolePermissionQueryWrapper.eq("org_code", byUserName.getUseOrgCode());
        rolePermissionQueryWrapper.in("role_id", roleIds);
        List<SysRolePermission> permissions = rolePermissionDao.selectList(rolePermissionQueryWrapper);
        if (CollectionUtils.isNotEmpty(permissions)) {
            List<String> perIds = permissions.stream().map(per -> per.getPermissionId()).collect(Collectors.toList());
            QueryWrapper<SysPermission> permissionQueryWrapper = new QueryWrapper<>();
            permissionQueryWrapper.in("id", perIds);
            permissionQueryWrapper.eq("is_route", true);
            permissionQueryWrapper.orderByAsc("sort_no");
            List<SysPermission> list = permissionDao.selectList(permissionQueryWrapper);
            List<SysPermission> permissionsNext = new ArrayList<>();
            List<RoutersInfo> parRout = ChildrenUtils.parPermissionNext(list, permissionsNext);
            List<RoutersInfo> parRoutEnd = ChildrenUtils.getChildRoutersInfo(parRout, permissionsNext);
            return new Result(CustomExceptionType.OK, parRoutEnd);
        }
        throw new CustomException(CustomExceptionType.OK_NO_DATA);
    }

    @Override
    public Result<List<ResSysRole>> getRoles(String sub, PageLimitBase pageLimit, SelRole selRole) {
        SysUser byUserName = getByUserName(sub);
        if(pageLimit.getPage() != -1){
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        List<SysRole> list = roleDao.listNoBasicsRole(pageLimit.getField(), pageLimit.getType(), byUserName.getUseOrgCode(), selRole.getRoleName(), selRole.getRoleCode(), selRole.getStatus(), selRole.getStartTime(), selRole.getEndTime());
        PageInfo<SysRole> paInfo = new PageInfo<>(list);
        List<ResSysRole> sysRoles = new ArrayList<>();
        paInfo.getList().stream().forEach(s -> {
            ResSysRole resSysRole = new ResSysRole();
            BeanUtils.copyProperties(s, resSysRole);
            resSysRole.setDepartId(String.valueOf(s.getDepartId()));
            resSysRole.setRoleId(String.valueOf(s.getRoleId()));
            sysRoles.add(resSysRole);
        });
        return new Result(CustomExceptionType.OK, sysRoles, paInfo.getTotal());
    }


    @CacheEvict(cacheNames = {"businessUserService.getRoles"}, allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result addRole(AddRole addRole, String sub) {
        SysRole sysRole = roleDao.selRoleCode(addRole.getRoleCode());
        if (sysRole != null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR14);
        }
        SysUser byUserName = getByUserName(sub);
        SysRole role = new SysRole();
        BeanUtils.copyProperties(addRole, role);
        role.setOrgCode(byUserName.getUseOrgCode());
        role.setDelFlag(false);
        role.setCreateBy(byUserName.getUsername());
        role.setBasicsRole(BasicsRole.NJC.getCode());
        roleDao.insert(role);
        List<String> permissionId = addRole.getPermissionId();
        if (CollectionUtils.isNotEmpty(permissionId)) {
            List<SysRolePermission> rolePermissions = permissionId.stream().map(pid -> new SysRolePermission(role.getRoleId(), pid, byUserName.getUseOrgCode(), false, byUserName.getUsername())).collect(Collectors.toList());
            rolePermissionDao.insertBatchSomeColumn(rolePermissions);
        }
        return new Result(CustomExceptionType.OK);
    }


    @Override
    public Result addPermission(AddPermission addPermission, String sub) {
        if (!"0".equals(addPermission.getParentId())) {
            SysPermission byId = permissionDao.selectById(Long.valueOf(addPermission.getParentId()));
            if (byId.getMenuType().getCode().intValue() == MenuType.F.getCode()) {
                if (addPermission.getMenuType().getDesc().equals(MenuType.M.getDesc()) || addPermission.getMenuType().getDesc().equals(MenuType.C.getDesc())) {
                    throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR18);
                }
            }

        }
        SysPermission permission = permissionDao.selPermissionCode(addPermission.getPerms());
        if (permission != null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR15);
        }
        SysUser byUserName = getByUserName(sub);
        SysPermission sysPermission = new SysPermission();
        BeanUtils.copyProperties(addPermission, sysPermission);
        sysPermission.setDelFlag(false);
        sysPermission.setCreateBy(byUserName.getUsername());
        sysPermission.setAlwaysShow(false);
        sysPermission.setName(StringToUpcase.toUpperCase(addPermission.getPath()));
        sysPermission.setRedirect(addPermission.getRedirect() != null ? addPermission.getRedirect() : "");
        sysPermission.setTitle(addPermission.getMenuName());
        sysPermission.setParentId(addPermission.getParentId());
        if (sysPermission.getMenuType().getCode().intValue() == MenuType.F.getCode().intValue()) {
            sysPermission.setIsRoute(false);
        }
        permissionDao.insert(sysPermission);
        return new Result(CustomExceptionType.OK);
    }

    /**
     * 更新角色信息
     *
     * @param updateRole
     * @param sub
     * @return
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result updateRole(UpdateRole updateRole, String sub) {
        String roleId = updateRole.getRoleId();
        SysUser byUserName = getByUserName(sub);
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(updateRole, sysRole);
        sysRole.setUpdateBy(byUserName.getUsername());
        sysRole.setRoleId(roleId);
        roleDao.update(sysRole, queryWrapper);
        QueryWrapper<SysRolePermission> roPerWp = new QueryWrapper<>();
        roPerWp.eq("role_id", roleId);
        rolePermissionDao.delete(roPerWp);
        List<String> permissionId = updateRole.getPermissionId();
        if (CollectionUtils.isNotEmpty(permissionId)) {
            List<SysRolePermission> rolePermissions = permissionId.stream().map(pid ->
                    new SysRolePermission(roleId, pid, byUserName.getUseOrgCode(), false, byUserName.getUsername())).collect(Collectors.toList());
            rolePermissionDao.insertBatchSomeColumn(rolePermissions);
        }
        return new Result(CustomExceptionType.OK);
    }


    @Override
    public Result disableEnabledRole(DisableEnabledRole enabledRole, String sub) {
        if (enabledRole.getStatus().intValue() == 0) {
            Integer roleId = userRoleDao.countRoleUser(enabledRole.getRoleId());
            if (roleId != null && roleId.intValue() > 0) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR32);
            }
        }
        SysUser byUserName = getByUserName(sub);
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(enabledRole.getRoleId());
        sysRole.setStatus(enabledRole.getStatus());
        sysRole.setUpdateBy(byUserName.getUsername());
        roleDao.updateById(sysRole);
        return new Result(CustomExceptionType.OK);
    }


    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result delRole(String delRole, String sub) {
        Integer coutuserRole = userRoleDao.countRoleUser(delRole);
        if (coutuserRole != null && coutuserRole.intValue() > 0) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR30);
        }
        SysUser byUserName = getByUserName(sub);
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(delRole);
        sysRole.setUpdateBy(byUserName.getUsername());
        roleDao.updateById(sysRole);
        roleDao.deleteById(delRole);
        return new Result(CustomExceptionType.OK);
    }


    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result delUser(String delUser, String usernum, String sub) {
        SysUser userServiceById = getById(delUser);
        removeById(delUser);
        userDepartDao.removeUserId(delUser);
        userRoleDao.removeUserId(delUser);
        redisUtil.del("userRoleService.listRoleCode::" + userServiceById.getUsername() + userServiceById.getUseOrgCode()
                , "rolePermissionService.listRolePermissionCode::" + userServiceById.getUsername() + userServiceById.getUseOrgCode());
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result disableEnabledUser(DisableEnabledUser enabledUser, String sub) {
        SysUser byUserName = getByUserName(sub);
        SysUser sysUser = new SysUser();
        sysUser.setUpdateBy(byUserName.getUsername());
        sysUser.setId(enabledUser.getId());
        sysUser.setStatus(enabledUser.getStatus());
        updateById(sysUser);
        SysUser userServiceById = getById(Long.valueOf(enabledUser.getId()));
        redisUtil.del("userRoleService.listRoleCode::" + userServiceById.getUsername() + userServiceById.getUseOrgCode()
                , "rolePermissionService.listRolePermissionCode::" + userServiceById.getUsername() + userServiceById.getUseOrgCode());
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result resetUser(ResetUser resetUser, String sub) {
        SysUser byUserName = getByUserName(sub);
        SysUser sysUser = new SysUser();
        sysUser.setPassword(Md5Util.md5(resetUser.getPassword()));
        sysUser.setId(resetUser.getUserId());
        sysUser.setUpdateBy(byUserName.getUsername());
        sysUser.setSecret("");
        sysUser.setRefSecret("");
        updateById(sysUser);
        return new Result(CustomExceptionType.OK);
    }


    @Override
    public Result selMenuPermission(String sub) {
        SysUser byUserName = getByUserName(sub);
        SysDepart byCode = departDao.getByCode(byUserName.getUseOrgCode());
        List<SysPermission> list = new ArrayList<>();
        if (byUserName.getUsername().equals(BasicsAdmin.Admin.getCode())) {
            QueryWrapper<SysPermission> wp = new QueryWrapper<>();
            wp.orderByAsc("sort_no");
            list = permissionDao.selectList(wp);
        } else {
            List<String> sysRoles = authRoleCommon.getSysRolesId(byUserName, byCode);
            String joinKey = StringUtils.join(sysRoles, "");
            list = rolePermissionDao.listRolePermission(sysRoles, joinKey);
        }

        List<MenusInfo> menusInfos = new ArrayList<>();
        for (SysPermission sysPermission : list) {
            MenusInfo metaInfo = new MenusInfo();
            BeanUtils.copyProperties(sysPermission, metaInfo);
            metaInfo.setChildren(Lists.newArrayList());
            metaInfo.setMenuId(String.valueOf(sysPermission.getId()));
            metaInfo.setMenuName(sysPermission.getTitle());
            metaInfo.setCreateTime(sysPermission.getCreateTime());
            metaInfo.setMenuType(sysPermission.getMenuType());
            metaInfo.setParentId(String.valueOf(sysPermission.getParentId()));
            menusInfos.add(metaInfo);
        }
        return new Result(CustomExceptionType.OK, menusInfos);
    }

    @Override
    public Result updatePermission(UpdatePermission updatePermission, String sub) {
        SysUser byUserName = getByUserName(sub);
        if (updatePermission.getIsRoute() == null) {
            updatePermission.setIsRoute(1);
        }
        SysPermission sysPermission = new SysPermission();
        BeanUtils.copyProperties(updatePermission, sysPermission);
        sysPermission.setTitle(updatePermission.getMenuName());
        sysPermission.setParentId(updatePermission.getParentId());
        sysPermission.setId(updatePermission.getMenuId());
        sysPermission.setCreateBy(byUserName.getUsername());
        sysPermission.setHidden(updatePermission.getHidden());
        sysPermission.setName(StringToUpcase.toUpperCase(updatePermission.getPath()));
        sysPermission.setIsRoute(updatePermission.getIsRoute().intValue() == 1 ? true : false);
        if (sysPermission.getMenuType().getCode().intValue() == MenuType.F.getCode().intValue()) {
            sysPermission.setIsRoute(false);
        }
        permissionDao.updateById(sysPermission);
        return new Result(CustomExceptionType.OK);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result delPermission(Long delPermission, String sub) {
        SysUser byUserName = getByUserName(sub);
//        查询角色管理的权限
        Integer coutRole = rolePermissionDao.selectByPerId(delPermission);
        if (coutRole != null && coutRole.intValue() > 0) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR28);
        }
        Integer coutRoleDept = departPermissionDao.selectByPerId(delPermission);
        if (coutRoleDept != null && coutRoleDept.intValue() > 0) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR29);
        }
        List<String> delList = new ArrayList<>();
        delList.add(delPermission.toString());
        //循环判断是否有下一级目录
        while (true) {
            permissionDao.deleteBatchIds(delList);
            List<SysPermission> list = permissionDao.selectList(new QueryWrapper<SysPermission>().in("parent_id", delList));
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            delList.addAll(list.stream().map(pis -> pis.getId()).collect(Collectors.toList()));
        }
        rolePermissionDao.delete(new QueryWrapper<SysRolePermission>().in("permission_id", delList));
//       删除当前节点
//        permissionDao.deleteById(delPermission);

//        删除子节点
//        QueryWrapper<SysPermission> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("parent_id", delPermission);
//        List<SysPermission> list = permissionDao.selectList(queryWrapper);
//        if (CollectionUtils.isNotEmpty(list)) {
//            List<String> perId = list.stream().map(pis -> pis.getId()).collect(Collectors.toList());
//            QueryWrapper<SysRolePermission> roleQueryWrapper = new QueryWrapper<>();
//            roleQueryWrapper.in("permission_id", perId);
//            rolePermissionDao.delete(roleQueryWrapper);
//            permissionDao.deleteBatchIds(perId);
//        }

//        删除当前主节点的管理角色表
//        QueryWrapper<SysRolePermission> sysRolePermissionQueryWrapper = new QueryWrapper<>();
//        sysRolePermissionQueryWrapper.eq("permission_id", delPermission);
//        rolePermissionDao.delete(sysRolePermissionQueryWrapper);
        return new Result(CustomExceptionType.OK);
    }


    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    @Override
    public Result switchSite(SwitchSite switchSite, String sub) {
        SysDepart byId = departDao.selectById(switchSite.getId());
        if (byId == null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR13);
        }
        if (byId.getStatus().intValue() == StatusEnum.Disable.getCode()) {
            throw new CustomException(CustomExceptionType.AUTHEN_TICATIIN_FAILURE, CustomResponseCode.ERR24);
        }
        SysUser byUserName = getByUserName(sub);
        if (byUserName.getUseOrgCode() != null && byUserName.getUseOrgCode().equals(byId.getOrgCode())) {
            return new Result(CustomExceptionType.OK);
        }
        SysUser sysUser = new SysUser();
        sysUser.setId(byUserName.getId());
        sysUser.setUseOrgCode(byId.getOrgCode());
        sysUser.setUpdateBy(byUserName.getUsername());
        updateById(sysUser);
        return new Result(CustomExceptionType.OK);
    }


    @Override
    public Result querySwitchSite(SysUser byUserName, Boolean isAll) {
        List<SwitchSiteDo> departs = new ArrayList<>();
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue()) {
//            大正用户
            if (byUserName.getUseOrgCode().equals(byUserName.getOrgCode())) {
//                没有切换站点
                if (isAll) {
//                  获取所有站点信息
                    departs = departDao.listAll();
                    return new Result(CustomExceptionType.OK, departs);
                } else {
//                    获取关联站点信息
                    departs = getSwitchSiteDos(byUserName, departs);
                    return new Result(CustomExceptionType.OK, departs);
                }
            } else {
//                切换了站点
                if (isAll) {
                    SwitchSiteDo switchSiteDo = departDao.getByOrgCode(byUserName.getUseOrgCode());
                    return Result.ok(Lists.newArrayList(switchSiteDo));
                } else {
//                   获取关联站点信息
                    departs = getSwitchSiteDos(byUserName, departs);
                    return new Result(CustomExceptionType.OK, departs);
                }
            }
        } else {
//           非大正用户 获取关联站点信息
            departs = getSwitchSiteDos(byUserName, departs);
            return new Result(CustomExceptionType.OK, departs);
        }
    }

    public List<SwitchSiteDo> getSwitchSiteDos(SysUser byUserName, List<SwitchSiteDo> departs) {
        QueryWrapper<SysUserDepart> userDepQwp = new QueryWrapper<>();
        userDepQwp.eq("user_id", byUserName.getId());
        List<SysUserDepart> list = userDepartDao.selectList(userDepQwp);
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> ids = list.stream().map(dep -> dep.getDepartId()).collect(Collectors.toList());
            departs = departDao.listId(ids);
        }
        return departs;
    }

    @Override
    public Result<MenusInfo> selMenuPermissionId(Long id, String sub) {
        SysPermission sysPermission = permissionDao.selectById(id);
        if (sysPermission == null) {
            throw new CustomException(CustomExceptionType.OK_NO_DATA);
        }
        MenusInfo metaInfo = new MenusInfo();
        BeanUtils.copyProperties(sysPermission, metaInfo);
        metaInfo.setChildren(Lists.newArrayList());
        metaInfo.setMenuId(String.valueOf(sysPermission.getId()));
        metaInfo.setMenuName(sysPermission.getTitle());
        metaInfo.setCreateTime(sysPermission.getCreateTime());
        metaInfo.setMenuType(sysPermission.getMenuType());
        metaInfo.setParentId(String.valueOf(sysPermission.getParentId()));
        return new Result(CustomExceptionType.OK, metaInfo);
    }


    @Override
    public Result treeSelect(String sub, SysUser byUserName) {
//        获取用户
        List<SysPermission> permissions = new ArrayList<>();
        SysDepart byCode = departDao.getByCode(byUserName.getUseOrgCode());
        if (byUserName.getUsername().equals(BasicsAdmin.Admin.getCode())) {
            if (byCode.getParentId().equals("0")) {
                list();
                permissions = permissionDao.selectList(Wrappers.emptyWrapper());
            } else {
                List<String> sysRoles = authRoleCommon.getSysRolesId(byUserName, byCode);
                if (CollectionUtils.isNotEmpty(sysRoles)) {
                    String joinKey = StringUtils.join(sysRoles, "");
                    permissions = rolePermissionDao.listRolePermission(sysRoles, joinKey);
                }
            }
        } else {
            List<String> sysRoles = authRoleCommon.getSysRolesId(byUserName, byCode);
            if (CollectionUtils.isNotEmpty(sysRoles)) {
                String joinKey = StringUtils.join(sysRoles, "");
                permissions = rolePermissionDao.listRolePermission(sysRoles, joinKey);
            }
        }
        List<SysPermission> permissionsNext = new ArrayList<>();
        List<RoutersInfo> parRout = ChildrenUtils.parPermissionNext(permissions, permissionsNext);
        List<RoutersInfo> parRoutEnd = ChildrenUtils.getChildRoutersInfo(parRout, permissionsNext);
        List<Map<String, Object>> map = ChildrenUtils.treeSelectMap(parRoutEnd);
        return new Result(CustomExceptionType.OK, map);
    }


    @Override
    public Result treeSelectCheck(String id, String sub) {
        Map<String, Object> map = new HashMap<>();
        List<String> longList = Arrays.asList(id);
        String joinKey = StringUtils.join(longList, "");
        List<SysPermission> sysPermissions = rolePermissionDao.listRolePermission(longList, joinKey);
        if (CollectionUtils.isNotEmpty(sysPermissions)) {
            List<String> checkIds = sysPermissions.stream().map(sysps -> String.valueOf(sysps.getId())).collect(Collectors.toList());
            map.put("checkedKeys", checkIds);
        }
        SysUser byUserName = getByUserName(sub);
        Result result = this.treeSelect(sub, byUserName);
        map.put("menus", result.getData());
        return Result.ok(map);
    }

    @Override
    public Result<ResSysRole> getRolesDetails(Long id, String sub) {
        SysRole byId = roleDao.selectById(id);
        ResSysRole resSysRole = null;
        if (byId != null) {
            resSysRole = new ResSysRole();
            BeanUtils.copyProperties(byId, resSysRole);
        }
        return new Result<>(CustomExceptionType.OK, resSysRole);
    }


    @Override
    public Result userLists(PageLimit pageLimit, SelUser selUser, String sub) {
        SysUser byUserName = getByUserName(sub);
        if(pageLimit.getPage() != -1){
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        List<UserListRes> list = listUserOrgCode(pageLimit.getField(), pageLimit.getType(), byUserName.getUseOrgCode(), selUser.getRealname(), selUser.getUsername(), selUser.getStatus(), selUser.getStartTime(), selUser.getEndTime());
        PageInfo pageInfo = new PageInfo(list);
        return new Result(CustomExceptionType.OK, pageInfo.getList(), pageInfo.getTotal());
    }

    @Override
    public Result getRolesNotAdmin(String sub, String userId) {
        SysUser byUserName = getByUserName(sub);
        QueryWrapper<SysRole> rwp = new QueryWrapper<>();
        rwp.eq("org_code", byUserName.getUseOrgCode());
        rwp.ne("basics_role", BasicsRole.JC.getCode());
        List<SysRole> roleListX = roleDao.selectList(rwp);
        List<ResSysRole> roleList = new ArrayList<>();
        roleListX.stream().forEach(s -> {
            ResSysRole sysRole = new ResSysRole();
            BeanUtils.copyProperties(s, sysRole);
            sysRole.setRoleId(String.valueOf(s.getRoleId()));
            roleList.add(sysRole);
        });
        Map<String, Object> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(roleList)) {
            roleList.stream().forEach(ro -> {
                if (ro.getBasicsRole().intValue() == BasicsRole.Admin.getCode().intValue()) {
                    ro.setDisabled(true);
                } else {
                    ro.setDisabled(false);
                }
            });
        }
        //        角色信息
        map.put("roles", roleList);
//       传递用户信息
        if (userId != null) {
            SysUser sysUser = getById(userId);
            ResSysUser userServiceById = new ResSysUser();
            BeanUtils.copyProperties(sysUser, userServiceById);
            List<String> roleId = userRoleDao.listRoleId(Long.valueOf(sysUser.getId()), byUserName.getUseOrgCode(), BasicsRole.NJC.getCode());
            map.put("roledIds", roleId);
            //      用户信息
            map.put("user", userServiceById);
            ResSysDepart byParentId = departDao.getByParentId(0);
            //       关联站点
            List<SysUserDepart> userDeparts = userDepartDao.listByUserIdOrgcodeNeDepartId(sysUser.getId(), sysUser.getUseOrgCode(), byParentId.getId());
            List<String> departIds = userDeparts.stream().map(usd -> String.valueOf(usd.getDepartId())).collect(Collectors.toList());
            map.put("departIds", departIds);
        }
//        站点信息
        List<ResSysDepart> allDepart = departDao.listNotDz();
        SysDepart byCode = departDao.getByCode(byUserName.getUseOrgCode());
        if ("0".equals(byCode.getParentId())) {
//            所有站点
            map.put("departs", allDepart);
        } else {
            QueryWrapper<SysUserDepart> userDepQwp = new QueryWrapper<>();
            userDepQwp.eq("user_id", byUserName.getId());
            userDepQwp.eq("org_code", byUserName.getUseOrgCode());
            List<SysUserDepart> list = userDepartDao.selectList(userDepQwp);
            if (CollectionUtils.isNotEmpty(list)) {
                List<String> collect = list.stream().map(dep -> dep.getDepartId()).collect(Collectors.toList());
                QueryWrapper<SysDepart> depQwp = new QueryWrapper<>();
                depQwp.in("id", collect);
                List<SysDepart> departs = departDao.selectList(depQwp);
                List<ResSysDepart> resSysDeparts = new ArrayList<>();
                departs.stream().forEach(s -> {
                    ResSysDepart resSysDepart = new ResSysDepart();
                    BeanUtils.copyProperties(s, resSysDepart);
                    resSysDepart.setId(String.valueOf(s.getId()));
                    resSysDepart.setParentId(String.valueOf(s.getParentId()));
                    resSysDeparts.add(resSysDepart);
                });
                map.put("departs", resSysDeparts);
            } else {
                map.put("departs", new ArrayList<>());
            }
        }
        map.put("affiliationDepartIds", allDepart);
        Result result = new Result(CustomExceptionType.OK, map);
        return result;
    }

    @Override
    public Result getUserId(Long userId, String sub) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("avatar", "create_by", "create_time", "id", "realname", "status", "update_by", "update_time", "use_org_code", "username");
        queryWrapper.eq("id", userId);
        SysUser sysUser = getOne(queryWrapper);
        return new Result(CustomExceptionType.OK, sysUser);
    }

    @Override
    public Result putPassword(String sub, PutUserPasswordVo putUserInfoVo) {
        SysUser byUserName = getByUserName(sub);
        if (!putUserInfoVo.getPasswordNew().equals(putUserInfoVo.getPasswordRepeat())) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_44);
        }
        if (!Md5Util.md5(putUserInfoVo.getPasswordOld()).equals(byUserName.getPassword())) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_45);
        }
        byUserName.setPassword(Md5Util.md5(putUserInfoVo.getPasswordNew()));
        updateById(byUserName);
        redisUtil.del("userRoleService.listRoleCode::" + byUserName.getUsername() + byUserName.getUseOrgCode()
                , "rolePermissionService.listRolePermissionCode::" + byUserName.getUsername() + byUserName.getUseOrgCode());
        return null;
    }

}
