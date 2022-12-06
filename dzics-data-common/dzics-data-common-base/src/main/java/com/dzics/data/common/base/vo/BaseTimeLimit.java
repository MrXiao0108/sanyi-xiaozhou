package com.dzics.data.common.base.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.data.common.base.annotation.QueryType;
import com.dzics.data.common.base.enums.QueryTypeEnu;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * @author ZhangChengJun
 * Date 2021/6/23.
 * @since
 */
@Data
public class BaseTimeLimit {
    @ApiModelProperty("当前页")
    private int page=1;
    @ApiModelProperty("每页查询条数")
    private int limit=10;

    @ApiModelProperty("开始时间")
    @TableField("create_time")
    @QueryType(QueryTypeEnu.ge)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    @ApiModelProperty("结束时间")
    @TableField("create_time")
    @QueryType(QueryTypeEnu.lt)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    @ApiModelProperty("排序字段")
    private String field;
    @ApiModelProperty("ASC OR DESC OR 空字符串")
    private String type;

}
