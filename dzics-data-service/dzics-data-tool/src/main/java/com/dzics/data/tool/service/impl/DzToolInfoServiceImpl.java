package com.dzics.data.tool.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.tool.db.dao.DzToolInfoDao;
import com.dzics.data.tool.model.entity.DzToolGroups;
import com.dzics.data.tool.model.entity.DzToolInfo;
import com.dzics.data.tool.service.DzToolInfoService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 刀具表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Service
public class DzToolInfoServiceImpl extends ServiceImpl<DzToolInfoDao, DzToolInfo> implements DzToolInfoService {

    @Override
    public Map<String, List<DzToolInfo>> mapById(Collection<String> ids) {
        List<DzToolInfo> list = this.list(Wrappers.<DzToolInfo>lambdaQuery().in(DzToolInfo::getToolGroupsId, ids));
        return list
                .stream()
                .collect(Collectors.groupingBy(DzToolInfo::getToolGroupsId));
    }
}
