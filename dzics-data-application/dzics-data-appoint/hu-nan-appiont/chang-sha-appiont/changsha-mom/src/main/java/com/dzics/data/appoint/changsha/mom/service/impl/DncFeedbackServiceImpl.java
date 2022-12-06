package com.dzics.data.appoint.changsha.mom.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dzics.data.appoint.changsha.mom.enums.DNCProgramEnum;
import com.dzics.data.appoint.changsha.mom.model.dto.dnc.DNCDto;
import com.dzics.data.appoint.changsha.mom.model.entity.DncFeedback;
import com.dzics.data.appoint.changsha.mom.db.dao.DncFeedbackDao;
import com.dzics.data.appoint.changsha.mom.model.entity.DncProgram;
import com.dzics.data.appoint.changsha.mom.service.DncFeedbackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.service.DncProgramService;
import com.dzics.data.appoint.changsha.mom.service.MomOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * <p>
 * dnc 反馈信息 服务实现类
 * </p>
 *
 * @author van
 * @since 2022-06-28
 */
@Slf4j
@Service
public class DncFeedbackServiceImpl extends ServiceImpl<DncFeedbackDao, DncFeedback> implements DncFeedbackService {

    @Autowired
    private DncProgramService dncProgramService;
    @Autowired
    private MomOrderService momOrderService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void feedbackHandel(DNCDto.Feedback feedback) {
        DncProgram dncProgram = dncProgramService.getOne(Wrappers.<DncProgram>lambdaQuery()
                .eq(DncProgram::getTaskNumber, feedback.getTask_number())
                .eq(DncProgram::getMachineCode, feedback.getMachine_code()));
        if (ObjectUtils.isEmpty(dncProgram)) {
            log.info("DncFeedbackServiceImpl [feedbackHandel] dncProgram is null feedback{}", feedback);
            return;
        }
        if (DNCDto.FeedbackEnum.SUCCESS.val().equals(feedback.getDresult())) {
            dncProgram.setState(DNCProgramEnum.CHANGE_SUCCESS.val());
        }
        if (DNCDto.FeedbackEnum.FAIL.val().equals(feedback.getDresult())) {
            dncProgram.setState(DNCProgramEnum.CHANGE_FAIL.val());
        }
        dncProgram.setProgramname(feedback.getProgramname());
        dncProgram.setFeedbackDetail(feedback.getDetail());
        if (!StringUtils.isEmpty(dncProgram.getProgramname())) {
            dncProgram.setRunProgramname(dncProgram.getProgramname().substring(1, 5));
        }
        dncProgramService.updateById(dncProgram);

        DncFeedback dncFeedback = this.wrapper(feedback, dncProgram.getId());
        this.save(dncFeedback);

        /*if (DNCDto.FeedbackEnum.SUCCESS.val().equals(feedback.getDresult())) {
            momOrderService.orderStart(dncProgram.getProTaskOrderId());
        }*/
    }

    private DncFeedback wrapper(DNCDto.Feedback feedback, String dncProgramId) {
        DncFeedback dncFeedback = new DncFeedback();
        dncFeedback.setDncProgramId(dncProgramId);
        dncFeedback.setDresult(feedback.getDresult());
        dncFeedback.setDetail(feedback.getDetail());
        dncFeedback.setDncRequest(JSON.toJSONString(feedback));
        return dncFeedback;
    }
}
