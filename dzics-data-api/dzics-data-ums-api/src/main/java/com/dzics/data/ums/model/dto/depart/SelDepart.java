package com.dzics.data.ums.model.dto.depart;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.data.common.base.annotation.QueryType;
import com.dzics.data.common.base.enums.QueryTypeEnu;
import com.dzics.data.common.base.model.dto.SearchTimeBase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户列表传递参数
 *
 * @author ZhangChengJun
 * Date 2021/1/12.
 * @since
 */
@Data
public class SelDepart extends SearchTimeBase {
    @ApiModelProperty(value = "站点公司名称")
    @QueryType(QueryTypeEnu.like)
    @TableField("depart_name")
    private String departName;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    @QueryType(QueryTypeEnu.like)
    private String orgCode;

    @ApiModelProperty(value = "状态(1-正常,0-冻结)")
    @TableField("status")
    private Integer status;


}
