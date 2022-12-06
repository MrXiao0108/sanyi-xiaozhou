package com.dzics.data.ums.model.dto.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.data.common.base.model.dto.SearchTimeBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户列表传递参数
 *
 * @author ZhangChengJun
 * Date 2021/1/12.
 * @since
 */
@Data
public class SelUser extends SearchTimeBase {
    @ApiModelProperty(value = "登录账号")
    @TableField("username")
    private String username;
    @ApiModelProperty(value = "真实姓名")
    @TableField("realname")
    private String realname;
    @ApiModelProperty(value = "状态(1-正常,0-冻结)")
    @TableField("status")
    private Integer status;



}
