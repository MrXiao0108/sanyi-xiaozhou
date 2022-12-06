package com.dzics.data.maintain.model.dao;

import lombok.Data;

/**
 * @Classname CheckItemType
 * @Description 描述
 * @Date 2022/3/11 9:33
 * @Created by NeverEnd
 */
@Data
public class CheckItemTypeDo {
    private String dictItemId;
    private String checkItemId;
    private String dictCode;
    private Boolean checked;
    private String checkTypeId;

    private Integer deviceType;
    private String checkName;
    private String createTime;
}
