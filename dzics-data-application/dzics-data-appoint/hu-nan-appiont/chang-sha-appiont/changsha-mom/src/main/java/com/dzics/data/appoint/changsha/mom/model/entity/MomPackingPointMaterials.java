package com.dzics.data.appoint.changsha.mom.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author xnb
 * @since 2022-10-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_packing_point_materials")
@ApiModel(value="MomPackingPointMaterials对象", description="")
public class MomPackingPointMaterials implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "订单ID")
    @TableField("order_id")
    private String orderId;

    @ApiModelProperty(value = "产线ID")
    @TableField("line_id")
    private String lineId;

    @ApiModelProperty(value = "订单编号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty(value = "产线编号")
    @TableField("line_no")
    private String lineNo;

    @ApiModelProperty(value = "物料编号")
    @TableField("point_code")
    private String pointCode;

    @ApiModelProperty(value = "物料信息")
    @TableField("materials")
    private String materials;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;


}
