package com.dzics.data.appoint.changsha.mom.model.dao;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.dzics.data.common.base.model.write.UpWorkWrite;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class GetWorkingDetailsDo implements Serializable {
    @ExcelProperty("生产开始时间")
    @ApiModelProperty("生产开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date startTime;

    @ExcelProperty("生产完成时间")
    @ApiModelProperty("生产完成时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date completeTime;

    @ExcelIgnore
    @ApiModelProperty("生产开始上报时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date startRopertTime;

    @ExcelIgnore
    private String stationId;
    /**
     * 生产节拍
     */
    @ExcelProperty("节拍")
    @ApiModelProperty("节拍")
    private String taktTime;

    @ExcelIgnore
    @ApiModelProperty("生产完成上报时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date completeRopertTime;

    @ExcelIgnore
    @ExcelProperty(value = "开始上报状态",converter = UpWorkWrite.class)
    @ApiModelProperty("开始上报状态  0未上报 ,1 已上报 ,3上报异常")
    private Integer startReportingStatus;

    @ExcelIgnore
    @ExcelProperty(value = "完成上报状态",converter = UpWorkWrite.class)
    @ApiModelProperty("完成上报状态  0未上报 ,1 已上报 ,3上报异常")
    private Integer completeReportingStatus;

    @ExcelProperty("唯一订单号")
    @ApiModelProperty("唯一订单号")
    private String workpieceCode;

    /**
     * 工件二维码
     */
    @ExcelProperty("二维码")
    @ApiModelProperty("二维码")
    private String qrCode;

    /**
     * 生产任务订单ID
     */
    @ExcelIgnore
    private String proTaskId;

    /**
     * 日期
     */
    @ExcelProperty("日期")
    private String workDate;

    /**
     *
     */
    @ExcelProperty("工位名称")
    @ApiModelProperty("工位名称")
    private String stationName;

    /**
     *
     */
    @ExcelProperty("MOM订单号")
    @ApiModelProperty("MOM订单号")
    private String productAliasProductionLine;

    /**
     *
     */
    @ExcelProperty("简码")
    @ApiModelProperty("简码")
    private String productAlias;

    @ExcelIgnore
    private String orderId;
    @ExcelIgnore
    private String lineId;

    @ExcelProperty("产线名称")
    @ApiModelProperty("产线名称")
    private String lineName;


}
