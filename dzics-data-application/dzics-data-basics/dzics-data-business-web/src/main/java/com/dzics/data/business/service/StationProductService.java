package com.dzics.data.business.service;

import com.dzics.data.business.model.dto.locationartifacts.AddLocationArtifactsVo;
import com.dzics.data.business.model.dto.locationartifacts.PutLocationArtifactsVo;
import com.dzics.data.business.model.vo.locationartifacts.GetLocationArtifactsByIdDo;
import com.dzics.data.common.base.vo.Result;

/**
 * @Classname StationProductService
 * @Description 描述
 * @Date 2022/3/8 10:21
 * @Created by NeverEnd
 */
public interface StationProductService {
    Result add(AddLocationArtifactsVo addLocationArtifactsVo, String sub);

    Result<GetLocationArtifactsByIdDo> selEditProcedureProduct(String orderId, String lineId, String workStationProductId, String sub);

    Result updateProcedureProduct(PutLocationArtifactsVo putLocationArtifactsVo, String sub);

    Result delWorkStationProductId(String workStationProductId, String sub);
}
