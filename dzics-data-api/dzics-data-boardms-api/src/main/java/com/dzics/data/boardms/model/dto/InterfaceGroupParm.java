package com.dzics.data.boardms.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 接口组
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "SysInterfaceGroup对象", description = "接口组")
public class InterfaceGroupParm implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "组id")
    private String groupId;

    @ApiModelProperty(value = "接口组名称", required = true)
    @NotNull(message = "组名称必填")
    private String groupName;

    @ApiModelProperty(value = "接口组编码 唯一", required = true)
    @NotNull(message = "组编码必填")
    private String groupCode;


    /**
     * 排序值
     */
    @ApiModelProperty(value = "排序值", required = true)
    private BigDecimal sortCode;
}
