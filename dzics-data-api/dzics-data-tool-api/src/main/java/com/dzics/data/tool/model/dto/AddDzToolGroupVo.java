package com.dzics.data.tool.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AddDzToolGroupVo {
    @ApiModelProperty("刀具组编号")
    @NotNull(message = "刀具组编号不能为空")
    private Integer groupNo;
    @ApiModelProperty("刀具编号数组")
    @NotNull(message = "刀具编号数组不能为空")
    private List<Integer> toolNoList;
}
