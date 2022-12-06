package com.dzics.data.ums.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.ums.model.dao.SwitchSiteDo;
import com.dzics.data.ums.model.dto.depart.ResSysDepart;
import com.dzics.data.ums.model.entity.SysDepart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 站点公司表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Mapper
@Repository
public interface SysDepartDao  extends BaseMapper<SysDepart> {

    List<SysDepart> getDepart(String orgCode);

    @Cacheable(cacheNames = "departService.getByCode",key = "#useOrgCode",unless = "#result == null")
    SysDepart getByCode(@Param("useOrgCode") String useOrgCode);

    List<SwitchSiteDo> listId(@Param("list") List<String> list);

    @Cacheable(value = "businessDepartService.listAll")
    List<SwitchSiteDo> listAll();

    SwitchSiteDo getByOrgCode(@Param("orgCode") String orgCode);

    SysDepart selectByLineId(String lineId);

    default ResSysDepart getByParentId(int i) {
        QueryWrapper<SysDepart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", 0);
        SysDepart sysDepart = selectOne(queryWrapper);
        ResSysDepart resSysDepart = null;
        if (sysDepart != null) {
            resSysDepart = new ResSysDepart();
            BeanUtils.copyProperties(sysDepart, resSysDepart);
            resSysDepart.setParentId(sysDepart.getParentId());
            resSysDepart.setId(sysDepart.getId());
        }
        return resSysDepart;
    }


    default List<ResSysDepart> listNotDz() {
        QueryWrapper<SysDepart> departQueryWrapper = new QueryWrapper<>();
        departQueryWrapper.ne("parent_id", 0);
        List<SysDepart> departs = selectList(departQueryWrapper);
        List<ResSysDepart> resSysDeparts = new ArrayList<>();
        departs.stream().forEach(s -> {
            ResSysDepart resSysDepart = new ResSysDepart();
            BeanUtils.copyProperties(s, resSysDepart);
            resSysDepart.setId(String.valueOf(s.getId()));
            resSysDepart.setParentId(String.valueOf(s.getParentId()));
            resSysDeparts.add(resSysDepart);
        });
        return resSysDeparts;
    }
}
