package com.dzics.data.kanban.changsha.xiaozhou.jingjia.model.vo;

import com.dzics.data.pms.model.dto.TableColumnDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 检测设置表格数据
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@Data
public class ProDetectionXiaoZhouJingJia<T> implements Serializable {
    @ApiModelProperty("表头")
    private List<TableColumnDto> tableColumn;
    @ApiModelProperty("行数据")
    private T tableData;
}
