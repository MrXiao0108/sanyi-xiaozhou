package com.dzics.data.appoint.changsha.mom.model.dto;

import lombok.Data;


/**
 * 生产叫料
 */
@Data
public class ProductionExpected<T> {
        private String taskType;//接口类型
        private T reported;//消息内容
        private Integer version;//协议版本
        private String taskId;//消息ID


}
