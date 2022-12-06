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

/**
 * @author LiuDongFei
 * @date 2022年06月24日 10:32
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dnc_report")
@ApiModel(value = "DncReport中控集成DNC", description = "请求DNC下载程序参数")
public class DncReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "dnc_id",type = IdType.ASSIGN_ID)
    private String dncId;


    @ApiModelProperty(value = "任务号/事件号")
    @TableField("task_number")
    private String taskNumber;

    @ApiModelProperty(value = "实际的物料编码")
    @TableField("material_code")
    private String materialCode;

    @ApiModelProperty(value = "实际工艺路线号")
    @TableField("routing_code")
    private String routingCode;

    @ApiModelProperty(value = "顺序号")
    @TableField("sequence_number")
    private String sequenceNumber;

    @ApiModelProperty(value = "工序号")
    @TableField("material_procedure")
    private String workingProcedure;

    @ApiModelProperty(value = "工作中心编码")
    @TableField("work_center")
    private String workCenter;

    @ApiModelProperty(value = "设备编码")
    @TableField("machine_code")
    private String machineCode;

    @ApiModelProperty(value = "tokenstr值")
    @TableField("tokenstr")
    private String tokenstr;

    @ApiModelProperty(value = "程序名称")
    @TableField("programname")
    private String programname;
}
