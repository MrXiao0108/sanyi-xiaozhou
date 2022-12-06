package com.dzics.data.maintain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.maintain.model.entity.DzCheckUpItem;

/**
 * <p>
 * 巡检项设置 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
public interface DzCheckUpItemService extends IService<DzCheckUpItem> {
    Result del(String checkItemId);
}
