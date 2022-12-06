package com.dzics.data.maintain.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.maintain.model.dao.CheckItemTypeDo;
import com.dzics.data.maintain.model.entity.DzCheckUpItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 巡检项设置 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
public interface DzCheckUpItemDao extends BaseMapper<DzCheckUpItem> {
    List<CheckItemTypeDo> getCheckItemType(@Param("orgCode") String orgCode, @Param("deviceType") Integer deviceType, @Param("checkName") String checkName);
}
