package com.dzics.data.appoint.changsha.mom.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * MOM 下发的订单参数信息
 * @author NeverEnd
 */
@Data
public class IssueOrderInformation<T> implements Serializable {

    /**
     *接口类型
     */
    @NotBlank
    private String taskType;
    /**
     * 消息内容
     */
    @NotNull(message = "内容不能为空")
    private T task;
    /**
     * 协议版本
     */
    @NotNull
    private int version;
    /**
     * 消息ID
     */
    @NotBlank
    private String taskId;


}
