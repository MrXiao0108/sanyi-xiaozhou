package com.dzics.data.business.service.impl;

import com.dzics.data.business.service.ProductionPlanService;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.enums.UserIdentityEnum;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.db.dao.DzEquipmentProNumDao;
import com.dzics.data.pdm.db.dao.DzProductionPlanDao;
import com.dzics.data.pdm.db.dao.DzProductionPlanDayDao;
import com.dzics.data.pdm.db.model.dao.PlanRecordDetailsListDo;
import com.dzics.data.pdm.db.model.dao.SelectEquipmentProductionDetailsDo;
import com.dzics.data.pdm.db.model.dao.SelectEquipmentProductionDo;
import com.dzics.data.pdm.db.model.dao.SelectProductionDetailsDo;
import com.dzics.data.pdm.db.model.dto.SelectEquipmentProductionVo;
import com.dzics.data.pdm.db.model.dto.SelectProductionDetailsVo;
import com.dzics.data.pdm.db.model.dto.SelectProductionPlanVo;
import com.dzics.data.pdm.db.model.vo.ProductionPlanDo;
import com.dzics.data.pdm.model.dao.SelectEquipmentProductionDetailsVo;
import com.dzics.data.pdm.model.dto.GetOneDayPlanDto;
import com.dzics.data.pdm.model.entity.DzProductionPlan;
import com.dzics.data.pms.service.DzDetectionTemplCache;
import com.dzics.data.pms.service.DzProductService;
import com.dzics.data.pub.db.dao.DzEquipmentDao;
import com.dzics.data.pub.db.dao.DzProductionLineDao;
import com.dzics.data.pub.db.dao.SysDictDao;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.vo.RunDataModel;
import com.dzics.data.pub.service.SysConfigService;
import com.dzics.data.pub.service.SysDictItemService;
import com.dzics.data.ums.db.dao.SysDepartDao;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProductionPlanServiceImpl implements ProductionPlanService {
    @Autowired
    private DzicsUserService sysUserServiceDao;
    @Autowired
    private DzEquipmentDao equipmentDao;
    @Autowired
    SysConfigService sysConfigService;
    @Autowired
    private DzProductionPlanDao dzProductionPlanMapper;
    @Autowired
    private SysDictItemService sysDictItemService;
    @Autowired
    private SysDictDao sysDictMapper;
    @Autowired
    DzDetectionTemplCache dzDetectionTemplCache;
    @Autowired
    DzProductionPlanDayDao dzProductionPlanDayMapper;
    @Autowired
    SysDepartDao sysDepartMapper;
    @Autowired
    DzProductionLineDao dzProductionLineMapper;
    @Autowired
    DzEquipmentProNumDao dzEquipmentProNumMapper;
    @Autowired
    DzProductService dzProductService;


    @Override
    public Result<ProductionPlanDo> list(String sub, PageLimit pageLimit, SelectProductionPlanVo selectProductionPlanVo) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        selectProductionPlanVo.setOrgCode(byUserName.getUseOrgCode());
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        List<ProductionPlanDo> list = dzProductionPlanMapper.list(selectProductionPlanVo);
        PageInfo<ProductionPlanDo> info = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Transactional
    @Override
    public Result<ProductionPlanDo> put(String sub, ProductionPlanDo productionPlanDo) {

        DzProductionPlan dzProductionPlan = dzProductionPlanMapper.selectById(productionPlanDo.getId());
        if (dzProductionPlan == null) {
            log.error("日生产计划id不存在:{}", productionPlanDo.getId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_65);
        }
        dzProductionPlan.setPlannedQuantity(productionPlanDo.getPlannedQuantity());
        dzProductionPlanMapper.updateById(dzProductionPlan);
//        SysDictItem one = sysDictItemService.getOne(new QueryWrapper<SysDictItem>().eq("dict_code", FinalCode.ClASSES_CODE).eq("item_text", dzProductionPlan.getLineId()));
//        if (one != null) {
//            one.setItemValue(String.valueOf(productionPlanDo.getDayClasses()));
//            sysDictItemService.updateById(one);
//        } else {
//            SysDict sysDict = sysDictMapper.selectOne(new QueryWrapper<SysDict>().eq("dict_code", FinalCode.ClASSES_CODE));
//            one = new SysDictItem();
//            one.setDictId(sysDict.getId());
//            one.setDictCode(sysDict.getDictCode());
//            one.setItemText(String.valueOf(dzProductionPlan.getLineId()));
//            one.setItemValue(String.valueOf(productionPlanDo.getDayClasses()));
//            sysDictItemService.save(one);
//        }
        //修改生产记录表
        return new Result(CustomExceptionType.OK, dzProductionPlan);
    }

    @Override
    public Result list(String sub,PageLimit pageLimit) {
        String tableKey = ((RunDataModel) sysConfigService.systemRunModel(null).getData()).getPlanDay();
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        List<GetOneDayPlanDto> plan = dzProductionPlanMapper.getOneDayPlan();
        PageInfo<GetOneDayPlanDto> info = new PageInfo<>(plan);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Override
    public Result detailsList(Long planId, String detectorTime) {
        String tableKey = ((RunDataModel) sysConfigService.systemRunModel(null).getData()).getTableName();
        DzProductionPlan dzProductionPlan = dzProductionPlanMapper.selectById(planId);
        if (dzProductionPlan == null) {
            log.error("生产计划id不存在:{}", planId);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_65);
        }
        String lineId = dzProductionPlan.getLineId();
        DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(lineId);
        if (dzProductionLine == null || dzProductionLine.getStatisticsEquimentId() == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR35);
        }
        List<PlanRecordDetailsListDo> list = dzEquipmentProNumMapper.detailsList(detectorTime, tableKey, dzProductionLine.getStatisticsEquimentId(), dzProductionLine.getOrderNo());
        return new Result(CustomExceptionType.OK, list, list.size());
    }

    @Override
    public Result<List<SelectProductionDetailsDo>> list(String sub, PageLimit pageLimit, SelectProductionDetailsVo detailsVo) {
        String tableKey = ((RunDataModel) sysConfigService.systemRunModel(null).getData()).getTableName();
        detailsVo.setTableKey(tableKey);//动态填充需要查询的table
        if (StringUtil.isEmpty(detailsVo.getLineId())) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR591);
        }
        DzProductionLine line = dzProductionLineMapper.selectById(detailsVo.getLineId());
        if (line != null) {
            String id = line.getStatisticsEquimentId();
            if (StringUtils.isEmpty(id)) {
                return new Result(CustomExceptionType.OK, new ArrayList<>(), 0);
            }
            detailsVo.setOrderNo(line.getOrderNo());
            detailsVo.setEqid(id);
        } else {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR592);
        }
        LocalDate startTime = detailsVo.getStartTime();
        if (startTime != null) {
            detailsVo.setStartTime(startTime.plusDays(-1));
        }
        LocalDate endTime = detailsVo.getEndTime();
        if (endTime != null) {
            detailsVo.setEndTime(endTime.plusDays(1));
        }
        if(pageLimit.getPage() != -1){
            PageHelper.startPage(0, pageLimit.getLimit());
        }
        List<SelectProductionDetailsDo> detailsDos = dzEquipmentProNumMapper.getlistPro(detailsVo);
        PageInfo<SelectProductionDetailsDo> info = new PageInfo<>(detailsDos);
        List<SelectProductionDetailsDo> list = info.getList();
        list.stream().forEach(s -> {
            s.setLineName(line.getLineName());
        });
        return new Result(CustomExceptionType.OK, list, info.getTotal());
    }

    @Override
    public Result<List<SelectEquipmentProductionDo>> listProductionEquipment(String sub, PageLimitBase pageLimit, SelectEquipmentProductionVo dto) {
        String tableKey = ((RunDataModel) sysConfigService.systemRunModel(null).getData()).getTableName();
        dto.setTableKey(tableKey);//动态填充需要查询的table
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        dto.setOrgCode(byUserName.getUseOrgCode());

        LocalDate startTime = dto.getStartTime();
        if (startTime != null) {
            dto.setStartTime(startTime.plusDays(-1));
        }
        LocalDate endTime = dto.getEndTime();
        if (endTime != null) {
            dto.setEndTime(endTime.plusDays(1));
        }
        if(pageLimit.getPage() != -1){
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        List<SelectEquipmentProductionDo> list = dzEquipmentProNumMapper.getDeviceProdoctSum(dto);
        PageInfo<SelectEquipmentProductionDo> info = new PageInfo<>(list);
        if(CollectionUtils.isEmpty(list)){
            return new Result(CustomExceptionType.OK_NO_DATA,info.getList(), info.getTotal());
        }
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Override
    public Result listProductionEquipmentDetails(String sub, SelectEquipmentProductionDetailsVo obj) {
        String tableKey = ((RunDataModel) sysConfigService.systemRunModel(null).getData()).getTableName();
        DzEquipment dzEquipment = equipmentDao.selectById(obj.getEquimentId());
        if (dzEquipment == null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR351);
        }
        List<SelectEquipmentProductionDetailsDo> list = dzEquipmentProNumMapper.listProductionEquipmentDetails(obj.getEquimentId(), obj.getWorkDate(), tableKey, dzEquipment.getOrderNo());
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getProductName() == null || "".equals(list.get(i).getProductName())) {
                list.get(i).setProductName(FinalCode.DZ_PRODUCT_NAME);
            }
        }
        return new Result(CustomExceptionType.OK, list);
    }
}
