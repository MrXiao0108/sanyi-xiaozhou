package com.dzics.data.common.base.model.vo;

import com.dzics.data.common.base.enums.PushEnumType;
import lombok.Data;

/**
 * 发送到看板数据定义
 *
 * @author ZhangChengJun
 * Date 2021/11/19.
 * @since
 */
@Data
public class PushKanbanBase<T> {
    /**
     * 类型
     */
    private PushEnumType type;

    /**
     * 数据
     */
    private T data;
}
