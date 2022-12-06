package com.dzics.data.pub.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pub.model.entity.SysConfig;
import com.dzics.data.pub.model.vo.RunDataModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统运行模式 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-31
 */
public interface SysConfigDao extends BaseMapper<SysConfig> {

    RunDataModel systemRunModel(@Param("rumModel") String rumModel);

    List<String> getMouthDate(@Param("year") int year, @Param("monthValue") int monthValue);

}
