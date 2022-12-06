package com.dzics.data.zkjob.model.dto;

import com.dzics.data.common.base.model.page.PageLimitBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author xnb
 * @date 2021年11月22日 11:11
 */
@Data
public class GetWorkStatusVo extends PageLimitBase {
    @ApiModelProperty(value = "作业名称")
    private String jobName;

    @ApiModelProperty("任务执行状态,可选值为TASK_STAGING, TASK_RUNNING, TASK_FINISHED, TASK_KILLED, TASK_LOST, TASK_FAILED, TASK_ERROR")
    private String state;

    @ApiModelProperty("起始时间")
    @DateTimeFormat(pattern = "yyyyy-MM-dd HH:mm:ss")
    private String startTime;

    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyyy-MM-dd HH:mm:ss")
    private String endTime;


}
