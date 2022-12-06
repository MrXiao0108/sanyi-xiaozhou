package com.dzics.data.appoint.changsha.mom.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * 切换程序请求DNC  下载程序参数
 * @author LiuDongFei
 * @date 2022年06月22日 11:21
 */
@Data
public class DownloadParameter {
    /**
     * 任务号或者事件号
     * "task_number": "T-0000527",
     */
    private String task_number;

    /**
     * 物料编码
     * "material_code": "实际的物料编码",
     */
    private String material_code;

    /**
     * 工艺路线号
     * "routing_code": "实际工艺路线号",
     */
    private String routing_code;

    /**
     * 顺序号
     *
     */
    private String sequenceNumber;

    /**
     * 工序号
     * "working_procedure": "10",
     */
    private String working_procedure;

    /**
     * 工作中心编码
     * "work_center": "1",
     */
    private String work_center;

    /**
     * 设备编码
     * "machine_code": "400700359",
     */
    private String machine_code;

    /**
     * DNC提供的key ,固定值，用来判定接口访问权限
     * "tokenstr": "12b9bb61-4703-48bb-879f-ff6c2a588c9e",
     */
    private String tokenstr;

    /**
     * 程序名称
     * "programname": "O0001.NC"
     */
    private String programname;

    public String getOrderId() {
        return null;
    }



    public Object getProductionTime() {
        return null;
    }

    public void setToken(Object token) {

    }

    public void setOrderId(String orderId) {

    }

    public void setProductionTime(Date productionTime) {

    }

    public void setRequestPath(Object requestPath) {

    }
}
