package com.dzics.data.appoint.changsha.mom.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author xnb
 * @date 2022/10/25 0025 10:56
 */
@Data
public class MomPackingVo {

    @ApiModelProperty(value = "请求ID")
    private String reqId;

    @ApiModelProperty(value = "系统编码")
    private String reqSys;

    @ApiModelProperty(value = "工厂编号")
    private String Facility;

    @ApiModelProperty(value = "工位号")
    private String StationNo;

    @ApiModelProperty(value = "工装号")
    private String ContainerListNo;

    @ApiModelProperty(value = "实际开始时间")
    private String ActualStartDate;

    @ApiModelProperty(value = "实际结束时间")
    private String ActualCompleteDate;

    @ApiModelProperty(value = "设备编号")
    private String DeviceID;

    @ApiModelProperty(value = "工序报工类型")
    private String ProgressType;

    @ApiModelProperty(value = "合格数量")
    private String Quantity;

    @ApiModelProperty(value = "不合格数量")
    private String NGQuantity;

    @ApiModelProperty(value = "报工员工号")
    private String EmployeeNo;

    @ApiModelProperty(value = "预留参数1")
    private String paramRsrv1;

    @ApiModelProperty(value = "预留参数2")
    private String paramRsrv2;

    @ApiModelProperty(value = "预留参数3")
    private String paramRsrv3;

    @ApiModelProperty(value = "预留参数4")
    private String paramRsrv4;

    @ApiModelProperty(value = "预留参数5")
    private String paramRsrv5;

    @ApiModelProperty(value = "序列号信息")
    private List<MomQrCodeVo>snList;


}
