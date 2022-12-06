package com.dzics.data.boardms.model.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户列表数据返回
 *
 * @author ZhangChengJun
 * Date 2021/1/12.
 * @since
 */
@Data
public class UserListRes {

    @ApiModelProperty(value = "主键id")
    private Long userId;

    @ApiModelProperty(value = "登录账号")
    private String username;

    @ApiModelProperty(value = "真实姓名")
    private String realname;


    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "状态(1-正常,0-冻结)")
    private Integer status;

    @ApiModelProperty(value = "当前使用站点编码")
    private String useOrgCode;

    @ApiModelProperty(value = "所属站点编码")
    private String orgCode;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    @ApiModelProperty("站点名称")
    private String departName;


}
