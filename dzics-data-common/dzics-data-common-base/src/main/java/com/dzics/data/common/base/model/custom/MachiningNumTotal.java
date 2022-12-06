package com.dzics.data.common.base.model.custom;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 机加线总产量数据信息
 *
 * @author ZhangChengJun
 * Date 2021/3/12.
 * @since
 */
@Data
public class MachiningNumTotal {
    /**
     * 设备id
     */
    @ApiModelProperty("设备id")
    private String equimentId;
    /**
     * 当前产量
     */
    @ApiModelProperty("当日数量")
    private Long dayNum = 0L;
    /**
     * 总数量
     */
    @ApiModelProperty("总数量")
    private Long totalNum = 0L;

    /**
     * 合格数量
     */
    @ApiModelProperty("合格数量")
    private Long qualifiedNum = 0L;

    /**
     * 不良品数量
     */
    @ApiModelProperty("不良品数量")
    private Long badnessNum = 0L;

    /**
     * 毛坯数量
     */
    @ApiModelProperty("毛坯数量")
    private Long roughNum;
}
