package com.dzics.data.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.model.dao.WorkNumberName;
import com.dzics.data.common.base.vo.Result;

import com.dzics.data.pms.model.dto.ProductParm;
import com.dzics.data.pms.model.entity.DzProduct;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;


/**
 * <p>
 * 产品列表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
public interface DzProductService extends IService<DzProduct> {


    @Cacheable(cacheNames = "dzProductService.getProductNo", key = "#modelNumber", unless = "#result == null")
    WorkNumberName getProductNo(String modelNumber);

    @Cacheable(cacheNames = "dzProductService.getProductType", key = "#productType", unless = "#result == null")
    WorkNumberName getProductType(String productType);

    DzProduct getSyProductNo(String syProductNo);

    String getNameAndOrder(String name, String lineType);


    Result getByOrderId(String sub, Integer page, Integer limit, Long departId);


    List<ProductParm> getByDepartId(Long departId);

    /**
     * @param productNo 产品序号
     * @return 返回产品关联站点
     */
    String getByProeuctNoDepartId(String productNo);

    /**
     * 查询用户下的产品列表
     * departId
     * @param sub
     * @return
     */
    Result getByProductId(String sub);

    /**
     * 根据产线类型获取产品信息
     * @param lineType
     * @return
     */
    Result getDepartLineType(String lineType);

    /**
     * 所有产品
     * @param sub
     * @return
     */
    Result selProduct(String sub,String lineType,String orgCode);
}
