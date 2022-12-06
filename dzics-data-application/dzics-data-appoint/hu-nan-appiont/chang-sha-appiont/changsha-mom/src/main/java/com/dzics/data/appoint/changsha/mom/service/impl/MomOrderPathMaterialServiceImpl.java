package com.dzics.data.appoint.changsha.mom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.db.dao.MomOrderPathMaterialDao;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrderPathMaterial;
import com.dzics.data.appoint.changsha.mom.service.MomOrderPathMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 工序物料表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
@Service
@Slf4j
public class MomOrderPathMaterialServiceImpl extends ServiceImpl<MomOrderPathMaterialDao, MomOrderPathMaterial> implements MomOrderPathMaterialService {

    @Override
    public List<MomOrderPathMaterial> getMaterialNo(String workingProcedureId) {
        QueryWrapper<MomOrderPathMaterial> wp = new QueryWrapper<>();
        wp.eq("working_procedure_id", workingProcedureId);
        List<MomOrderPathMaterial> list = list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        } else {
            log.warn("根据工序ID获取组件物料失败：workingProcedureId : {}, list:{}", list);
            return null;
        }
    }
}
