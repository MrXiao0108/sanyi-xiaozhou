package com.dzics.data.appoint.changsha.mom.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Classname MaterialParmsDto
 * @Description 物料参数
 * @Date 2022/5/12 14:10
 * @Created by NeverEnd
 */
@Data
public class MaterialParmsDto {

    /**
     * 生产订单号
     */
    private String wipOrderNo;

    /**
     * 物料编码
     */
    private String materialNo;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 序列号
     */
    private String serialNo;

    /**
     * 顺序号
     */
    private String sourceSequenceNo;

    /**
     * 工序号
     */
    private String sourceOprSequenceNo;

    private String materialType;

    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
}
