package com.dzics.data.pub.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.db.dao.DzWorkingProcedureDao;
import com.dzics.data.pub.model.dto.WorkingProcedureAdd;
import com.dzics.data.pub.model.entity.DzWorkStationManagement;
import com.dzics.data.pub.model.entity.DzWorkingProcedure;
import com.dzics.data.pub.model.vo.WorkingProcedureRes;
import com.dzics.data.pub.service.DzWorkStationManagementService;
import com.dzics.data.pub.service.DzWorkingProcedureService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 工序表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
@Service
@Slf4j
public class DzWorkingProcedureServiceImpl extends ServiceImpl<DzWorkingProcedureDao, DzWorkingProcedure> implements DzWorkingProcedureService {

    @Autowired
    private DzWorkingProcedureDao dzWorkingProcedureMapper;
    @Autowired
    private DzWorkingProcedureService workingProcedureService;
    @Autowired
    DzWorkStationManagementService dzWorkStationManagementService;

    @Override
    public Result<List<WorkingProcedureRes>> selWorkingProcedure(PageLimit pageLimit, WorkingProcedureAdd procedureAdd, String useDepartId) {
        if (pageLimit.getPage() != -1 && pageLimit.getLimit() != -1) {
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        List<WorkingProcedureRes> procedureRes = dzWorkingProcedureMapper.selWorkingProcedure(pageLimit.getField(), pageLimit.getType(), procedureAdd.getOrderId(),
                procedureAdd.getLineId(), procedureAdd.getWorkCode(), procedureAdd.getWorkName(),useDepartId);
        PageInfo<WorkingProcedureRes> pageInfo = new PageInfo<>(procedureRes);
        return Result.ok(pageInfo.getList(), pageInfo.getTotal());
    }


    @Override
    public Result delWorkingProcedure(String id, String sub) {
        List<DzWorkStationManagement> working_procedure_id = dzWorkStationManagementService.list(new QueryWrapper<DzWorkStationManagement>().eq("working_procedure_id", id));
        if (working_procedure_id.size() > 0) {
            log.warn("删除工序接口，工序下绑定了工位，不允许删除，工序id:{}", id);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_119);
        }
        workingProcedureService.removeById(id);
        return Result.ok();
    }


}
