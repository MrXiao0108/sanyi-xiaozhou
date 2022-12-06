package com.dzics.data.boardms.model.dto;

import com.dzics.data.boardms.model.entity.SysInterfaceGroup;
import com.dzics.data.boardms.model.entity.SysInterfaceMethod;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
@Data
public class ReqGroupConfig {
    @ApiModelProperty(value = "接口组")
    private SysInterfaceGroup interfaceGroup;

    @ApiModelProperty("所有接口")
    private List<SysInterfaceMethod> interfaceMethods;


}
