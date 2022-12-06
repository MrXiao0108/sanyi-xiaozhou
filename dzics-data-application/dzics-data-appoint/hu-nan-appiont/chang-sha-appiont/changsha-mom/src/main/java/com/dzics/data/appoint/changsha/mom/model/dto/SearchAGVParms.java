package com.dzics.data.appoint.changsha.mom.model.dto;

import com.dzics.data.common.base.model.page.LimitBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author LiuDongFei
 * @date 2022年07月08日 20:39
 */
@Data
public class SearchAGVParms extends LimitBase implements Serializable {

    @ApiModelProperty(value = "产线id")
    private  String lineId;
    @ApiModelProperty(value = "投料点编号")
    private String externalCode;

}
