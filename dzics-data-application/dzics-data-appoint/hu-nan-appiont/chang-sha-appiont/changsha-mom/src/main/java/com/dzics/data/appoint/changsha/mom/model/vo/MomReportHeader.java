package com.dzics.data.appoint.changsha.mom.model.vo;

import lombok.Data;

/**
 * 请求MOM 通用 请求头参数
 *
 * @param <T>
 */
@Data
public class MomReportHeader<T> {
    private int version;
    private String taskId;
    private String taskType;
    private T reported;

}
