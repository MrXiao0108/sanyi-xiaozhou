package com.dzics.data.pub.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pub.model.entity.DzWorkingProcedureProduct;
import com.dzics.data.pub.model.vo.WorkingProcedures;

import java.util.List;

/**
 * <p>
 * 工序-工件关联关系表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
public interface DzWorkingProcedureProductDao extends BaseMapper<DzWorkingProcedureProduct> {
    List<WorkingProcedures> getWorkingProcedures();
}
