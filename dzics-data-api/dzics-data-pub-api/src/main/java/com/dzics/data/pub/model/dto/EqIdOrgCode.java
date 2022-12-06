package com.dzics.data.pub.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Classname EqIdOrgCode
 * @Description 描述
 * @Date 2022/3/11 15:16
 * @Created by NeverEnd
 */
@Data
public class EqIdOrgCode implements Serializable {

    private String orgCode;

    private String deviceId;
}
