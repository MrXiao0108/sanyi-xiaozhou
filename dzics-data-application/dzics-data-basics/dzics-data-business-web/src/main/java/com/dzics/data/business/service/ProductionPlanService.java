package com.dzics.data.business.service;

import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.db.model.dao.SelectEquipmentProductionDo;
import com.dzics.data.pdm.db.model.dao.SelectProductionDetailsDo;
import com.dzics.data.pdm.db.model.dto.SelectEquipmentProductionVo;
import com.dzics.data.pdm.db.model.dto.SelectProductionDetailsVo;
import com.dzics.data.pdm.db.model.dto.SelectProductionPlanVo;
import com.dzics.data.pdm.db.model.vo.ProductionPlanDo;
import com.dzics.data.pdm.model.dao.SelectEquipmentProductionDetailsVo;

import java.util.List;

public interface ProductionPlanService {
    Result<ProductionPlanDo> list(String sub, PageLimit pageLimit, SelectProductionPlanVo selectProductionPlanVo);

    Result<ProductionPlanDo> put(String sub, ProductionPlanDo productionPlanDo);

    /**
     * 查询日生产计划记录列表
     * @param sub
     * @param pageLimit
     * @return
     */
    Result list(String sub,PageLimit pageLimit);

    /**
     * 查询日生产计划记录详情列表
     * @param planId
     * @param detectorTime
     * @return
     */
    Result detailsList(Long planId, String detectorTime);

    Result<List<SelectProductionDetailsDo>> list(String sub, PageLimit pageLimit, SelectProductionDetailsVo selectProductionDetailsVo);

    /**
     * 设备生产数量明细
     * @param sub
     * @param pageLimit
     * @param dto
     * @return
     */
    Result<List<SelectEquipmentProductionDo>> listProductionEquipment(String sub, PageLimitBase pageLimit, SelectEquipmentProductionVo dto);

    Result listProductionEquipmentDetails(String sub, SelectEquipmentProductionDetailsVo selectEquipmentProductionDetailsVo);
}
