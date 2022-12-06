package com.dzics.data.business.service;

import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.model.dto.AddProductVo;
import com.dzics.data.pms.model.dto.ProductListModel;
import com.dzics.data.pms.model.entity.DzProduct;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

public interface ProductService {
    /**
     * 产品列表查询
     *
     * @param sub
     * @param productListModel
     * @return
     */
    Result<List<DzProduct>> list(String sub, ProductListModel productListModel);

    Result getById(String sub, Long productId);

    /**
     * 添加产品
     *
     * @param sub
     * @param addProductVo
     * @return
     */
    @CacheEvict(value = {"dzDetectionTemplCache.getByDepartId","cacheService.getProductNameFrequency"}, key = "#addProductVo.departId")
    Result add(String sub, AddProductVo addProductVo);

    @CacheEvict(cacheNames = {"dzProductService.getProductNo","dzDetectionTemplCache.getByOrderNoProNo", "dzDetectionTemplCache.getGroupKey", "cacheService.getProductNo","cacheService.getProductType","cacheService.getProductNameFrequency"}, allEntries = true)
    Result put(String sub, AddProductVo addProductVo);

    @CacheEvict(cacheNames = {"dzProductService.getProductNo","dzDetectionTemplCache.getByOrderNoProNo", "cacheService.getProductNo","cacheService.getProductType","cacheService.getProductNameFrequency"}, allEntries = true)
    Result del(String sub, Long productId);
}
