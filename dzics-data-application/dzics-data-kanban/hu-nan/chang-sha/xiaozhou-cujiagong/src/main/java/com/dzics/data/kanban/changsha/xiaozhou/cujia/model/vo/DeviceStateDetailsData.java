package com.dzics.data.kanban.changsha.xiaozhou.cujia.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/10/13.
 * @since
 */
@Data
public class DeviceStateDetailsData {
    private String name;
    private List<Object> value;
    private Long duration;
}
