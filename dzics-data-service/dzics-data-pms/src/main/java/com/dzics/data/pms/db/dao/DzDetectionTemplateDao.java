package com.dzics.data.pms.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pms.model.entity.DzDetectionTemplate;
import com.dzics.data.pms.model.vo.DzDetectTempVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 产品检测设置默认模板 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@Mapper
public interface DzDetectionTemplateDao extends BaseMapper<DzDetectionTemplate> {

    List<DzDetectTempVo> listDzDetectTempVo();

}
