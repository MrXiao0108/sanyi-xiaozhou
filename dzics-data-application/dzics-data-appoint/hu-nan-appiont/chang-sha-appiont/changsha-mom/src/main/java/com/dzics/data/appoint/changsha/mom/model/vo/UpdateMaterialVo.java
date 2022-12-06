package com.dzics.data.appoint.changsha.mom.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author Administrator
 */
@Data
public class UpdateMaterialVo {
    @ApiModelProperty(value = "唯一键", required = true)
    @NotEmpty(message = "唯一主键必填")
    private String materialPointId;

    @ApiModelProperty(value = "产线ID")
    @NotEmpty(message = "产线ID必填")
    private String lineId;

    /**
     * 投料点编号
     */
    @ApiModelProperty(value = "投料点编号必填")
    private String externalCode;

    /**
     * 投料点区域
     */
    @ApiModelProperty(value = "投料点区域")
    private String externalRegion;


    /**
     * 线路节点
     */
    @ApiModelProperty(value = "线路节点")
    private String lineNode;

    /**
     * 小车名称
     */
    @ApiModelProperty(value = "小车编号")
    private String inIslandCode;

    /**
     * 绑定工位
     */
    @ApiModelProperty(value = "工位编号")
    private String stationId;


    /**
     * 料点模式
     */
    @ApiModelProperty("料点模式, NG （NG物料） TL (退库)  正常 不填写,下拉框，传递的值 就是 NG 或 TL ,正常的不传递，或传递空字符串 ")
    private String pointModel;

    /**
     * 是否下料点
     * next_point
     */
    @ApiModelProperty("是否终点工序")
    private Boolean nextPoint;

    @ApiModelProperty("料点料框类型")
    private String palletType;
}
