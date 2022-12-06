package com.dzics.data.appoint.changsha.mom.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.appoint.changsha.mom.model.dao.MomMaterialPointDo;
import com.dzics.data.appoint.changsha.mom.model.dto.MomUpPoint;
import com.dzics.data.appoint.changsha.mom.model.dto.SearchAGVParms;
import com.dzics.data.appoint.changsha.mom.model.entity.MomMaterialPoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 料点编码 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-11-02
 */
@Mapper
@Repository
public interface MomMaterialPointDao extends BaseMapper<MomMaterialPoint> {

    MomUpPoint getStationCode(@Param("basketType") String basketType, @Param("orderCode") String orderCode, @Param("lineNo") String lineNo);

   default List<String> getOrderNoLineNo(String orderNo, String lineNo){
       QueryWrapper<MomMaterialPoint> wp = new QueryWrapper<>();
       wp.eq("order_no", orderNo);
       wp.eq("line_no", lineNo);
       List<MomMaterialPoint> momMaterialPoints = selectList(wp);
       if (CollectionUtils.isNotEmpty(momMaterialPoints)) {
           return momMaterialPoints.stream().map(MomMaterialPoint::getStationId).distinct().collect(Collectors.toList());
       }
       return null;
   }

    List<MomMaterialPointDo> getAllPoints(SearchAGVParms parms);



}
