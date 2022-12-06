package com.dzics.data.pub.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.pub.db.dao.DzRepairHistoryDao;
import com.dzics.data.pub.model.dto.FaultRecordParmsDateils;
import com.dzics.data.pub.model.dto.FaultRecordParmsReq;
import com.dzics.data.pub.model.entity.DzRepairHistory;
import com.dzics.data.pub.model.entity.DzRepairHistoryDetails;
import com.dzics.data.pub.model.vo.FaultRecord;
import com.dzics.data.pub.model.vo.FaultRecordDetails;
import com.dzics.data.pub.model.vo.FaultRecordDetailsInner;
import com.dzics.data.pub.service.DzRepairHistoryDetailsService;
import com.dzics.data.pub.service.DzRepairHistoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 设备故障维修单 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
@Service
public class DzRepairHistoryServiceImpl extends ServiceImpl<DzRepairHistoryDao, DzRepairHistory> implements DzRepairHistoryService {
    @Autowired
    private DzRepairHistoryService dzRepairHistoryService;
    @Autowired
    private DzRepairHistoryDetailsService historyDetailsService;

    @Override
    public Result<List<FaultRecord>> getFaultRecordList(String useOrgCode, PageLimitBase pageLimit, FaultRecordParmsReq parmsReq) {
        if (pageLimit.getPage()!=-1){
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        Long checkNumber = StringUtils.isEmpty(parmsReq.getCheckNumber()) ? null : Long.valueOf(parmsReq.getCheckNumber());
        String equipmentNo = parmsReq.getEquipmentNo();
        Integer faultType = StringUtils.isEmpty(parmsReq.getFaultType()) ? null : Integer.valueOf(parmsReq.getFaultType());
        Long lineId = StringUtils.isEmpty(parmsReq.getLineId()) ? null : Long.valueOf(parmsReq.getLineId());
        List<FaultRecord> faultRecords =   baseMapper.getFaultRecordList(useOrgCode,checkNumber, lineId, faultType, equipmentNo,  pageLimit.getField(), pageLimit.getType(),parmsReq.getStartTime(),parmsReq.getEndTime());
        PageInfo<FaultRecord> faultRecordPageInfo = new PageInfo<>(faultRecords);
        List<FaultRecord> list = faultRecordPageInfo.getList();
        long total = faultRecordPageInfo.getTotal();
        return new Result(CustomExceptionType.OK, list, total);
    }

    @Override
    public Result<FaultRecordDetails> getFaultRecordDetails(String sub, FaultRecordParmsDateils parmsReq) {
        DzRepairHistory byId = dzRepairHistoryService.getById(parmsReq.getRepairId());
        List<FaultRecordDetailsInner> detailsInners = dzRepairHistoryService.getFaultRecordDetails(parmsReq.getRepairId());
        FaultRecordDetails details = new FaultRecordDetails();
        details.setDetailsInner(detailsInners);
        details.setRemarks(byId.getRemarks());
        details.setStartHandleDate(DateUtil.dateFormatToStingYmdHms(byId.getStartHandleDate()));
        details.setCompleteHandleDate(DateUtil.dateFormatToStingYmdHms(byId.getCompleteHandleDate()));
        return Result.OK(details);
    }


    @Override
    public List<FaultRecordDetailsInner> getFaultRecordDetails(String repairId) {
        return baseMapper.getFaultRecordDetails(repairId);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result delFaultRecord(String sub, String repairId) {
        dzRepairHistoryService.removeById(repairId);
        QueryWrapper<DzRepairHistoryDetails> wp = new QueryWrapper<>();
        wp.eq("repair_id", repairId);
        historyDetailsService.remove(wp);
        return Result.ok();
    }
}
