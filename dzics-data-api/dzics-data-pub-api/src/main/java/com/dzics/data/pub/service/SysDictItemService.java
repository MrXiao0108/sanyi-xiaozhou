package com.dzics.data.pub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.DictItemVo;
import com.dzics.data.pub.model.entity.SysDictItem;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * <p>
 * 系统字典详情 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
public interface SysDictItemService extends IService<SysDictItem> {
    @Cacheable(cacheNames = "cacheService.getIndexIsShowNg")
    String getIndexIsShowNg();

    @Cacheable(cacheNames = "cacheService.getSystemConfigDepart")
    String getSystemConfigDepart();

    String getDictTest(String datasource, String key);

    @CacheEvict(cacheNames = {"cacheService.getSystemConfigDepart","cacheService.getIndexIsShowNg"}, allEntries = true)
    Result addDictItem(String sub, DictItemVo dictItemVo);

    Result delDictItem(String sub, String id);
    @CacheEvict(cacheNames = {"cacheService.getSystemConfigDepart","cacheService.getIndexIsShowNg"}, allEntries = true)
    Result updateDictItem(String sub, DictItemVo dictItemVo);

    Result<SysDictItem> listDictItem(PageLimit pageLimit, String dictId);

    Result<SysDictItem> getDictItem(String dictCode);

    Result<SysDictItem> getItemListByCode(String dictCode);

    String getMomRunModel(String momRunModelKey, String orderCode);

    String updateAgvRunModel(String momRunModelKey, String orderCode, Integer logId);
}
