package com.dzics.data.pub.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pub.db.model.dto.LineListDo;
import com.dzics.data.pub.db.model.dto.Lines;
import com.dzics.data.pub.db.model.vo.SelectLineVo;
import com.dzics.data.pub.model.dto.LineDo;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.vo.OrderIdLineId;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 产线表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Mapper
@Repository
public interface DzProductionLineDao extends BaseMapper<DzProductionLine> {

    List<LineDo> list(SelectLineVo data);

    List<LineListDo> allLineList(@Param("useOrgCode") String useOrgCode);

    List<String> selectLineIdList();


    List<Lines> listOrderId(@Param("orderId") String orderId, @Param("useOrgCode") String useOrgCode);

    Long getLineEqmentId(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo);

    List<Lines> getByOerderId(@Param("orderId") Long ordeId);

    OrderIdLineId getOrderIdLineId(@Param("orderId") Long orderId, @Param("lineId") Long lineId);

    String getlineType(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo);
}
