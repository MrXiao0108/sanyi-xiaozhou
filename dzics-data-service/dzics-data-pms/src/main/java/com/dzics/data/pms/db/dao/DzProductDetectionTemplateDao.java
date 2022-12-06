package com.dzics.data.pms.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pms.model.dto.DzProductDetectionTemplateParms;
import com.dzics.data.pms.model.dto.ProductTemp;
import com.dzics.data.pms.model.dto.TableColumnDto;
import com.dzics.data.pms.model.entity.DzProductDetectionTemplate;
import com.dzics.data.pms.model.vo.DBDetectTempVo;
import com.dzics.data.pms.model.vo.DzDetectTempVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

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
public interface DzProductDetectionTemplateDao extends BaseMapper<DzProductDetectionTemplate> {

    List<DzDetectTempVo> groupById(@Param("groupId") String groupId);

    List<DzProductDetectionTemplateParms> listGroupBy(@Param("field") String field, @Param("type") String type, @Param("productName") String productName, @Param("departId") String departId, @Param("orderId") String orderId, @Param("lineId") String lineId);

    List<DBDetectTempVo> geteditdbdetectoritem(@Param("groupId") String groupId);

    Integer updateTemplate(@Param("oldProductNo") String productNo, @Param("newProductNo") String newProductNo);

    List<ProductTemp> getDzProDetectIonTemp(@Param("productNo") String productNo);

    List<DzProductDetectionTemplate> getOneObj(@Param("productNo") String productNo);

    List<TableColumnDto> listMap(@Param("productNo") String productNo, @Param("orderNo") String orderNo, @Param("lineNo") String lineNo);

    List<DzDetectTempVo> productId(@Param("orderId") String orderId, @Param("lineId") String lineId, @Param("productNo") String productNo);

    DzProductDetectionTemplate getProductNoItem(@Param("productNo") String productNo, @Param("item") String item);

    List<Map<String, Object>> listProductIdMap(@Param("productNo") String productNo);

    List<TableColumnDto> getDefoutDetectionTemp();

    List<DzProductDetectionTemplate> selOrderLineProductNo(@Param("productNo") String productNo, @Param("show") int show, @Param("lineId") String lineId, @Param("orderId") String orderId);
}
