package com.dzics.data.pub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.entity.DzWorkingStationProduct;
import com.dzics.data.pub.model.vo.locationartifacts.LocationArtifactsVo;

/**
 * <p>
 * 工位-工件关联关系表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-28
 */
public interface DzWorkingStationProductService extends IService<DzWorkingStationProduct> {
    Result locationArtifactsList(LocationArtifactsVo locationArtifactsVo, String sub);
}
