package com.dzics.data.ums.model.entity;

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
 * @author NeverEnd
 * @since 2021-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user_role")
@ApiModel(value="SysUserRole对象", description="")
public class SysUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type =IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty(value = "角色id")
    @TableField("role_id")
    private String roleId;

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

    public SysUserRole() {
    }

    public SysUserRole(String userId, String roleId, String orgCode, Boolean delFlag, String createBy) {
        this.userId = userId;
        this.roleId = roleId;
        this.orgCode = orgCode;
        this.delFlag = delFlag;
        this.createBy = createBy;
    }

    public SysUserRole(String userId, String roleId, String orgCode, Boolean delFlag, String createBy, String updateBy, Date updateTime) {
        this.userId = userId;
        this.roleId = roleId;
        this.orgCode = orgCode;
        this.delFlag = delFlag;
        this.createBy = createBy;
        this.updateBy =updateBy;
        this.updateTime = updateTime;
    }
}
