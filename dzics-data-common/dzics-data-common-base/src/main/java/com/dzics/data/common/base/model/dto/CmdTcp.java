package com.dzics.data.common.base.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CmdTcp implements Serializable {

    private static final long serialVersionUID = 1L;
    //指令
    private String tcpValue;
    //指令值
    private String deviceItemValue;
    //指令描述
    private String tcpDescription;
}
