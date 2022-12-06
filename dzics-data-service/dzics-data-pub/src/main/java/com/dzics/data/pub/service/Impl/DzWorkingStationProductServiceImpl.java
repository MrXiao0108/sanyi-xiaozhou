package com.dzics.data.pub.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.db.dao.DzWorkingStationProductDao;
import com.dzics.data.pub.model.dao.LocationArtifactsDo;
import com.dzics.data.pub.model.entity.DzWorkingStationProduct;
import com.dzics.data.pub.model.vo.locationartifacts.LocationArtifactsVo;
import com.dzics.data.pub.service.DzWorkingStationProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 工位-工件关联关系表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-28
 */
@Service
public class DzWorkingStationProductServiceImpl extends ServiceImpl<DzWorkingStationProductDao, DzWorkingStationProduct> implements DzWorkingStationProductService {

    @Override
    public Result locationArtifactsList(LocationArtifactsVo locationArtifactsVo, String sub) {
        PageHelper.startPage(locationArtifactsVo.getPage(), locationArtifactsVo.getLimit());
        List<LocationArtifactsDo> product = this.baseMapper.locationArtifactsList(locationArtifactsVo);
        PageInfo<LocationArtifactsDo> info = new PageInfo<>(product);
        return Result.ok(info.getList(), Long.valueOf(info.getTotal()));
    }

}
