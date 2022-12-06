package com.dzics.data.business.service;

import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.AddFaultRecordParms;

public interface FaultService {
    Result addFaultRecord(String sub, AddFaultRecordParms parmsReq);

    Result updateFaultRecord(String sub, AddFaultRecordParms parmsReq);
}
