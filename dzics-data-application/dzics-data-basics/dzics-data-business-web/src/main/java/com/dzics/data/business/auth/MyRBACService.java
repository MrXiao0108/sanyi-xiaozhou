package com.dzics.data.business.auth;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.ums.db.dao.SysDepartDao;
import com.dzics.data.ums.model.entity.SysDepart;
import com.dzics.data.ums.model.entity.SysPermission;
import com.dzics.data.ums.model.entity.SysRole;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.AuthRoleComService;
import com.dzics.data.ums.service.DzicsUserService;
import com.dzics.data.ums.service.SysRolePermissionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("rabcService")
public class MyRBACService {

    @Autowired
    private AntPathMatcher antPathMatcher;
    @Autowired
    @Lazy
    private DzicsUserService userService;

    @Autowired
    @Lazy
    private SysRolePermissionService rolePermissionService;

    @Autowired
    private AuthRoleComService authRoleCommon;
    @Autowired
    private SysDepartDao departDao;

    /**
     * 判断某用户是否具有该request资源的访问权限
     */
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            //加载基础用户信息
            SysUser users = userService.getByUserName(username);
            if (users == null) {
                return false;
            }
            Map<String, List<String>> permissionMap = getPermissionMap(users, users.getUseOrgCode());
            List<String> strings = null;
            String requestUrl = request.getRequestURI();
            String method = request.getMethod();
            for (Map.Entry<String, List<String>> per : permissionMap.entrySet()) {
                String key = per.getKey();
                boolean match = antPathMatcher.match(key, requestUrl);
                if (match) {
                    if (CollectionUtils.isNotEmpty(strings)) {
                        strings.addAll(per.getValue());
                    } else {
                        strings = new ArrayList<>();
                        strings.addAll(per.getValue());
                    }
                }
            }
            if (CollectionUtils.isEmpty(strings)) {
                return false;
            }
            return strings.stream().anyMatch(methodType -> antPathMatcher.match(methodType, method));
        }
        return false;
    }

    /**
     * @param byUserName 用户信息
     * @param useOrgCode 当前操作站点
     * @return
     */
    public Map<String, List<String>> getPermissionMap(SysUser byUserName, String useOrgCode) {
        SysDepart sysDepart = departDao.getByCode(useOrgCode);
        Map<String, List<String>> mapMap = new HashMap<>();
        List<SysRole> roleList = authRoleCommon.getSysRoles(byUserName, sysDepart);
        List<String> roleIds = roleList.stream().map(ro -> ro.getRoleId()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(roleIds)) {
            String joinKey = StringUtils.join(roleIds, "");
            List<SysPermission> sp = rolePermissionService.listRolePermission(roleIds,joinKey);
            if (CollectionUtils.isNotEmpty(sp)) {
                for (SysPermission sysPermission : sp) {
                    List<String> methods = mapMap.get(sysPermission.getPath());
                    if (methods == null) {
//                    首次加载接口路径
                        if (sysPermission.getPath() != null && sysPermission.getRequestMethod() != null) {
                            List<String> requestMethod = new ArrayList<>();
                            requestMethod.add(sysPermission.getRequestMethod());
                            mapMap.put(sysPermission.getPath(), requestMethod);
                        }
                    } else {
//                        二次添加请求方法
                        if (sysPermission.getPath() != null && sysPermission.getRequestMethod() != null) {
                            methods.add(sysPermission.getRequestMethod());
                        }
                    }
                }
            }
        }
        return mapMap;
    }

}
