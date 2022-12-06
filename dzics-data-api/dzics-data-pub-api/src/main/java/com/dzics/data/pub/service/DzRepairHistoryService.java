package com.dzics.data.pub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.FaultRecordParmsDateils;
import com.dzics.data.pub.model.dto.FaultRecordParmsReq;
import com.dzics.data.pub.model.entity.DzRepairHistory;
import com.dzics.data.pub.model.vo.FaultRecord;
import com.dzics.data.pub.model.vo.FaultRecordDetails;
import com.dzics.data.pub.model.vo.FaultRecordDetailsInner;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 设备故障维修单 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
public interface DzRepairHistoryService extends IService<DzRepairHistory> {
    /**
     * 故障列表
     * @param sub
     * @param pageLimit
     * @param parmsReq
     * @return
     */
    Result<List<FaultRecord>> getFaultRecordList(String useOrgCode, PageLimitBase pageLimit, FaultRecordParmsReq parmsReq);

    Result<FaultRecordDetails> getFaultRecordDetails(String sub, FaultRecordParmsDateils parmsReq);


    List<FaultRecordDetailsInner> getFaultRecordDetails(String repairId);

    Result delFaultRecord(String sub, String repairId);
}
