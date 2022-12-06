package com.dzics.data.pub.service.kanban;

/**
 * @Classname AnrenToolCompService
 * @Description 查看刀具信息接口
 * @Date 2022/3/14 8:46
 * @Created by NeverEnd
 */
public interface ToolCompService<T> {
    /**
     * 刀具信息数据
     * @param orderNo
     * @param lineNo
     * @return
     */
    T getToolInfoData(String orderNo, String lineNo);




}
