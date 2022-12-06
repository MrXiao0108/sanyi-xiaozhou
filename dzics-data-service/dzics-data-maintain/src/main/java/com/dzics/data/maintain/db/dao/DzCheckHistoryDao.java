package com.dzics.data.maintain.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.maintain.model.dto.GetDeviceCheckVo;
import com.dzics.data.maintain.model.entity.DzCheckHistory;
import com.dzics.data.maintain.model.vo.GetDeviceCheckDo;

import java.util.List;

/**
 * <p>
 * 设备巡检记录 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
public interface DzCheckHistoryDao extends BaseMapper<DzCheckHistory> {

    List<GetDeviceCheckDo> getList(GetDeviceCheckVo getDeviceCheckVo);
}
