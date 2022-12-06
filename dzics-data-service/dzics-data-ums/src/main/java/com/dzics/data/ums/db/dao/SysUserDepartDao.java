package com.dzics.data.ums.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.ums.model.entity.SysUserDepart;
import com.dzics.data.common.base.mybatis.DzicsBaseMapper;
import org.apache.ibatis.annotations.Mapper;

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
public interface SysUserDepartDao extends DzicsBaseMapper<SysUserDepart> {

    default List<SysUserDepart> listByUserIdOrgcodeNeDepartId(String userId, String useOrgCode, String departId) {
        QueryWrapper<SysUserDepart> userDepQwp = new QueryWrapper<>();
        userDepQwp.eq("user_id", userId);
        userDepQwp.ne("depart_id", departId);
        return selectList(userDepQwp);
    }

    default void removeUserId(String delUser) {
        QueryWrapper<SysUserDepart> wpUsDep = new QueryWrapper<>();
        wpUsDep.eq("user_id",delUser);
        delete(wpUsDep);
    }
}
