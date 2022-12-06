package com.dzics.data.logms.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.enums.OperTypeStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * <p>
 * 操作日志
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_operation_logging")
@ApiModel(value = "SysOperationLogging对象", description = "操作日志")
public class SysOperationLogging implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日志主键")
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
//    @JsonIgnore
    private Long id;

    @ApiModelProperty(value = "模块标题")
    @TableField("title")
    @ExcelProperty("模块标题")
    private String title;

    @ApiModelProperty(value = "操作描述")
    @TableField("oper_desc")
    @ExcelProperty("操作内容")
    private String operDesc;

    @ApiModelProperty(value = "业务类型（0其它 1新增 2修改 3删除）")
    @TableField("business_type")
    @ExcelProperty("业务类型")
    private OperType businessType;

    @ApiModelProperty(value = "方法名称")
    @TableField("method")
    @ExcelIgnore
    private String method;

    @ApiModelProperty(value = "请求方式")
    @TableField("request_method")
    @ExcelProperty("请求方式")
    private String requestMethod;

    @ApiModelProperty(value = "接口类型")
    @TableField("operator_type")
    @ExcelIgnore
    private String operatorType;

    @ApiModelProperty(value = "操作人员")
    @TableField("oper_name")
    @ExcelProperty("操作人员")
    private String operName;

    @ApiModelProperty(value = "请求URL")
    @TableField("oper_url")
    @ExcelProperty("请求URL")
    private String operUrl;

    @ApiModelProperty(value = "主机地址")
    @TableField("oper_ip")
    @ExcelProperty("主机地址")
    private String operIp;

    @ApiModelProperty(value = "操作地点")
    @TableField("oper_location")
    @ExcelIgnore
    private String operLocation;

    @ApiModelProperty(value = "请求参数")
    @TableField("oper_param")
    @ExcelIgnore
    private String operParam;

    @ApiModelProperty("运行时间单位 ms 毫秒")
    @TableField("run_time")
    @ExcelProperty("耗时")
    private Long runTime;

    @ApiModelProperty(value = "返回参数")
    @TableField("json_result")
    @ExcelIgnore
    private String jsonResult;

    @ApiModelProperty(value = "操作状态（0正常 1异常）")
    @TableField("status")
    @ExcelProperty("操作状态")
    private OperTypeStatus status;

    @ApiModelProperty(value = " 异常信息")
    @TableField("exc_message")
    @ExcelIgnore
    private String excMessage;

    @ApiModelProperty(value = "异常名称")
    @TableField("error_msg")
    @ExcelIgnore
    private String errorMsg;

    @ApiModelProperty(value = "操作时间")
    @TableField("oper_time")
    @ExcelProperty("操作时间")
    private Date operTime;

    @ApiModelProperty(value = "访问日期")
    @TableField("oper_date")
    @ExcelIgnore
    private LocalDate operDate;

    @TableField("org_code")
    @ApiModelProperty("系统编码")
    @ExcelIgnore
    private String orgCode;

}
