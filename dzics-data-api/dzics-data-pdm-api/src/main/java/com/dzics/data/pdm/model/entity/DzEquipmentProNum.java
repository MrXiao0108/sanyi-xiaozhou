package com.dzics.data.pdm.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dzics.data.common.base.model.dto.CmdTcp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 设备生产数量表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_equipment_pro_num")
@ApiModel(value = "DzEquipmentProNum对象", description = "班次生产记录表")
public class DzEquipmentProNum implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type =IdType.AUTO)
    private Long id;

    @ApiModelProperty("每日排班id")
    @TableField("day_id")
    private String dayId;

    @ApiModelProperty("订单编号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty("产线序号")
    @TableField("line_no")
    private String lineNo;
    /**
     * 设备id
     */
    @ApiModelProperty("设备id")
    @TableField("equiment_id")
    private String equimentId;

    @ApiModelProperty(value = "批次号")
    @TableField("batch_number")
    private String batchNumber;

    @ApiModelProperty(value = "产品编码")
    @TableField("model_number")
    private String modelNumber;

    @ApiModelProperty("产品类型")
    @TableField("product_type")
    private String productType;
    /**
     * 毛坯数量
     */
    @ApiModelProperty("毛坯数量")
    @TableField("rough_num")
    private Long roughNum;

    /**
     * 合格数量
     */
    @ApiModelProperty("合格数量")
    @TableField("qualified_num")
    private Long qualifiedNum;

    /**
     * 当前产量
     */
    @ApiModelProperty(value = "当前产量")
    @TableField("now_num")
    private Long nowNum;

    /**
     * 生产总量
     */
    @ApiModelProperty(value = "生产总量")
    @TableField("total_num")
    private Long totalNum;

    /**
     * 不良品数量
     */
    @ApiModelProperty("不良品数量")
    @TableField("badness_num")
    private Long badnessNum;

    /**
     * 工作日期
     */
    @TableField("work_data")
    private LocalDate workData;

    @TableField("work_year")
    private Integer workYear;

    @TableField("work_mouth")
    private String workMouth;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    private String orgCode;

    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @TableField("del_flag")
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    @ApiModelProperty("小时")
    @TableField("work_hour")
    private Integer workHour;
    /**
     * 设备生产的工件编号,工件名称 的指令信息
     */
    @TableField(exist = false)
    private List<CmdTcp> cmdTcpList;

}
