package com.dzics.data.appoint.changsha.mom.service;

import com.dzics.data.appoint.changsha.mom.model.dto.dnc.DNCDto;
import com.dzics.data.appoint.changsha.mom.model.entity.DncFeedback;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * dnc 反馈信息 服务类
 * </p>
 *
 * @author van
 * @since 2022-06-28
 */
public interface DncFeedbackService extends IService<DncFeedback> {

    /**
     * DNC反馈结果处理
     *
     * @param feedback: DNC反馈实体
     * @author van
     * @date 2022/6/30
     */
    void feedbackHandel(DNCDto.Feedback feedback);
}
