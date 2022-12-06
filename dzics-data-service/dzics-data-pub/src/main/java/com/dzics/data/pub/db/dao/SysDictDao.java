package com.dzics.data.pub.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.dzics.data.pub.model.entity.SysDict;
import com.dzics.data.pub.model.vo.DictVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 系统字典表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Mapper
@Repository
public interface SysDictDao extends BaseMapper<SysDict> {

    Integer hasDict(DictVo dictVo);
}
