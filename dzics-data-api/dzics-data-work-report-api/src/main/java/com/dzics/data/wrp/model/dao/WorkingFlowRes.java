package com.dzics.data.wrp.model.dao;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 最近检测工件
 *
 * @author ZhangChengJun
 * Date 2021/5/20.
 * @since
 */
@Data
public class WorkingFlowRes {
    private String stationId;

    private String workingProcedureId;

    private String qrCode;

    private String workpieceCode;

    private String startTime;

    private String completeTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "最早时间")
    private Date updateTimeAfter;

    @ApiModelProperty(value = "格式化后的时间")
    private String updateTimeUse;
}
