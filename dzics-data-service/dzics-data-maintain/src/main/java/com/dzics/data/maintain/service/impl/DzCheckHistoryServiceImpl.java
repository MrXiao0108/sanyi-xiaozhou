package com.dzics.data.maintain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.maintain.db.dao.DzCheckHistoryDao;
import com.dzics.data.maintain.model.dto.GetDeviceCheckVo;
import com.dzics.data.maintain.model.entity.DzCheckHistory;
import com.dzics.data.maintain.model.entity.DzCheckHistoryItem;
import com.dzics.data.maintain.model.vo.GetDeviceCheckDo;
import com.dzics.data.maintain.service.DzCheckHistoryItemService;
import com.dzics.data.maintain.service.DzCheckHistoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 设备巡检记录 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
@Service
public class DzCheckHistoryServiceImpl extends ServiceImpl<DzCheckHistoryDao,DzCheckHistory> implements DzCheckHistoryService {

    @Resource
    DzCheckHistoryDao dzCheckHistoryMapper;
    @Autowired
    DzCheckHistoryService dzCheckHistoryService;
    @Autowired
    DzCheckHistoryItemService dzCheckHistoryItemService;

    @Override
    public List<GetDeviceCheckDo> getList(GetDeviceCheckVo getDeviceCheckVo) {
        return dzCheckHistoryMapper.getList(getDeviceCheckVo);
    }

    @Override
    public Result<List<GetDeviceCheckDo>> list(GetDeviceCheckVo getDeviceCheckVo) {
        if (getDeviceCheckVo.getPage() != -1){
            PageHelper.startPage(getDeviceCheckVo.getPage(),getDeviceCheckVo.getLimit());
        }
        List<GetDeviceCheckDo> list = dzCheckHistoryService.getList(getDeviceCheckVo);
        PageInfo<GetDeviceCheckDo> info=new PageInfo(list);
        return Result.ok(info.getList(),info.getTotal());
    }

    @Override
    public Result put(String sub, List<DzCheckHistoryItem> list) {
        boolean b = dzCheckHistoryItemService.updateBatchById(list);
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Result del(String sub, String checkHistoryId) {
        boolean remove = dzCheckHistoryItemService.remove(new QueryWrapper<DzCheckHistoryItem>().eq("check_history_id", checkHistoryId));
        boolean b = dzCheckHistoryService.removeById(checkHistoryId);
        return Result.ok();
    }
}
