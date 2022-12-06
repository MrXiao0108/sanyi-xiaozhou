package com.dzics.data.pms.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.common.base.model.dao.WorkNumberName;
import com.dzics.data.pms.model.dto.GetProductByOrderIdDo;
import com.dzics.data.pms.model.dto.ProductParm;
import com.dzics.data.pms.model.dto.Products;
import com.dzics.data.pms.model.entity.DzProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 产品列表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@Mapper
@Repository
public interface DzProductDao extends BaseMapper<DzProduct> {

    List<GetProductByOrderIdDo> getByOrderId(Long departId);

    List<DzProduct> listProduct(@Param("field") String field,
                                  @Param("type") String type, @Param("productName") String productName,
                                  @Param("useOrgCode") String useOrgCode, @Param("lineType") String lineType);

    List<ProductParm> getByDepartId(@Param("departId") Long departId);

    String getByProeuctNoDepartId(@Param("productNo") String productNo);

    WorkNumberName getProductNo(@Param("modelNumber") String modelNumber);

    WorkNumberName getProductType(@Param("productType") String productType);

    List<Products> selProducts(@Param("lineType") String lineType,@Param("useOrgCode")String useOrgCode);

    List<Products> getDepartLineType(@Param("lineType") String lineType);
}
