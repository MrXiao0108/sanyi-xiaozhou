package com.dzics.data.appoint.changsha.mom.service;

import com.dzics.data.appoint.changsha.mom.model.vo.ActiveTipsVo;

import java.util.List;

/**
 * @author xnb
 * @date 2022/12/6 0006 14:42
 */
public interface ActiveTipsService {
    /**
     * 到期消息推送
     *
     * @return List<ActiveTipsVo>
     */
    List<ActiveTipsVo> getActiveTipsVo();
}
