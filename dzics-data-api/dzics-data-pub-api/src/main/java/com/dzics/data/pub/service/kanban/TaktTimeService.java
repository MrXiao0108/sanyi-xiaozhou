package com.dzics.data.pub.service.kanban;

import com.dzics.data.common.base.dto.GetOrderNoLineNo;

/**
 * @Classname TaktTimeService
 * @Description 生产节拍接口定义
 * @Date 2022/4/19 10:38
 * @Created by NeverEnd
 */
public interface TaktTimeService<T> {
    /**
     * 获取生产节拍
     * @param orderNoLineNo
     * @return
     */
    T getTaktTime(GetOrderNoLineNo orderNoLineNo);
}
