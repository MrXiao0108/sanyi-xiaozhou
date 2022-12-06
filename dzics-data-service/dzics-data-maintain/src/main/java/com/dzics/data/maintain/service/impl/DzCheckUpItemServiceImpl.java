package com.dzics.data.maintain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.maintain.db.dao.DzCheckUpItemDao;
import com.dzics.data.maintain.model.entity.DzCheckUpItem;
import com.dzics.data.maintain.model.entity.DzCheckUpItemType;
import com.dzics.data.maintain.model.vo.DzCheckUpItemDo;
import com.dzics.data.maintain.service.DzCheckUpItemService;
import com.dzics.data.maintain.service.DzCheckUpItemTypeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 巡检项设置 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
@Service
public class DzCheckUpItemServiceImpl extends ServiceImpl<DzCheckUpItemDao, DzCheckUpItem> implements DzCheckUpItemService {
    @Autowired
    private DzCheckUpItemTypeService dzCheckUpItemTypeService;
    @Autowired
    DzCheckUpItemService dzCheckUpItemService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Result del(String checkItemId) {
        dzCheckUpItemTypeService.remove(new QueryWrapper<DzCheckUpItemType>().eq("check_item_id", checkItemId));
        dzCheckUpItemService.remove(new QueryWrapper<DzCheckUpItem>().eq("check_item_id", checkItemId));
        return Result.ok();
    }
}
