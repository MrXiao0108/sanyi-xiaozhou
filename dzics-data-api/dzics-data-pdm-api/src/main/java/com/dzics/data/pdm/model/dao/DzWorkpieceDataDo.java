package com.dzics.data.pdm.model.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 设备检测数据V2新版记录
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Data
public class DzWorkpieceDataDo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private String onlyKey;


    /**
     * 设备类型
     */
    @TableField("equipment_type")
    private String equipmentType;

    /**
     *
     *设备序号
     */
    @TableField("equipment_no")
    private String equipmentNo;
    /**
     * 产品编号
     */
    @ApiModelProperty(value = "产品编号")
    @TableField("product_no")
    private String productNo;

    /**
     * 产品ID
     */
    @TableField(exist = false)
    private String productId;

    @ApiModelProperty(value = "条形码")
    @TableField("produc_barcode")
    private String producBarcode;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @TableField("name")
    private String name;

    /**
     * 工位编号
     */
    @ApiModelProperty(value = "工位编号")
    @TableField("work_number")
    private String workNumber;

    /**
     * 机床编号
     */
    @ApiModelProperty("机床编号")
    @TableField("machine_number")
    private String machineNumber;
    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    @TableField("order_no")
    private String orderNo;

    /**
     * 交换码(验证码)
     */
    @TableField("auth_code")
    private String authCode;
    /**
     * 二维码
     */
    @TableField("qr_code")
    private String qrCode;

    /**
     * 产线编号
     */
    @TableField("line_no")
    private String lineNo;


    @ApiModelProperty(value = "检测日期")
    @TableField("check_date")
    private String checkDate;

    @ApiModelProperty(value = "创建日期")
    @TableField("detector_time")
    private String detectorTime;

    @ApiModelProperty("创建日期")
    @TableField("create_time")
    private String createTime;

    @ApiModelProperty(value = "总输出")
    @TableField("out_ok")
    private String outOk;

    @ApiModelProperty(value = "检测")
    @TableField("detect01")
    private String detect01;

    @ApiModelProperty(value = "检测结果")
    @TableField("out_ok01")
    private String outOk01;

    @TableField("detect02")
    private String detect02;

    @TableField("out_ok02")
    private String outOk02;

    @TableField("detect03")
    private String detect03;

    @TableField("out_ok03")
    private String outOk03;

    @TableField("detect04")
    private String detect04;

    @TableField("out_ok04")
    private String outOk04;

    @TableField("detect05")
    private String detect05;

    @TableField("out_ok05")
    private String outOk05;

    @TableField("detect06")
    private String detect06;

    @TableField("out_ok06")
    private String outOk06;

    @TableField("detect07")
    private String detect07;

    @TableField("out_ok07")
    private String outOk07;

    @TableField("detect08")
    private String detect08;

    @TableField("out_ok08")
    private String outOk08;

    @TableField("detect09")
    private String detect09;

    @TableField("out_ok09")
    private String outOk09;

    @TableField("detect10")
    private String detect10;

    @TableField("out_ok10")
    private String outOk10;

    @TableField("detect11")
    private String detect11;

    @TableField("out_ok11")
    private String outOk11;

    @TableField("detect12")
    private String detect12;

    @TableField("out_ok12")
    private String outOk12;

    @TableField("detect13")
    private String detect13;

    @TableField("out_ok13")
    private String outOk13;

    @TableField("detect14")
    private String detect14;

    @TableField("out_ok14")
    private String outOk14;

    @TableField("detect15")
    private String detect15;

    @TableField("out_ok15")
    private String outOk15;

    @TableField("detect16")
    private String detect16;

    @TableField("out_ok16")
    private String outOk16;

    @TableField("detect17")
    private String detect17;

    @TableField("out_ok17")
    private String outOk17;

    @TableField("detect18")
    private String detect18;

    @TableField("out_ok18")
    private String outOk18;

    @TableField("detect19")
    private String detect19;

    @TableField("out_ok19")
    private String outOk19;

    @TableField("detect20")
    private String detect20;

    @TableField("out_ok20")
    private String outOk20;

    @TableField("detect21")
    private String detect21;

    @TableField("out_ok21")
    private String outOk21;

    @TableField("detect22")
    private String detect22;

    @TableField("out_ok22")
    private String outOk22;

    @TableField("detect23")
    private String detect23;

    @TableField("out_ok23")
    private String outOk23;

    @TableField("detect24")
    private String detect24;

    @TableField("out_ok24")
    private String outOk24;

    @TableField("detect25")
    private String detect25;

    @TableField("out_ok25")
    private String outOk25;

    @TableField("detect26")
    private String detect26;

    @TableField("out_ok26")
    private String outOk26;

    @TableField("detect27")
    private String detect27;

    @TableField("out_ok27")
    private String outOk27;

    @TableField("detect28")
    private String detect28;

    @TableField("out_ok28")
    private String outOk28;

    @TableField(exist = false)
    private String equipmentName;
}
