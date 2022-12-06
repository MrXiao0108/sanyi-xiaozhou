package com.dzics.data.ums.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.ums.model.entity.SysRole;
import com.dzics.data.common.base.enums.BasicsRole;
import com.dzics.data.common.util.UnderlineTool;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Mapper
public interface SysRoleDao extends BaseMapper<SysRole> {

    SysRole selRoleCode(@Param("roleCode") String roleCode);

    List<String> getRoleName(Long id);

    default SysRole getRole(String affiliationDepartId, Integer code) {
        QueryWrapper<SysRole> wpRole = new QueryWrapper<>();
        wpRole.eq("depart_id", affiliationDepartId);
        wpRole.eq("basics_role", BasicsRole.JC.getCode());
        SysRole sysRole = selectOne(wpRole);
        return sysRole;
    }

    default void updateDepartId(Long departId, Integer status) {
        QueryWrapper<SysRole> wpRole = new QueryWrapper<>();
        wpRole.eq("depart_id", departId);
        SysRole sysRole = new SysRole();
        sysRole.setStatus(status);
        update(sysRole, wpRole);
    }


    default List<SysRole> listNoBasicsRole(String field, String type, String useOrgCode, String roleName, String roleCode, Integer status, Date createTime, Date endTime) {
        QueryWrapper<SysRole> rwp = new QueryWrapper<>();
        rwp.eq("org_code", useOrgCode);
        rwp.ne("basics_role", BasicsRole.JC.getCode());
        if (!StringUtils.isEmpty(roleCode)) {
            rwp.eq("role_code", roleCode);
        }
        if (status != null) {
            rwp.eq("status", status);
        }
        if (createTime != null) {
            rwp.ge("create_time", createTime);
        }
        if (endTime != null) {
            rwp.le("create_time", endTime);
        }
        if (!StringUtils.isEmpty(roleName)){
            rwp.and(wp -> wp.like("role_name", roleName).or().like("description", roleName));
        }
        if(!StringUtils.isEmpty(type)){
            if("DESC".equals(type)){
                rwp.orderByDesc(UnderlineTool.humpToLine(field));
            } else if("ASC".equals(type)){
                rwp.orderByAsc(UnderlineTool.humpToLine(field));
            }
        }
        List<SysRole> sysRoles = selectList(rwp);
        return sysRoles;
    }
}
