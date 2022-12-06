package com.dzics.data.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.enums.UserIdentityEnum;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.ums.model.dao.SwitchSiteDo;
import com.dzics.data.ums.model.dto.depart.AddDepart;
import com.dzics.data.ums.model.dto.depart.DisableEnabledDepart;
import com.dzics.data.ums.model.dto.depart.SelDepart;
import com.dzics.data.ums.model.dto.depart.UpdateDepart;
import com.dzics.data.ums.model.entity.*;
import com.dzics.data.ums.model.vo.depart.ResDepart;
import com.dzics.data.ums.model.vo.router.RoutersInfo;
import com.dzics.data.ums.service.DzicsUserService;
import com.dzics.data.ums.service.SysDepartService;
import com.dzics.data.ums.service.SysRolePermissionService;
import com.dzics.data.ums.util.ChildrenUtils;
import com.dzics.data.common.base.enums.BasicsRole;
import com.dzics.data.common.base.enums.StatusEnum;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.UnderlineTool;
import com.dzics.data.ums.db.dao.*;
import com.dzics.data.ums.util.WapperAddUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 站点公司表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@SuppressWarnings("ALL")
@Slf4j
@Service
public class SysDepartServiceImpl extends ServiceImpl<SysDepartDao, SysDepart> implements SysDepartService {

    @Autowired
    private DzicsUserService dzicsUserService;
    @Autowired
    private SysDepartPermissionDao departPermissionDao;
    @Autowired
    private SysUserDao userDao;
    @Autowired
    private SysRoleDao roleDao;
    @Autowired
    private SysPermissionDao permissionDao;
    @Autowired
    private SysUserDepartDao userDepartDao;
    @Autowired
    private SysRolePermissionService rolePermissionService;
    @Autowired
    private WapperAddUtil wapperAddUtil;
    @Autowired
    private  SysDepartDao departDao;

    @Override
    public List<SysDepart> listNotDz() {
        QueryWrapper<SysDepart> departQueryWrapper = new QueryWrapper<>();
        departQueryWrapper.ne("parent_id", 0);
        return this.baseMapper.selectList(departQueryWrapper);

    }



    @Override
    public Result addDepart(AddDepart addDepart, String sub) {
        SysDepart byCode = departDao.getByCode(addDepart.getOrgCode());
        if (byCode != null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR23);
        }
        SysDepart sysDepart = new SysDepart();
        BeanUtils.copyProperties(addDepart, sysDepart);
        sysDepart.setCreateBy(sub);
        sysDepart.setDelFlag(false);
        sysDepart.setParentId("1");
        save(sysDepart);
        List<String> permissionId = addDepart.getPermissionId();
        if (CollectionUtils.isNotEmpty(permissionId)) {
            List<SysDepartPermission> collect = permissionId.stream().map(pmid -> new SysDepartPermission(sysDepart.getId(), pmid, false, sub)).collect(Collectors.toList());
            departPermissionDao.insertBatchSomeColumn(collect);
//            创建站点基础角色
            SysRole sysRole = new SysRole();
            sysRole.setRoleName(sysDepart.getDepartName());
            sysRole.setRoleCode(sysDepart.getOrgCode() + "_Admin");
            sysRole.setDescription("基础Admin角色");
            sysRole.setDepartId(sysDepart.getId());
            sysRole.setBasicsRole(BasicsRole.JC.getCode());
            sysRole.setOrgCode(sysDepart.getOrgCode());
            sysRole.setStatus(StatusEnum.Enable.getCode());
            sysRole.setDelFlag(false);
            sysRole.setCreateBy(sub);
            roleDao.insert(sysRole);
            List<SysRolePermission> rolePermissions = permissionId.stream().map(pid ->
                    new SysRolePermission(sysRole.getRoleId(), pid, sysDepart.getOrgCode(),
                            false, sub)).collect(Collectors.toList());
            rolePermissionService.saveBatch(rolePermissions);
        }
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result<List<ResDepart>> queryDepart(PageLimit pageLimit, SelDepart selDepart, String sub) {
        QueryWrapper<SysDepart> queryWrapper = new QueryWrapper<>();
        if (selDepart != null) {
            wapperAddUtil.addQuery(selDepart, queryWrapper);
        }
        queryWrapper.ne("parent_id", 0L);
        if (!StringUtils.isEmpty(pageLimit.getType())) {
            if ("DESC".equals(pageLimit.getType())) {
                queryWrapper.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if ("ASC".equals(pageLimit.getType())) {
                queryWrapper.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            }
        }
        if(pageLimit.getPage() != -1){
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        List<SysDepart> list = list(queryWrapper);
        PageInfo<SysDepart> sysDepartPageInfo = new PageInfo<>(list);
        List<ResDepart> departs = new ArrayList<>();
        for (SysDepart sysDepart : sysDepartPageInfo.getList()) {
            ResDepart resDepart = new ResDepart();
            BeanUtils.copyProperties(sysDepart, resDepart);
            resDepart.setDepartId(String.valueOf(sysDepart.getId()));
            departs.add(resDepart);
        }
        return new Result(CustomExceptionType.OK, departs, sysDepartPageInfo.getTotal());
    }

    @Override
    public Result<List<ResDepart>> queryDepart(PageLimitBase pageLimit, SelDepart selDepart, String sub) {
        SysUser byUserName = dzicsUserService.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        selDepart.setOrgCode(byUserName.getOrgCode());
        QueryWrapper<SysDepart>queryWrapper=new QueryWrapper<>();
        if(!StringUtils.isEmpty(selDepart.getDepartName())){
            queryWrapper.like("depart_name",selDepart.getDepartName());
        }
        if(!StringUtils.isEmpty(selDepart.getOrgCode())){
            queryWrapper.like("org_code",selDepart.getOrgCode());
        }
        if(!StringUtils.isEmpty(selDepart.getStatus())){
            queryWrapper.eq("",selDepart.getStatus());
        }
        if(pageLimit.getPage() != -1){
            PageHelper.startPage(pageLimit.getPage(),pageLimit.getLimit());
        }
        List<SysDepart> list = list(queryWrapper);
        PageInfo<SysDepart>info=new PageInfo<>(list);
        List<ResDepart> departs = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            for (SysDepart sysDepart : list) {
                ResDepart resDepart = new ResDepart();
                BeanUtils.copyProperties(sysDepart, resDepart);
                resDepart.setDepartId(String.valueOf(sysDepart.getId()));
                departs.add(resDepart);
            }
        }
        return new Result(CustomExceptionType.OK, departs,info.getTotal());
    }

    /**
     * @param sub      操作用户
     * @param departId 站点id 如果传递了站点id则返回 已选择的权限 id
     * @return
     */
    @Override
    public Result getDepartMsg(String sub, Long departId) {
        Map<String, Object> map = new HashMap<>();
        SysUser byUserName = dzicsUserService.getByUserName(sub);
        SysDepart byCode = departDao.getByCode(byUserName.getUseOrgCode());
        if (byCode == null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR13);
        }
        if (!"0".equals(byCode.getParentId())) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR31);
        } else {
            List<SysPermission> permissions = permissionDao.selectList(Wrappers.emptyWrapper());
            List<SysPermission> permissionsNext = new ArrayList<>();
            List<RoutersInfo> parRout = ChildrenUtils.parPermissionNext(permissions, permissionsNext);
            List<RoutersInfo> parRoutEnd = ChildrenUtils.getChildRoutersInfo(parRout, permissionsNext);
            List<Map<String, Object>> list = ChildrenUtils.treeSelectMap(parRoutEnd);
            map.put("permissions", list);
        }
        if (departId != null) {
//         编辑是 需要获取 获取的站点信息
            QueryWrapper<SysDepartPermission> depPerMis = new QueryWrapper<>();
            depPerMis.eq("depart_id", departId);
            List<String> permisChecked = departPermissionDao.selectList(depPerMis).stream().map(dePers -> String.valueOf(dePers.getPermissionId())).collect(Collectors.toList());
            map.put("checkedKeys", permisChecked);
        }
        return new Result(CustomExceptionType.OK, map);
    }



    @Override
    public Result getDepartDetails(String sub, Long departId) {
        SysDepart byId = getById(departId);
        ResDepart resDepart = new ResDepart();
        BeanUtils.copyProperties(byId, resDepart);
        resDepart.setDepartId(String.valueOf(byId.getId()));
        return new Result(CustomExceptionType.OK, resDepart);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result updateDepart(UpdateDepart updateDepart, String sub) {
        SysUser byUserName = dzicsUserService.getByUserName(sub);
        SysDepart sysDepart = new SysDepart();
        BeanUtils.copyProperties(updateDepart, sysDepart);
        sysDepart.setCreateBy(byUserName.getUsername());
        sysDepart.setUpdateBy(byUserName.getUsername());
        sysDepart.setId(updateDepart.getDepartId());
         updateById(sysDepart);
        departPermissionDao.removeDepartId(Long.valueOf(updateDepart.getDepartId()));
        List<String> permissionId = updateDepart.getPermissionId();
        if (CollectionUtils.isNotEmpty(permissionId)) {
            List<SysDepartPermission> collect = permissionId.stream().map(pmid -> new SysDepartPermission(sysDepart.getId(),pmid, false, byUserName.getUsername())).collect(Collectors.toList());
            departPermissionDao.insertBatchSomeColumn(collect);
//            更新站点基础角色
            SysRole role = roleDao.getRole(sysDepart.getId(), BasicsRole.JC.getCode());
            role.setOrgCode(sysDepart.getOrgCode());
            roleDao.updateById(role);
            rolePermissionService.removeRoleId(role.getRoleId());
            List<SysRolePermission> rolePermissions = permissionId.stream().map(pid ->
                    new SysRolePermission(role.getRoleId(), pid, sysDepart.getOrgCode(),
                            false, byUserName.getUsername())).collect(Collectors.toList());
            rolePermissionService.saveBatch(rolePermissions);
        }
        return new Result(CustomExceptionType.OK);
    }


    @Override
    public Result disableEnabledRole(DisableEnabledDepart enabledDepart, String sub) {
        SysDepart sysDepart = new SysDepart();
        sysDepart.setId(enabledDepart.getDepartId());
        sysDepart.setStatus(enabledDepart.getStatus());
        updateById(sysDepart);
        roleDao.updateDepartId(Long.valueOf(enabledDepart.getDepartId()), enabledDepart.getStatus());
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public SysDepart getById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public List<SysDepart> list(QueryWrapper<SysDepart> depQwp) {
        return this.baseMapper.selectList(depQwp);
    }

    @Override
    public List<SwitchSiteDo> listId(List<String> ids) {
        return this.baseMapper.listId(ids);
    }

    @Override
    public List<SwitchSiteDo> listAll() {
        return this.baseMapper.listAll();
    }




}
