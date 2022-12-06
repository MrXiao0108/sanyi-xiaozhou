package com.dzics.data.maintain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.maintain.model.dto.GetDeviceCheckVo;
import com.dzics.data.maintain.model.entity.DzCheckHistory;
import com.dzics.data.maintain.model.entity.DzCheckHistoryItem;
import com.dzics.data.maintain.model.vo.GetDeviceCheckDo;


import java.util.List;

/**
 * <p>
 * 设备巡检记录 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
public interface DzCheckHistoryService extends IService<DzCheckHistory> {

    List<GetDeviceCheckDo> getList(GetDeviceCheckVo getDeviceCheckVo);

    Result<List<GetDeviceCheckDo>> list(GetDeviceCheckVo getDeviceCheckVo);

    Result put(String sub, List<DzCheckHistoryItem> list);

    Result del(String sub, String checkHistoryId);
}
