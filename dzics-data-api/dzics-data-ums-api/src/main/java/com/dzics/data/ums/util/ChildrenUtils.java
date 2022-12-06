package com.dzics.data.ums.util;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.ums.model.entity.SysPermission;
import com.dzics.data.ums.model.vo.router.RoutersInfo;
import com.dzics.data.ums.model.vo.user.MetaInfo;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树形节点方法抽取
 *
 * @author ZhangChengJun
 * Date 2021/1/13.
 * @since
 */
public class ChildrenUtils {

    public static List<Map<String, Object>> treeSelectMap(List<RoutersInfo> parRoutEnd) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RoutersInfo routersInfo : parRoutEnd) {
            Map<String, Object> map = new HashMap<>();
            map.put("label", routersInfo.getMeta().getTitle());
            map.put("id", routersInfo.getId());
            if (routersInfo.getChildren() != null && !routersInfo.getChildren().isEmpty()) {
                List<Map<String, Object>> o = childrenAdd(routersInfo.getChildren());
                if (o != null && !o.isEmpty()) {
                    map.put("children", o);
                }
            }
            list.add(map);
        }
        return list;
    }

    public static List<Map<String, Object>> childrenAdd(List<RoutersInfo> routersInfos) {
        List<Map<String, Object>> c = new ArrayList<>();
        for (RoutersInfo child : routersInfos) {
            Map<String, Object> map = new HashMap<>();
            map.put("label", child.getMeta().getTitle());
            map.put("id", child.getId());
            if (child.getChildren() != null && !child.getChildren().isEmpty()) {
                List<Map<String, Object>> o = childrenAdd(child.getChildren());
                if (o != null && !o.isEmpty()) {
                    map.put("children", o);
                }

            }
            c.add(map);
        }
        return c;
    }

    public static List<RoutersInfo> getNextRouts(List<SysPermission> permissionsNext, String id) {
        List<RoutersInfo> routersInfos = new ArrayList<>();
        for (SysPermission permission : permissionsNext) {
            if (String.valueOf(permission.getParentId()).equals(id)) {
                routersInfos.add(permissionRoutersInfo(permission));
            }
        }
        return routersInfos;
    }

    public static RoutersInfo permissionRoutersInfo(SysPermission permission) {
        RoutersInfo routersInfo = new RoutersInfo();
        routersInfo.setId(String.valueOf(permission.getId()));
        routersInfo.setPid(String.valueOf(permission.getParentId()));
        routersInfo.setRedirect(permission.getRedirect());
        routersInfo.setPath(permission.getPath());
        routersInfo.setComponent(permission.getComponent());
        routersInfo.setHidden(permission.getHidden().intValue() == 0 ? false : true);
        routersInfo.setChildren(Lists.newArrayList());
        routersInfo.setMeta(new MetaInfo(false, permission.getIcon(), permission.getTitle()));
        routersInfo.setName(permission.getName());
        routersInfo.setAlwaysShow(permission.getAlwaysShow());
        return routersInfo;
    }

    public static List<RoutersInfo> parPermissionNext(List<SysPermission> list, List<SysPermission> permissionsNext) {
//        一级节点数据
        List<RoutersInfo> mapList = new ArrayList<>();
//      子节点数据
        for (SysPermission permission : list) {
            if (permission.getParentId().equals("0")) {
                mapList.add(permissionRoutersInfo(permission));
            } else {
                permissionsNext.add(permission);
            }

        }
        return mapList;
    }

    public static List<RoutersInfo> getChildRoutersInfo(List<RoutersInfo> parRout, List<SysPermission> permissionsNext) {
        List<RoutersInfo> routersInfos = new ArrayList<>();
        for (RoutersInfo routersInfo : parRout) {
            List<RoutersInfo> childList = getNextRouts(permissionsNext, routersInfo.getId());
            if (CollectionUtils.isNotEmpty(childList)) {
                routersInfo.setChildren(childList);
                getChildRoutersInfo(childList, permissionsNext);
            }
            routersInfos.add(routersInfo);
        }
        return routersInfos;
    }
}
