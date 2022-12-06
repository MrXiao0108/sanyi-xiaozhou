package com.dzics.data.ums.model.dao;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 可切换站点
 *
 * @author ZhangChengJun
 * Date 2021/2/9.
 * @since
 */
@Data
public class SwitchSiteDo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "站点公司名称")
    private String departName;
}
