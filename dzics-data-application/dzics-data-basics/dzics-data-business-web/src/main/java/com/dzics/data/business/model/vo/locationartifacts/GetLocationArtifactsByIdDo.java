package com.dzics.data.business.model.vo.locationartifacts;

import com.dzics.data.pms.model.dto.Products;
import com.dzics.data.pms.model.vo.DzDetectTempVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetLocationArtifactsByIdDo {
    @ApiModelProperty("工序-工件关联关系ID")
    private String workStationProductId;

    @ApiModelProperty("工件信息")
    private Products product;

    @ApiModelProperty("检测项选择数据")
    List<DzDetectTempVo> dzDetectTempVos;
}
