package com.dzics.data.appoint.changsha.mom.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.dzics.data.appoint.changsha.mom.model.enumeration.DncStateConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * dnc 换型信息
 * </p>
 *
 * @author van
 * @since 2022-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dnc_program")
@ApiModel(value = "DncProgram对象", description = "dnc 换型信息")
public class DncProgram implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelIgnore
    private String id;

    @ApiModelProperty(value = "产线id")
    @TableField("line_id")
    @ExcelIgnore
    private String lineId;

    @ApiModelProperty(value = "mom下发订单表 ID")
    @TableField("pro_task_order_id")
    @ExcelIgnore
    private String proTaskOrderId;

    @ApiModelProperty(value = "订单号")
    @TableField("WipOrderNo")
    @ExcelIgnore
    private String WipOrderNo;

    @ApiModelProperty(value = "任务号或者事件号")
    @TableField("task_number")
    @ExcelIgnore
    private String taskNumber;

    @ApiModelProperty(value = "物料编码")
    @TableField("material_code")
    @ExcelIgnore
    private String materialCode;

    @ApiModelProperty(value = "工艺路线号")
    @TableField("routing_code")
    @ExcelIgnore
    private String routingCode;

    @ApiModelProperty(value = "顺序号")
    @TableField("sequenceNumber")
    @ExcelIgnore
    private String sequencenumber;

    @ExcelIgnore
    @ApiModelProperty(value = "工序号")
    @TableField("working_procedure")
    private String workingProcedure;

    @ExcelIgnore
    @ApiModelProperty(value = "工作中心")
    @TableField("work_center")
    private String workCenter;

    @ExcelProperty("设备编号")
    @ApiModelProperty(value = "设备编码")
    @TableField("machine_code")
    private String machineCode;

    @ExcelProperty("主程序名")
    @ApiModelProperty(value = "主程序名")
    @TableField("programname")
    private String programname;

    @ExcelIgnore
    @ApiModelProperty(value = "运行主程序名")
    @TableField("run_programname")
    private String runProgramname;

    @ExcelIgnore
    @ApiModelProperty(value = "中控厂家提供的key ,固定值，用来判定接口访问权限")
    @TableField("tokenstr")
    private String tokenstr;

    @ExcelProperty(value = "处理结果",converter = DncStateConverter.class)
    @ApiModelProperty(value = "状态（1：请求失败，2：请求成功，未反馈，3：切换成功，4：切换失败，5：人工干预）")
    @TableField("state")
    private String state;

    @ExcelIgnore
    @ApiModelProperty(value = "DNC请求信息")
    @TableField("dnc_request")
    private String dncRequest;

    @ExcelProperty("DNC响应信息")
    @ApiModelProperty(value = "DNC响应信息")
    @TableField("dnc_response")
    private String dncResponse;

    @ExcelIgnore
    @ApiModelProperty(value = "DNC反馈详情")
    @TableField("feedback_detail")
    private String feedbackDetail;

    @ExcelIgnore
    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @TableField("del_flag")
    private Boolean delFlag;

    @ExcelIgnore
    @ApiModelProperty(value = "创建数据机构编码")
    @TableField("org_code")
    private String orgCode;

    @ExcelProperty("请求时间")
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ExcelIgnore
    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    @ExcelIgnore
    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;

    @ExcelIgnore
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
