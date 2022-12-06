package com.dzics.data.pub.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LockScreenPassWord {
    /**
     * 锁屏密码
     */
    @ApiModelProperty(value = "锁屏密码", required = true)
    @NotEmpty(message = "密码必填")
    private String lockPassword;
}
