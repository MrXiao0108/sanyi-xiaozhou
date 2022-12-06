package com.dzics.data.appoint.changsha.mom.model.dao;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author LiuDongFei
 * @date 2022年07月11日 15:50
 */
@Data
public class SearchAGVParmsDo {
    @ApiModelProperty("主键")
    @ExcelIgnore
    private String materialPointId;

    @ApiModelProperty("产线Id")
    @ExcelIgnore
    private String lineId;

    @ApiModelProperty("产线名称")
    @ExcelProperty("产线名称")
    private String lineName;

    @ApiModelProperty("投料点编号")
    @ExcelProperty("投料点编号")
    private String externalCode;

    @ApiModelProperty("投料点区域")
    @ExcelProperty("投料点区域")
    private String externalRegion;

    @ApiModelProperty("线路节点")
    @ExcelProperty("线路节点")
    private String lineNode;

    @ApiModelProperty("小车名称")
    @ExcelProperty("小车名称")
    private String inIslandCode;

    /**
     * 是否下料点
     * next_point
     */
    @ApiModelProperty("是否终点工序")
    @ExcelProperty("结束工位")
    private Boolean nextPoint;

    @ApiModelProperty("料点模式, NG （NG物料） TL (退库)  正常 不填写,下拉框，传递的值 就是 NG 或 TL ,正常的不传递，或传递空字符串 ")
    @ExcelProperty("料点模式")
    private String pointModel;

    @ApiModelProperty("绑定工位")
    @ExcelProperty("工位编号(三一)")
    private String stationId;

    @ApiModelProperty("料框料点类型")
    @ExcelProperty("料框料点类型")
    private String palletType;

}
