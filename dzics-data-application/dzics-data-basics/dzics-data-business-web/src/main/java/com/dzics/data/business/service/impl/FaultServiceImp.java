package com.dzics.data.business.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.data.business.service.FaultService;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.AddFaultRecordParms;
import com.dzics.data.pub.model.entity.DzRepairHistory;
import com.dzics.data.pub.model.entity.DzRepairHistoryDetails;
import com.dzics.data.pub.model.vo.AddFaultRecordParmsInner;
import com.dzics.data.pub.service.DzRepairHistoryDetailsService;
import com.dzics.data.pub.service.DzRepairHistoryService;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FaultServiceImp implements FaultService {
    @Autowired
    private DzicsUserService sysUserServiceDao;
    @Autowired
    private DzRepairHistoryService dzRepairHistoryService;
    @Autowired
    private DzRepairHistoryDetailsService historyDetailsService;


    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result addFaultRecord(String sub, AddFaultRecordParms parmsReq) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        String realname = byUserName.getRealname();
        String useOrgCode = byUserName.getUseOrgCode();
        DzRepairHistory dzRepairHistory = new DzRepairHistory();
        dzRepairHistory.setLineId(Long.valueOf(parmsReq.getLineId()));
        dzRepairHistory.setDieviceId(Long.valueOf(parmsReq.getDeviceId()));
        dzRepairHistory.setFaultType(Integer.valueOf(parmsReq.getFaultType()));
        dzRepairHistory.setStartHandleDate(parmsReq.getStartHandleDate());
        dzRepairHistory.setCompleteHandleDate(parmsReq.getCompleteHandleDate());
        dzRepairHistory.setRemarks(parmsReq.getRemarks());
        dzRepairHistory.setUsername(sub);
        dzRepairHistory.setOrgCode(useOrgCode);
        dzRepairHistory.setDelFlag(false);
        dzRepairHistory.setCreateBy(realname);
        dzRepairHistoryService.save(dzRepairHistory);
        String repairId = dzRepairHistory.getRepairId();
        List<AddFaultRecordParmsInner> parmsInners = parmsReq.getParmsInners();
        if (CollectionUtils.isNotEmpty(parmsInners)) {
            List<DzRepairHistoryDetails> historyDetails = new ArrayList<>();
            for (AddFaultRecordParmsInner parmsInner : parmsInners) {
                DzRepairHistoryDetails dzRepairHistoryDetails = new DzRepairHistoryDetails();
                dzRepairHistoryDetails.setRepairId(repairId);
                dzRepairHistoryDetails.setFaultLocation(parmsInner.getFaultLocation());
                dzRepairHistoryDetails.setFaultDescription(parmsInner.getFaultDescription());
                dzRepairHistoryDetails.setOrgCode(useOrgCode);
                dzRepairHistoryDetails.setDelFlag(false);
                dzRepairHistoryDetails.setCreateBy(realname);
                historyDetails.add(dzRepairHistoryDetails);
            }
            historyDetailsService.saveBatch(historyDetails);
        }
        return Result.ok();
    }

    @Override
    public Result updateFaultRecord(String sub, AddFaultRecordParms parmsReq) {
        String repairId = parmsReq.getRepairId();
        DzRepairHistory repairHistory = dzRepairHistoryService.getById(repairId);
        if (!repairHistory.getUsername().equals(sub)) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR57);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        String realname = byUserName.getRealname();
        DzRepairHistory dzRepairHistory = new DzRepairHistory();
        dzRepairHistory.setRepairId(repairId);
        dzRepairHistory.setLineId(Long.valueOf(parmsReq.getLineId()));
        dzRepairHistory.setDieviceId(Long.valueOf(parmsReq.getDeviceId()));
        dzRepairHistory.setFaultType(Integer.valueOf(parmsReq.getFaultType()));
        dzRepairHistory.setStartHandleDate(parmsReq.getStartHandleDate());
        dzRepairHistory.setCompleteHandleDate(parmsReq.getCompleteHandleDate());
        dzRepairHistory.setRemarks(parmsReq.getRemarks());
        dzRepairHistory.setUpdateBy(realname);
        dzRepairHistoryService.updateById(dzRepairHistory);
        List<AddFaultRecordParmsInner> parmsInners = parmsReq.getParmsInners();
        if (CollectionUtils.isNotEmpty(parmsInners)) {
            List<DzRepairHistoryDetails> historyDetails = new ArrayList<>();
            for (AddFaultRecordParmsInner parmsInner : parmsInners) {
                DzRepairHistoryDetails dzRepairHistoryDetails = new DzRepairHistoryDetails();
                dzRepairHistoryDetails.setRepairDetailsId(parmsInner.getRepairDetailsId());
                dzRepairHistoryDetails.setFaultLocation(parmsInner.getFaultLocation());
                dzRepairHistoryDetails.setFaultDescription(parmsInner.getFaultDescription());
                historyDetails.add(dzRepairHistoryDetails);
            }
            historyDetailsService.updateBatchById(historyDetails);
        }
        return Result.ok();
    }
}
