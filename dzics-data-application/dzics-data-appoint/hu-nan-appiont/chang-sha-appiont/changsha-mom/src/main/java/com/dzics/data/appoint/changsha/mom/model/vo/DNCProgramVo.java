package com.dzics.data.appoint.changsha.mom.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.data.common.base.model.page.PageLimit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: van
 * @since: 2022-06-29
 */
@Data
public class DNCProgramVo extends PageLimit {

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "产线id")
    private String lineId;

    @ApiModelProperty(value = "产线名称")
    private String lineName;

    @ApiModelProperty(value = "mom下发订单表 ID")
    private String proTaskOrderId;

    @ApiModelProperty(value = "订单号")
    private String WipOrderNo;

    @ApiModelProperty(value = "任务号或者事件号")
    private String taskNumber;

    @ApiModelProperty(value = "物料编码")
    @TableField("material_code")
    private String materialCode;

    @ApiModelProperty(value = "工艺路线号")
    private String routingCode;

    @ApiModelProperty(value = "顺序号")
    private String sequencenumber;

    @ApiModelProperty(value = "工序号")
    private String workingProcedure;

    @ApiModelProperty(value = "工作中心")
    private String workCenter;

    @ApiModelProperty(value = "设备编码")
    private String machineCode;

    @ApiModelProperty(value = "主程序名")
    private String programname;

    @ApiModelProperty(value = "运行主程序名")
    private String runProgramname;

    @ApiModelProperty(value = "状态（1：请求失败，2：请求成功，未反馈，3：切换成功，4：切换失败，5：人工干预）")
    private String state;

    @ApiModelProperty(value = "DNC响应信息")
    private String dncResponse;

    @ApiModelProperty(value = "DNC反馈详情")
    private String feedbackDetail;
}
