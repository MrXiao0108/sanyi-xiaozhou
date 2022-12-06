package com.dzics.data.appoint.changsha.mom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.entity.MomMaterialWarehouse;

/**
 * <p>
 * 物料仓库 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2022-05-12
 */
public interface MomMaterialWarehouseService extends IService<MomMaterialWarehouse> {

    MomMaterialWarehouse getOrderAndMaterial(String order_no, String lineNo, String materialNo);
}
