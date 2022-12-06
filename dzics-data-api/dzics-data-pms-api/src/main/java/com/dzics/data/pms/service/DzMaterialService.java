package com.dzics.data.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.model.entity.DzMaterial;


/**
 * <p>
 * 产品物料表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-08-16
 */
public interface DzMaterialService extends IService<DzMaterial> {

    Result getMaterialByProductId(String productId);
}
