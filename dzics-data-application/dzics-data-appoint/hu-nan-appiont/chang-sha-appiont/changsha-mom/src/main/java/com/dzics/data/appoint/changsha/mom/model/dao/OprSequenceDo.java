package com.dzics.data.appoint.changsha.mom.model.dao;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname OprSequenceDo
 * @Description 工序组
 * @Date 2022/4/26 16:08
 * @Created by NeverEnd
 */
@Data
public class OprSequenceDo implements Serializable {
    /**
     *产品物料号
     */
    @ApiModelProperty("产品物料号")
    private String pathProductNo;
    /**
     *工序号
     */
    @ApiModelProperty("工序号")
    private String OprSequenceNo;

    /**
     *工序名称
     */
    @ApiModelProperty("工序名称")
    private String OprSequenceName;


    /**
     *顺序号
     */
    @ApiModelProperty("顺序号")
    private String SequenceNo;
}
