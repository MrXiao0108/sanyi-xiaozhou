package com.dzics.data.appoint.changsha.mom.service;

import com.dzics.data.appoint.changsha.mom.model.dto.agv.AgvClickSignal;
import com.dzics.data.appoint.changsha.mom.model.vo.ResultDto;
import com.dzics.data.appoint.changsha.mom.util.AutomaticGuidedVehicle;
import com.dzics.data.common.base.vo.Result;

public interface AgvRobackService {
    ResultDto automaticGuidedVehicle(AutomaticGuidedVehicle automaticGuidedVehicle);
    void handelAgvMessage(String reqId);

    /**
     * 来料信号确认
     *
     * @param clickSignal
     * @return
     */
    Result chlickSignal(AgvClickSignal clickSignal);
}
