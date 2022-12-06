package com.dzics.data.appoint.changsha.mom.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author LiuDongFei
 * @date 2022年09月13日 15:02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("manage_mode")
@ApiModel(value="ManageMode对象", description="")
public class ManageMode {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "编号")
    @TableField(value = "code")
    private String code;

    @ApiModelProperty(value = "通讯类型（1：手动，2：自动)")
    @TableField("type")
    private String type;

    @ApiModelProperty(value = "创建数据机构编码")
    @TableField("org_code")
    private String orgCode;

    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @TableField("del_flag")
    @JsonIgnore
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    @JsonIgnore
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonIgnore
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
