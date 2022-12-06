package com.dzics.data.pub.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DictVo {
    @TableId(value = "id", type =IdType.ASSIGN_ID)
    @ApiModelProperty(value = "字典id(修改必填,添加不填)")
    private Long id;

    @ApiModelProperty(value = "字典名称")
    @NotEmpty(message = "字典名不能为空")
    private String dictName;


    @ApiModelProperty(value = "字典编码(修改不填,添加必填)")
    private String dictCode;

    @ApiModelProperty(value = "描述")
    private String description;

//    @ApiModelProperty(value = "字典类型0为string,1为number(修改不填,添加必填)")
//    @Min(0)
//    @Max(1)
//    private Integer type;
}
