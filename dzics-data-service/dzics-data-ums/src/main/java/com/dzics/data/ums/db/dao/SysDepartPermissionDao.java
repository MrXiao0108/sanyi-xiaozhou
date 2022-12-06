package com.dzics.data.ums.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.ums.model.entity.SysDepartPermission;
import com.dzics.data.common.base.mybatis.DzicsBaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-13
 */
@Mapper
public interface SysDepartPermissionDao extends DzicsBaseMapper<SysDepartPermission> {
    default void removeDepartId(Long departId) {
        QueryWrapper<SysDepartPermission> wpDepPermi = new QueryWrapper<>();
        wpDepPermi.eq("depart_id", departId);
        delete(wpDepPermi);
    }

    default List<SysDepartPermission> listDepartIdPerMission(Long affiliationDepartId) {
        QueryWrapper<SysDepartPermission> wpDepPermi = new QueryWrapper<>();
        wpDepPermi.eq("depart_id", affiliationDepartId);
        return selectList(wpDepPermi);
    }

    default Integer selectByPerId(Long delPermission) {
        QueryWrapper<SysDepartPermission> wpDepPermi = new QueryWrapper<>();
        wpDepPermi.eq("permission_id", delPermission);
        return selectCount(wpDepPermi);
    }
}
