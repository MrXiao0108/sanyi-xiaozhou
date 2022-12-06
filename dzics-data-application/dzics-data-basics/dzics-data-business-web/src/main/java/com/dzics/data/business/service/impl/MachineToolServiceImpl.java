package com.dzics.data.business.service.impl;

import com.dzics.data.business.service.MachineToolService;
import com.dzics.data.common.base.enums.UserIdentityEnum;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.db.dao.DzEquipmentDowntimeRecordDao;
import com.dzics.data.pdm.db.dao.DzEquipmentProNumDao;
import com.dzics.data.pdm.db.dao.DzLineShiftDayDao;
import com.dzics.data.pdm.db.model.dao.EquipmentDataDo;
import com.dzics.data.pdm.db.model.dto.EquipmentDownExcelDo;
import com.dzics.data.pdm.db.model.dto.SelectEquipmentDataVo;
import com.dzics.data.pdm.model.dto.GetByEquipmentNoVo;
import com.dzics.data.pdm.model.vo.GetByEquipmentNoDo;
import com.dzics.data.pdm.model.vo.MachineDownExcelVo;
import com.dzics.data.pub.db.dao.DzEquipmentDao;
import com.dzics.data.pub.model.dto.SelectEquipmentVo;
import com.dzics.data.pub.model.vo.EquipmentDo;
import com.dzics.data.pub.model.vo.RunDataModel;
import com.dzics.data.pub.service.SysConfigService;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author xnb
 * @date 2022年02月10日 13:59
 */
@Service
@Slf4j
public class MachineToolServiceImpl implements MachineToolService {

    @Autowired
    private DzicsUserService userService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private DzEquipmentProNumDao dzEquipmentProNumMapper;
    @Autowired
    private DzEquipmentDao equipmentMapper;
    @Autowired
    private DzEquipmentDowntimeRecordDao dzEquipmentDowntimeRecordMapper;
    @Autowired
    private DzLineShiftDayDao lineShiftDayDao;

    @Override
    public Result<List<EquipmentDataDo>> listEquipmentData(String sub, Integer robotEquipmentCode, SelectEquipmentDataVo vo) {
        if (vo.getPage() != -1) {
            PageHelper.startPage(vo.getPage(), vo.getLimit());
        }
        String lineId = vo.getLineId();
        if (StringUtils.isEmpty(lineId)) {
            SysUser byUserName = userService.getByUserName(sub);
            vo.setOrgCode(byUserName.getUseOrgCode());
        }
        List<EquipmentDataDo> list = dzEquipmentProNumMapper.listEquipmentDataV2(vo);
        PageInfo<EquipmentDataDo> info = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Override
    public Result systemRunModel(String sub) {
        RunDataModel runDataModel = sysConfigService.systemRunModel();
        if (runDataModel == null) {
            runDataModel.setTableName("dz_equipment_pro_num");
            runDataModel.setPlanDay("dz_production_plan_day");
            runDataModel.setRunDataModel("数量累计模式");
        }
        return Result.OK(runDataModel);
    }

    @Override
    public Result<List<EquipmentDo>> list(String sub, Integer type, PageLimit pageLimit, SelectEquipmentVo data) {
        String tableKey = ((RunDataModel) systemRunModel(null).getData()).getTableName();
        data.setTableKey(tableKey);
        SysUser byUserName = userService.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        if (pageLimit.getPage() != -1) {
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        data.setEquipmentType(type);
        data.setUseOrgCode(byUserName.getUseOrgCode());
        List<EquipmentDo> list = equipmentMapper.equipmentList(data);
        PageInfo<EquipmentDo> info = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Override
    public Result getByEquipmentNo(String sub, GetByEquipmentNoVo noVo, PageLimit pageLimit) {
        String orderNo = equipmentMapper.selectById(noVo.getId()).getOrderNo();
        noVo.setOrderNo(orderNo);
        PageHelper.startPage(pageLimit.getPage(),pageLimit.getLimit());
        List<GetByEquipmentNoDo> list = dzEquipmentDowntimeRecordMapper.getByEquipmentNo(noVo);
        PageInfo<GetByEquipmentNoDo>info=new PageInfo(list);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Override
    public Result<List<MachineDownExcelVo>> getMachineDownExcel(String sub, EquipmentDownExcelDo selectEquipmentVo) {
        SysUser byUserName = userService.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        selectEquipmentVo.setOrgCode(byUserName.getOrgCode());
        if (selectEquipmentVo.getPage() != -1) {
            PageHelper.startPage(selectEquipmentVo.getPage(),selectEquipmentVo.getLimit());
        }
        List<MachineDownExcelVo> machineDownExcel = lineShiftDayDao.getMachineDownExcel(selectEquipmentVo.getDepartId(), selectEquipmentVo.getOrgCode(), selectEquipmentVo.getOrderNo(), selectEquipmentVo.getLineNo(), selectEquipmentVo.getEquipmentNo(), selectEquipmentVo.getField(), selectEquipmentVo.getType());
        PageInfo<MachineDownExcelVo>info=new PageInfo<>(machineDownExcel);
        return new Result(CustomExceptionType.OK,info.getList(), info.getTotal());
    }

}
