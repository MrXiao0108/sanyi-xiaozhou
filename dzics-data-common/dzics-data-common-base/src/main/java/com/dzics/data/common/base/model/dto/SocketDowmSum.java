package com.dzics.data.common.base.model.dto;

import com.dzics.data.common.base.model.vo.BaseSocketEqment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 机器人停机次数信息
 *
 * @author ZhangChengJun
 * Date 2021/2/26.
 * @since
 */
@Data
@ApiModel(value = "SocketDowmSum", description = "机器人停机次数信息")
public class SocketDowmSum extends BaseSocketEqment {
    @ApiModelProperty("停机次数")
    private Long downSum;
}
