package com.dzics.data.pms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.db.dao.DzMaterialDao;
import com.dzics.data.pms.model.entity.DzMaterial;
import com.dzics.data.pms.service.DzMaterialService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 产品物料表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-08-16
 */
@Service
public class DzMaterialServiceImpl extends ServiceImpl<DzMaterialDao, DzMaterial> implements DzMaterialService {

    @Override
    public Result getMaterialByProductId(String productId) {

        QueryWrapper<DzMaterial>wrapper=new QueryWrapper<>();
        wrapper.eq("product_id",productId);
        List<DzMaterial> list = this.list(wrapper);
        return Result.ok(list);
    }
}
