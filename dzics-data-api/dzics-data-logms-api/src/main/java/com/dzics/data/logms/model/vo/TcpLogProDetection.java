package com.dzics.data.logms.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 检测设置表格数据
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@Data
public class TcpLogProDetection implements Serializable {
    @ApiModelProperty("表头")
    private List<HeaderClom> tableColumn;
    @ApiModelProperty("行数据")
    private List<Map<String, Object>> tableData;


    public TcpLogProDetection() {
        tableColumn = new ArrayList<>();
        HeaderClom headerClom1 = new HeaderClom();
        headerClom1.setColData("message_id");
        headerClom1.setColName("消息id");
        tableColumn.add(headerClom1);
        HeaderClom headerClom2 = new HeaderClom();
        headerClom2.setColData("queue_name");
        headerClom2.setColName("队列名称");
        tableColumn.add(headerClom2);
        HeaderClom headerClom3 = new HeaderClom();
        headerClom3.setColData("client_id");
        headerClom3.setColName("客户端Id");
        tableColumn.add(headerClom3);
        HeaderClom headerClom4 = new HeaderClom();
        headerClom4.setColData("order_code");
        headerClom4.setColName("订单编码");
        tableColumn.add(headerClom4);
        HeaderClom headerClom5 = new HeaderClom();
        headerClom5.setColData("line_no");
        headerClom5.setColName("产线序号");
        tableColumn.add(headerClom5);
        HeaderClom headerClom6 = new HeaderClom();
        headerClom6.setColData("device_type");
        headerClom6.setColName("设备类型");
        tableColumn.add(headerClom6);
        HeaderClom headerClom7 = new HeaderClom();
        headerClom7.setColData("device_code");
        headerClom7.setColName("设备编码");
        tableColumn.add(headerClom7);
        HeaderClom headerClom8 = new HeaderClom();
        headerClom8.setColName("传输时间");
        headerClom8.setColData("send_timestamp");
        tableColumn.add(headerClom8);
    }
}
