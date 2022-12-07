package com.dzics.data.business.service;

import com.dzics.data.business.model.vo.ActiveTip.ActiveTipsVo;

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
