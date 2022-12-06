package com.dzics.data.pms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.dao.WorkNumberName;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.db.dao.DzProductDao;
import com.dzics.data.pms.model.dto.*;
import com.dzics.data.pms.model.entity.DzProduct;
import com.dzics.data.pms.service.DzProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 产品列表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@Service
@Slf4j
public class DzProductServiceImpl extends ServiceImpl<DzProductDao, DzProduct> implements DzProductService {

    @Autowired
    private DzProductDao dzProductMapper;
    @Override
    public WorkNumberName getProductNo(String modelNumber) {
        WorkNumberName productNo = this.baseMapper.getProductNo(modelNumber);
        if (productNo == null) {
            productNo = new WorkNumberName();
            productNo.setProductName("默认产品");
            productNo.setModelNumber("-9999.999");
        }
        return productNo;
    }

    @Override
    public WorkNumberName getProductType(String productType) {
        return this.baseMapper.getProductType(productType);
    }

    @Override
    public DzProduct getSyProductNo(String syProductNo) {
        QueryWrapper<DzProduct> wp = new QueryWrapper<>();
        wp.eq("sy_productNo", syProductNo);
        List<DzProduct> list = list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            DzProduct dzProduct = list.get(0);
            return dzProduct;
        }
        return null;
    }

    @Override
    public String getNameAndOrder(String name, String lineType) {
        try {
            QueryWrapper<DzProduct> wp = new QueryWrapper<>();
            wp.eq("product_name", name);
            wp.eq("line_type", lineType);
            DzProduct dzProduct = getOne(wp);
            if (dzProduct != null) {
                return dzProduct.getProductNo();
            }
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR88);
        } catch (Throwable throwable) {
            log.error("根据产品名称：{}，产线类型：{} 查询产品失败", name, lineType, throwable);
        }
        return "SYAN-000000";
    }


    /**
     * 产品 添加修改  检测序号和名称是否重复
     *
     * @param type         1检测名称  2检测序号(id) 3检查产品简码 4.检查产品物料编码
     * @param addProductVo
     * @return
     */
    public boolean isExistProduct(int type, AddProductVo addProductVo) {
        QueryWrapper<DzProduct> eq = new QueryWrapper<DzProduct>();
        if (type == 1) {
            eq.eq("line_type", addProductVo.getLineType());
            eq.eq("product_name", addProductVo.getProductName());
        } else if (type == 2) {
            eq.eq("product_no", addProductVo.getProductNo());
        } else if (type == 3) {
            eq.eq("line_type", addProductVo.getLineType());
            eq.eq("sy_product_alias", addProductVo.getSyProductAlias());
        } else if (type == 4) {
            eq.eq("line_type", addProductVo.getLineType());
            eq.eq("sy_productNo", addProductVo.getSyProductNo());
        }
        List<DzProduct> dzProducts = dzProductMapper.selectList(eq);
        if (dzProducts.size() > 0) {
            return true;
        }
        return false;
    }


    @Override
    public Result getByOrderId(String sub, Integer page, Integer limit, Long departId) {
        PageHelper.startPage(page, limit);
        List<GetProductByOrderIdDo> list = dzProductMapper.getByOrderId(departId);
        PageInfo<GetProductByOrderIdDo> info = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Override
    public List<ProductParm> getByDepartId(Long departId) {
        return dzProductMapper.getByDepartId(departId);
    }

    @Override
    public String getByProeuctNoDepartId(String productNo) {
        return dzProductMapper.getByProeuctNoDepartId(productNo);
    }

    @Override
    public Result getByProductId(String departId) {

        QueryWrapper<DzProduct> queryWrapper = new QueryWrapper<DzProduct>()
                .select("product_no", "product_name")
                .eq("depart_id", departId);
        List<DzProduct> dzProducts = dzProductMapper.selectList(queryWrapper);
        return new Result(CustomExceptionType.OK, dzProducts);
    }

    @Override
    public Result getDepartLineType(String lineType) {
        List<Products> productVos = dzProductMapper.getDepartLineType(lineType);
        return Result.OK(productVos);
    }

    @Override
    public Result selProduct(String sub, String lineType,String orgCode) {
        List<Products> dzProducts = dzProductMapper.selProducts(lineType,orgCode);
        if (CollectionUtils.isNotEmpty(dzProducts)) {
            for (Products dzProduct : dzProducts) {
                dzProduct.setProductName(dzProduct.getProductName() + "-" + dzProduct.getLineType());
            }
        }
        return Result.ok(dzProducts, Long.valueOf(dzProducts.size()));
    }
}
