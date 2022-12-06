package com.dzics.data.pub.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pub.model.entity.DzWorkingProcedure;
import com.dzics.data.pub.model.vo.SelProcedureProduct;
import com.dzics.data.pub.model.vo.WorkingProcedureRes;
import com.dzics.data.pub.model.vo.WorkingProcedures;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 工序表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
@Mapper
public interface DzWorkingProcedureDao extends BaseMapper<DzWorkingProcedure> {

    List<WorkingProcedureRes> selWorkingProcedure(@Param("field") String field, @Param("type") String type, @Param("orderId") String orderId, @Param("lineId") String lineId,
                                                  @Param("workCode") String workCode, @Param("workName") String workName, @Param("useDepartId") String useDepartId);

    List<SelProcedureProduct> selProcedureProduct(@Param("productNo") String productNo, @Param("workingProcedureId") String workingProcedureId);

    List<WorkingProcedures> getWorkingProcedures();

}
