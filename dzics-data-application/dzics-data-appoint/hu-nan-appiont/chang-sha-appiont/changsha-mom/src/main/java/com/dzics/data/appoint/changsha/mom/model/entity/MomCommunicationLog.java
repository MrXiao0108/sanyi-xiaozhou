package com.dzics.data.appoint.changsha.mom.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * MOM通讯日志
 * </p>
 *
 * @author van
 * @since 2022-08-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_communication_log")
@ApiModel(value = "MomCommunicationLog对象", description = "MOM通讯日志")
public class MomCommunicationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "通讯类型（1：mom下发，2：请求mom)")
    @TableField("type")
    private String type;

    @ApiModelProperty(value = "业务类型（101：叫料，102：叫空框，103：满料拖出，104：空框拖出，105：报工，106：查询料框信息")
    @TableField("business_type")
    private String businessType;

    @ApiModelProperty(value = "处理结果（0：成功，1：失败）")
    @TableField("result_state")
    private String resultState;

    @ApiModelProperty(value = "请求内容")
    @TableField("request_content")
    private String requestContent;

    @ApiModelProperty(value = "响应内容")
    @TableField("response_content")
    private String responseContent;

    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @TableField("del_flag")
    private Boolean delFlag;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
