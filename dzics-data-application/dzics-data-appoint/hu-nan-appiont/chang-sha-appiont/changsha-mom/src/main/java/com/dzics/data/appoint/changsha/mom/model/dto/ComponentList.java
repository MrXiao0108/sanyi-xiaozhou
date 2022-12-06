package com.dzics.data.appoint.changsha.mom.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ComponentList {
    /**
     *组件物料
     */
    @JsonProperty(value = "MaterialNo")
    private String MaterialNo;
    /**
     *组件物料描述
     */
    @JsonProperty(value = "MaterialName")
    private String MaterialName;
    /**
     *组件物料简码
     */
    @JsonProperty(value = "MaterialAlias")
    private String MaterialAlias;

    /**
     *组件数量
     */
    @JsonProperty(value = "Quantity")
    private int Quantity;


    private String paramRsrv1;

    private String paramRsrv2;

}
