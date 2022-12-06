package com.dzics.data.pub.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DepartParms {
    /**
     * 站点ID
     */
    @ApiModelProperty("站点ID")
    private String departId;
}
