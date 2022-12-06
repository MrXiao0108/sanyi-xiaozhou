package com.dzics.data.appoint.changsha.mom.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author LiuDongFei
 * @date 2022年07月09日 9:18
 */
@Data
public class AddMaterialVo {
    /**
     * 产线Id
     */
    @ApiModelProperty("产线Id")
    @NotNull(message = "产线Id必选")
    private Long lineId;

    @ApiModelProperty("产线名称")
    private String lineName;


    /**
     * 投料点编号
     */
    @ApiModelProperty("投料点编号")
    private String externalCode;

    /**
     * 投料点区域
     */
    @ApiModelProperty("投料点区域")
    private String externalRegion;

    /**
     * 线路节点
     */
    @ApiModelProperty("线路节点")
    private String lineNode;

    /**
     * 小车名称
     */
    @ApiModelProperty("小车名称")
    private String inIslandCode;

    /**
     * 绑定工位
     */
    @ApiModelProperty("绑定工位")
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
