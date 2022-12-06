package com.dzics.data.pms.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pms.model.dto.DetectionData;
import com.dzics.data.pms.model.entity.DzDetectorData;
import com.dzics.data.pms.model.vo.SelectTrendChartVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品检测设置默认模板 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@Mapper
@Repository
public interface DzDetectorDataDao extends BaseMapper<DzDetectorData> {

    List<Map<String, Object>> getGroupKey(@Param("list") List<String> list);

    List<DetectionData> getDataValue(@Param("groupKey") String groupKey);

    List<BigDecimal> selectTrendChart(SelectTrendChartVo chartParms);

    List<BigDecimal> getChartsData(@Param("productNo") String productNo, @Param("orderNo") String orderNo, @Param("lineNo") String lineNo);

    List<DzDetectorData> groupBuby(@Param("productNo") String productNo, @Param("detectionResult") Integer detectionResult, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("orgCode") String orgCode);

    List<DzDetectorData> groupBubyData();

}
