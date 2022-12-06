package com.dzics.data.business.service;

import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.maintain.model.dto.*;

public interface MainTainService {
    Result addCheck(String sub, CheckUpVo checkUpVo);

    Result listCheck(PageLimit pageLimit, Integer deviceType, String checkName, String useOrgCode);

    Result putCheck(String sub, CheckUpVo checkUpVo);

    Result addDevice(String sub, DeviceCheckVo deviceCheckVo);

    Result getById(String checkHistoryId);

    Result addMaintainDevice(String sub, AddMaintainDevice parmsReq);

    Result updateMaintainDevice(String sub, AddMaintainDevice parmsReq);

    Result addMaintainRecord(String sub, AddMaintainRecord parmsReq);

    Result list(PageLimit pageLimit, Integer deviceType, String checkName);
}
