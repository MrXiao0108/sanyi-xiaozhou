package com.dzics.data.pub.service.kanban;

/**
 * @Classname CurrentDateService
 * @Description 获取当前日期信息
 * @Date 2022/3/17 15:23
 * @Created by NeverEnd
 */
public interface CurrentDateService<T,M> {
    /**
     * 获取当前日期数据
     *
     * @param t
     * @return
     */
    T getCurrentDate(M t);
}
