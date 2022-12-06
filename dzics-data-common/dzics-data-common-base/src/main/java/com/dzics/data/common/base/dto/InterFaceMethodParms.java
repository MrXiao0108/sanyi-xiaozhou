package com.dzics.data.common.base.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class InterFaceMethodParms {
    @ApiModelProperty(value = "缓存时长（单位 秒）")
    @TableField("cache_duration")
    private Integer cacheDuration;

    @ApiModelProperty(value = "方法名称", required = true)
    @TableField("method_name")
    @NotBlank(message = "方法名称必填")
    private String methodName;


    @ApiModelProperty(value = "返回参数名称", required = true)
    @TableField("response_name")
    @NotEmpty(message = "返回参数名称必填")
    private String responseName;

    @ApiModelProperty(value = "容器中类名称", required = true)
    private String beanName;
}
