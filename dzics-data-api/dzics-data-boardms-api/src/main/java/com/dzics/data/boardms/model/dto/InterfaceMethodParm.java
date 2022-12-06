package com.dzics.data.boardms.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 调用方法类详情
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "SysInterface对象", description = "调用方法类详情")
public class InterfaceMethodParm implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "接口id", required = true)
    private String interfaceId;

    @ApiModelProperty(value = "介绍内容", required = true)
    @NotNull(message = "介绍内容必填")
    private String methodExplain;

    @ApiModelProperty(value = "简介", required = true)
    @NotNull(message = "简介必填")
    private String briefIntroduction;

    @ApiModelProperty(value = "方法名称", required = true)
    @NotNull(message = "方法名称必填")
    private String methodName;

    @ApiModelProperty(value = "容器中类名称")
    private String beanName;

    @ApiModelProperty(value = "缓存时长（单位 秒）")
    private Integer cacheDuration;

    @ApiModelProperty(value = "返回参数名称", required = true)
    @NotEmpty(message = "返回参数名称必填")
    private String responseName;

//    /**
//     * 是否标记
//     */
    @ApiModelProperty("是否选中")
    private Integer isShow = 1;

    @ApiModelProperty("父节点ID")
    private String parentId;



    @ApiModelProperty(value = "方法组名称",required = true)
    private String groupName;

    @ApiModelProperty(value = "组排序值")
    private BigDecimal sortCode;

}
