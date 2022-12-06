package com.dzics.data.appoint.changsha.mom.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Classname MomHeader
 * @Description 描述
 * @Date 2022/4/22 13:30
 * @Created by NeverEnd
 */
@Data
public class MomHeader {
    /**
     *接口类型
     */
    @NotBlank
    private String taskType;
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
