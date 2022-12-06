package com.dzics.data.tool.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.tool.model.entity.DzToolInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 刀具表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
public interface DzToolInfoService extends IService<DzToolInfo> {


    /**
     * 通过道具组主键 查询道具组映射
     *
     * @param ids：道具组主键集合
     * @return 查询结果
     */
    Map<String, List<DzToolInfo>> mapById(Collection<String> ids);
}
