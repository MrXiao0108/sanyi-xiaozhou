package com.dzics.data.ums.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.ums.model.entity.SysRole;
import com.dzics.data.ums.model.entity.SysUserRole;
import com.dzics.data.common.base.mybatis.DzicsBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Mapper
@Repository
public interface SysUserRoleDao extends DzicsBaseMapper<SysUserRole> {

    List<SysRole> getListRoleCode(@Param("id") String id, @Param("useOrgCode") String useOrgCode);

    List<String> listRoleId(@Param("id") Long id, @Param("useOrgCode") String useOrgCode, @Param("code") Integer code);

    default void removeUserId(String delUser) {
        QueryWrapper<SysUserRole> wpUsRo = new QueryWrapper<>();
        wpUsRo.eq("user_id", delUser);
        delete(wpUsRo);
    }

    default Integer countRoleUser(String delRole) {
        QueryWrapper<SysUserRole> wpUsRo = new QueryWrapper<>();
        wpUsRo.eq("role_id", delRole);
        return selectCount(wpUsRo);
    }
}
