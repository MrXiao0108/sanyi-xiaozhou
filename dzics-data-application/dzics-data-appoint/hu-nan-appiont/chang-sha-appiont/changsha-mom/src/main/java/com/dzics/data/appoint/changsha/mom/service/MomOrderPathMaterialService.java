package com.dzics.data.appoint.changsha.mom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrderPathMaterial;

import java.util.List;

/**
 * <p>
 * 工序物料表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
public interface MomOrderPathMaterialService extends IService<MomOrderPathMaterial> {


    /**
     * 根据工序ID 获取组件物料
     * @param workingProcedureId
     * @return
     */
    List<MomOrderPathMaterial> getMaterialNo(String workingProcedureId);
}
