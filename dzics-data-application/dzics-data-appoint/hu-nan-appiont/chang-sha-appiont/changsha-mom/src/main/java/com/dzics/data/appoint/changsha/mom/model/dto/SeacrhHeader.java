package com.dzics.data.appoint.changsha.mom.model.dto;

import lombok.Data;

@Data
public class SeacrhHeader {

    private String taskType;//接口类型
    private SearchNo reported;//消息内容
    private Integer version;//协议版本
    private String taskId;//消息ID
}
