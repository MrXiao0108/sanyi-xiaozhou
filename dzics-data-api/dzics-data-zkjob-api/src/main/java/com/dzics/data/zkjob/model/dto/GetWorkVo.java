package com.dzics.data.zkjob.model.dto;

import com.dzics.data.common.base.model.page.PageLimitBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author xnb
 * @date 2021年11月22日 8:41
 */
@Data
public class GetWorkVo extends PageLimitBase {

    @ApiModelProperty(value = "作业名称")
    private String jobName;

    @ApiModelProperty(value = "服务器IP")
    private String ip;

    @ApiModelProperty(value = "执行结果")
    private Integer isSuccess;

    @ApiModelProperty("起始时间")
    @DateTimeFormat(pattern = "yyyyy-MM-dd HH:mm:ss")
    private String startTime;

    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyyy-MM-dd HH:mm:ss")
    private String endTime;

}
