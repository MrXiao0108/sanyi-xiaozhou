package com.dzics.data.appoint.changsha.mom.service;

/**
 * @author ZhangChengJun
 * Date 2022/1/13.
 * @since
 */
public interface HttpService<T> {
    /**
     * 发送请求
     *
     *
     * @param innerGroupId
     * @param orderCode
     * @param lineNo
     * @param groupId
     * @param url
     * @param reqJson
     * @param responseType
     * @return
     */
    T sendPost(String innerGroupId, String orderCode, String lineNo, String groupId, String url, String reqJson, Class<T> responseType);
}
