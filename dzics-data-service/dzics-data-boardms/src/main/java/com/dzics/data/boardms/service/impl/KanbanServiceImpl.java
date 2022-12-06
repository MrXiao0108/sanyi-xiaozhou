package com.dzics.data.boardms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.enums.MenuType;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.StringToUpcase;
import com.dzics.data.boardms.db.dao.SysKanbanRoutingDao;
import com.dzics.data.boardms.model.dto.menu.AddPermission;
import com.dzics.data.boardms.model.dto.menu.SelKbRouting;
import com.dzics.data.boardms.model.dto.menu.UpdatePermission;
import com.dzics.data.boardms.model.entity.SysKanbanRouting;
import com.dzics.data.boardms.model.entity.SysUser;
import com.dzics.data.boardms.model.vo.KanbanRouting;
import com.dzics.data.boardms.model.vo.MenusInfo;
import com.dzics.data.boardms.model.vo.RoutersInfo;
import com.dzics.data.boardms.service.KanbanService;
import com.dzics.data.boardms.util.ChildrenUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ZhangChengJun
 * Date 2021/4/28.
 * @since
 */
@Service
@Slf4j
public class KanbanServiceImpl  extends ServiceImpl<SysKanbanRoutingDao, SysKanbanRouting> implements KanbanService {

    @Autowired
    private SysUserServiceImpl userDao;

    @Override
    public Result<MenusInfo> selMenuPermission(String sub) {
        QueryWrapper<SysKanbanRouting> wp = new QueryWrapper<>();
        wp.orderByAsc("sort_no");
        List<SysKanbanRouting> list = list(wp);
        List<MenusInfo> menusInfos = new ArrayList<>();
        for (SysKanbanRouting sysPermission : list) {
            MenusInfo metaInfo = new MenusInfo();
            BeanUtils.copyProperties(sysPermission, metaInfo);
            metaInfo.setChildren(Lists.newArrayList());
            metaInfo.setMenuId(String.valueOf(sysPermission.getId()));
            metaInfo.setMenuName(sysPermission.getTitle());
            metaInfo.setCreateTime(sysPermission.getCreateTime());
            metaInfo.setParentId(String.valueOf(sysPermission.getParentId()));
            menusInfos.add(metaInfo);
        }
        return new Result(CustomExceptionType.OK, menusInfos);
    }

    @Override
    public Result addPermission(AddPermission addPermission, String sub) {
        if(!"M".equals(addPermission.getMenuType().getDesc())){
            if(StringUtils.isEmpty(addPermission.getPathNumber())){
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR39);
            }
        }
        if (addPermission.getParentId().longValue() != 0) {
            SysKanbanRouting byId = getById(addPermission.getParentId());
            if (byId.getMenuType().getCode().intValue() == MenuType.F.getCode()) {
                if (addPermission.getMenuType().getDesc().equals(MenuType.M.getDesc()) || addPermission.getMenuType().getDesc().equals(MenuType.C.getDesc())) {
                    throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR18);
                }
            }

        }
        QueryWrapper<SysKanbanRouting> wp = new QueryWrapper<>();
        wp.eq("path",addPermission.getPath());
        wp.eq("path_number",addPermission.getPathNumber());
        List<SysKanbanRouting> list = list(wp);
        if (CollectionUtils.isNotEmpty(list)){
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
        SysUser byUserName = userDao.getByUserName(sub);
        SysKanbanRouting sysPermission = new SysKanbanRouting();
        BeanUtils.copyProperties(addPermission, sysPermission);
        sysPermission.setDelFlag(false);
        sysPermission.setCreateBy(byUserName.getUsername());
        sysPermission.setAlwaysShow(false);
        sysPermission.setName(StringToUpcase.toUpperCase(addPermission.getPath()));
        sysPermission.setRedirect(addPermission.getRedirect() != null ? addPermission.getRedirect() : "");
        sysPermission.setTitle(addPermission.getMenuName());
        if (sysPermission.getMenuType().getCode().intValue() == MenuType.F.getCode().intValue()) {
            sysPermission.setIsRoute(false);
        }
        save(sysPermission);
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result<MenusInfo> selMenuPermissionId(Long id, String sub) {
        SysKanbanRouting sysPermission = getById(id);
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
    public Result updatePermission(UpdatePermission updatePermission, String sub) {
        SysUser byUserName = userDao.getByUserName(sub);
        if (updatePermission.getIsRoute() == null) {
            updatePermission.setIsRoute(1);
        }
        SysKanbanRouting sysPermission = new SysKanbanRouting();
        BeanUtils.copyProperties(updatePermission, sysPermission);
        sysPermission.setParentId(Long.valueOf(updatePermission.getParentId()));
        sysPermission.setTitle(updatePermission.getMenuName());
        sysPermission.setId(updatePermission.getMenuId());
        sysPermission.setCreateBy(byUserName.getUsername());
        sysPermission.setHidden(updatePermission.getHidden());
        sysPermission.setName(StringToUpcase.toUpperCase(updatePermission.getPath()));
        sysPermission.setIsRoute(updatePermission.getIsRoute().intValue() == 1 ? true : false);
        if (sysPermission.getMenuType().getCode().intValue() == MenuType.F.getCode().intValue()) {
            sysPermission.setIsRoute(false);
        }
        updateById(sysPermission);
        return new Result(CustomExceptionType.OK);
    }


    @Override
    public Result delPermission(Long delPermission, String sub) {
        removeById(delPermission);
//        删除子节点
        QueryWrapper<SysKanbanRouting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", delPermission);
        List<SysKanbanRouting> list = list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> perId = list.stream().map(pis -> pis.getId()).collect(Collectors.toList());
            removeByIds(perId);
        }
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result selRoutingDetails(SelKbRouting kbRouting, String sub) {
        if (kbRouting == null || StringUtils.isEmpty(kbRouting.getParentTitle())) {
            return selRouting(sub);
        }
        QueryWrapper<SysKanbanRouting> wp = new QueryWrapper<>();
        wp.eq("title", kbRouting.getParentTitle());
        wp.eq("parent_id", 0);
        SysKanbanRouting one = getOne(wp);
        if (one != null) {
            QueryWrapper<SysKanbanRouting> wpKb = new QueryWrapper<>();
            wpKb.eq("parent_id", one.getId());
            List<SysKanbanRouting> list = list(wpKb);
            List<MenusInfo> menusInfos = new ArrayList<>();
            for (SysKanbanRouting sysPermission : list) {
                MenusInfo metaInfo = new MenusInfo();
                BeanUtils.copyProperties(sysPermission, metaInfo);
                metaInfo.setChildren(Lists.newArrayList());
                metaInfo.setMenuId(String.valueOf(sysPermission.getId()));
                metaInfo.setMenuName(sysPermission.getTitle());
                metaInfo.setCreateTime(sysPermission.getCreateTime());
                metaInfo.setMenuType(sysPermission.getMenuType());
                menusInfos.add(metaInfo);
            }
            return new Result(CustomExceptionType.OK, menusInfos);
        }
        return Result.ok(CustomExceptionType.OK_NO_DATA);
    }

    @Override
    public Result selRouting(String sub) {
        List<SysKanbanRouting> list = list();
        List<MenusInfo> menusInfos = new ArrayList<>();
        for (SysKanbanRouting sysPermission : list) {
            MenusInfo metaInfo = new MenusInfo();
            BeanUtils.copyProperties(sysPermission, metaInfo);
            metaInfo.setChildren(Lists.newArrayList());
            metaInfo.setMenuId(String.valueOf(sysPermission.getId()));
            metaInfo.setMenuName(sysPermission.getTitle());
            metaInfo.setCreateTime(sysPermission.getCreateTime());
            metaInfo.setMenuType(sysPermission.getMenuType());
            menusInfos.add(metaInfo);
        }
        return new Result(CustomExceptionType.OK, menusInfos);
    }

    @Override
    public Result<KanbanRouting> selMenuRouting(SelKbRouting kbRouting, String sub) {
        List<SysKanbanRouting> permissions = new ArrayList<>();
        if (kbRouting == null || StringUtils.isEmpty(kbRouting.getParentTitle())) {
            permissions = list();
        } else {
            QueryWrapper<SysKanbanRouting> wp = new QueryWrapper<>();
            wp.eq("title", kbRouting.getParentTitle());
            wp.eq("parent_id", 0);
            SysKanbanRouting one = getOne(wp);
            if (one != null) {
                QueryWrapper<SysKanbanRouting> wpKb = new QueryWrapper<>();
                wpKb.eq("parent_id", one.getId());
                permissions = list(wpKb);
            }
        }
        if (CollectionUtils.isNotEmpty(permissions)) {
            List<SysKanbanRouting> permissionsNext = new ArrayList<>();
            List<RoutersInfo> parRout = ChildrenUtils.parPermissionNextKb(permissions, permissionsNext);
            List<RoutersInfo> parRoutEnd = ChildrenUtils.getChildRoutersInfoKb(parRout, permissionsNext);
            return new Result(CustomExceptionType.OK, parRoutEnd);
        }
        return null;
    }

    @Override
    public Result<KanbanRouting> selRoutingDetailsOrder(SelKbRouting kbRouting, String sub) {
        QueryWrapper<SysKanbanRouting> wp = new QueryWrapper<>();
        wp.eq("path", kbRouting.getParentTitle());
        wp.eq("path_number",kbRouting.getPathNumber());
        SysKanbanRouting one = getOne(wp);
        KanbanRouting kanbanRouting = new KanbanRouting();
        BeanCopier copier = BeanCopier.create(SysKanbanRouting.class, KanbanRouting.class, false);
        copier.copy(one, kanbanRouting, null);
        return Result.ok(kanbanRouting);
    }


}
