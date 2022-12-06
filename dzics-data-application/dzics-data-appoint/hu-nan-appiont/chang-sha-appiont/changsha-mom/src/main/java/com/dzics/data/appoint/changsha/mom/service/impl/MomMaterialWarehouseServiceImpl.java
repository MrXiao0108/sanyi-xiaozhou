package com.dzics.data.appoint.changsha.mom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.db.dao.MomMaterialWarehouseDao;
import com.dzics.data.appoint.changsha.mom.model.entity.MomMaterialWarehouse;
import com.dzics.data.appoint.changsha.mom.service.MomMaterialWarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 物料仓库 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2022-05-12
 */
@Service
@Slf4j
public class MomMaterialWarehouseServiceImpl extends ServiceImpl<MomMaterialWarehouseDao, MomMaterialWarehouse> implements MomMaterialWarehouseService {

    @Autowired
    private MomMaterialWarehouseDao momMaterialWarehouseDao;

    @Override
    public MomMaterialWarehouse getOrderAndMaterial(String order_no, String lineNo, String materialNo) {
        return momMaterialWarehouseDao.getOrderAndMaterial(order_no, lineNo, materialNo);
    }
}
