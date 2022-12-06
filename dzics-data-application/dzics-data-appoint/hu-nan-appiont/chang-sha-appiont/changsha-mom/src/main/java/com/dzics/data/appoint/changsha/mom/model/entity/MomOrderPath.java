package com.dzics.data.appoint.changsha.mom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 订单工序组
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_order_path")
@ApiModel(value = "MomOrderPath对象", description = "订单工序组")
public class MomOrderPath implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "working_procedure_id", type = IdType.ASSIGN_ID)
    private String workingProcedureId;

    @ApiModelProperty(value = "mom 订单 id")
    @TableField("mom_order_id")
    private String momOrderId;

    /**
     * 订单号
     */
    @TableField("WipOrderNo")
    private String WipOrderNo;

    /**
     *产品物料号
     */
    @TableField("ProductNo")
    private String ProductNo;
    /**
     *订单物料简码
     */
    @TableField("ProductAlias")
    private String ProductAlias;
    /**
     *工厂
     */
    @TableField("Facility")
    private String Facility;
    /**
     *产线
     */
    @TableField("ProductionLine")
    private String ProductionLine;
    /**
     *顺序号
     */
    @TableField("SequenceNo")
    private String SequenceNo;
    /**
     *工序号
     */
    @TableField("OprSequenceNo")
    private String OprSequenceNo;
    /**
     *工序名称
     */
    @TableField("OprSequenceName")
    private String OprSequenceName;

    /**
     *计划开始时间
     */
    @TableField("ScheduledStartDate")
    private String ScheduledStartDate;
    /**
     *计划结束时间
     */
    @TableField("ScheduledCompleteDate")
    private String ScheduledCompleteDate;
    /**
     *工序类型
     */
    @TableField("OprSequenceType")
    private String OprSequenceType;
    /**
     *工作中心
     */
    @TableField("WorkCenter")
    private String WorkCenter;
    /**
     *工作中心描述
     */
    @TableField("WorkCenterName")
    private String WorkCenterName;
    /**
     *工位
     */
    @TableField("WorkStation")
    private String WorkStation;
    /**
     *工位描述
     */
    @TableField("WorkStationName")
    private String WorkStationName;
    /**
     *工序状态
     * 110已下达 120已派工 121派工失败 125已接受 130进行中 135暂停
     * 140已完工 150已报工 155报工失败 160取消报工 170已删除
     */
    @TableField("ProgressStatus")
    private String ProgressStatus;
    /**
     *工序数量
     */
    @TableField("Quantity")
    private int Quantity;
    /**
     *下一工序顺序号
     */
    @TableField("NextSequenceNo")
    private String NextSequenceNo;
    /**
     *下一工序工序号
     */
    @TableField("NextOprSequenceNo")
    private String NextOprSequenceNo;
    /**
     *下一工序工序名称
     */
    @TableField("NextOprSequenceName")
    private String NextOprSequenceName;
    /**
     *下一工序工作中心
     */
    @TableField("NextWorkCenter")
    private String NextWorkCenter;
    /**
     *预留参数1
     */
    @TableField("paramRsrv1")
    private String paramRsrv1;
    /**
     *预留参数2
     */
    @TableField("paramRsrv2")
    private String paramRsrv2;

    @TableField(exist = false)
    private List<MomOrderPathMaterial> momOrderPathMaterials;

    @ApiModelProperty(value = "Mom下发订单原Json格式")
    @TableField("JsonOriginalData")
    private String JsonOriginalData;

}
