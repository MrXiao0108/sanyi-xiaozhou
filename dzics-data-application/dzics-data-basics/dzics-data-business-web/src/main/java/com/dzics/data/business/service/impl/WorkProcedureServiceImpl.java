package com.dzics.data.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.business.service.WorkProcedureService;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.WorkingProcedureAdd;
import com.dzics.data.pub.model.entity.DzWorkingProcedure;
import com.dzics.data.pub.service.DzWorkingProcedureService;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class WorkProcedureServiceImpl implements WorkProcedureService {
    @Autowired
    private DzWorkingProcedureService workingProcedureService;
    @Autowired
    private DzicsUserService sysUserServiceDao;

    @Override
    public Result addWorkingProcedure(WorkingProcedureAdd procedureAdd, String sub) {
        QueryWrapper<DzWorkingProcedure> wp = new QueryWrapper<>();
        wp.eq("work_code", procedureAdd.getWorkCode());
        wp.eq("order_id",procedureAdd.getOrderId());
        wp.eq("line_id",procedureAdd.getLineId());
        DzWorkingProcedure one = workingProcedureService.getOne(wp);
        if (one != null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR39);
        }
        wp.clear();
        wp.eq("order_id",procedureAdd.getOrderId());
        wp.eq("line_id",procedureAdd.getLineId());
        wp.eq("sort_code", procedureAdd.getSortCode());
        DzWorkingProcedure one1 = workingProcedureService.getOne(wp);
        if (one1 != null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR40);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzWorkingProcedure workingProcedure = new DzWorkingProcedure();
        workingProcedure.setWorkCode(procedureAdd.getWorkCode());
        workingProcedure.setWorkName(procedureAdd.getWorkName());
        workingProcedure.setDepartId(procedureAdd.getDepartId());
        workingProcedure.setLineId(procedureAdd.getLineId());
        workingProcedure.setOrderId(procedureAdd.getOrderId());
        workingProcedure.setSortCode(procedureAdd.getSortCode());
        workingProcedure.setDelFlag(false);
        workingProcedure.setCreateBy(byUserName.getUsername());
        workingProcedure.setOrgCode(byUserName.getUseOrgCode());
        workingProcedureService.save(workingProcedure);
        return Result.ok();
    }


    @Override
    public Result editWorkingProcedure(WorkingProcedureAdd procedureAdd, String sub) {

        if (StringUtils.isEmpty(procedureAdd.getWorkingProcedureId())) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR38);
        }
        QueryWrapper<DzWorkingProcedure> wp = new QueryWrapper<>();
        wp.eq("order_id",procedureAdd.getOrderId());
        wp.eq("line_id",procedureAdd.getLineId());
        wp.eq("work_code", procedureAdd.getWorkCode());
        DzWorkingProcedure one = workingProcedureService.getOne(wp);
        if (one != null) {
            if (!one.getWorkingProcedureId().equals(procedureAdd.getWorkingProcedureId())) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR39);
            }
        }

        QueryWrapper<DzWorkingProcedure> wpSort = new QueryWrapper<>();
        wpSort.eq("order_id",procedureAdd.getOrderId());
        wpSort.eq("line_id",procedureAdd.getLineId());
        wpSort.eq("sort_code", procedureAdd.getSortCode());
        DzWorkingProcedure wpSortPero = workingProcedureService.getOne(wpSort);
        if (wpSortPero != null) {
            if (!wpSortPero.getWorkingProcedureId().equals(procedureAdd.getWorkingProcedureId())) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR40);
            }
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzWorkingProcedure workingProcedure = new DzWorkingProcedure();
        workingProcedure.setWorkingProcedureId(procedureAdd.getWorkingProcedureId());
        workingProcedure.setWorkCode(procedureAdd.getWorkCode());
        workingProcedure.setSortCode(procedureAdd.getSortCode());
        workingProcedure.setWorkName(procedureAdd.getWorkName());
        workingProcedure.setDepartId(procedureAdd.getDepartId());
        workingProcedure.setLineId(procedureAdd.getLineId());
        workingProcedure.setOrderId(procedureAdd.getOrderId());
        workingProcedure.setUpdateBy(byUserName.getUsername());
        workingProcedure.setOrgCode(byUserName.getUseOrgCode());
        workingProcedureService.updateById(workingProcedure);
        return Result.ok();
    }

}
