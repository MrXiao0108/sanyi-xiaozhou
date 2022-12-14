package com.dzics.data.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.business.service.EquipmentService;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.enums.UserIdentityEnum;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.model.constant.ProductDefault;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.model.dao.WorkNumberName;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.vo.PutIsShowVo;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.db.dao.DzEquipmentDowntimeRecordDao;
import com.dzics.data.pdm.db.dao.DzEquipmentProNumDao;
import com.dzics.data.pdm.db.dao.DzLineShiftDayDao;
import com.dzics.data.pdm.db.model.dao.EquipmentDataDo;
import com.dzics.data.pdm.db.model.dto.EquipmentDownExcelDo;
import com.dzics.data.pdm.db.model.dto.SelectEquipmentDataVo;
import com.dzics.data.pdm.model.dto.GetByEquipmentNoVo;
import com.dzics.data.pdm.model.entity.DzLineShiftDay;
import com.dzics.data.pdm.model.vo.GetByEquipmentNoDo;
import com.dzics.data.pdm.model.vo.GetEquipmentStateDo;
import com.dzics.data.pdm.model.vo.RobotDownExcelVo;
import com.dzics.data.pdm.service.DzLineShiftDayService;
import com.dzics.data.pms.db.dao.DzProductDao;
import com.dzics.data.pub.db.dao.DzEquipmentDao;
import com.dzics.data.pub.db.dao.DzProductionLineDao;
import com.dzics.data.pub.model.dto.PutEquipmentVo;
import com.dzics.data.pub.model.dto.SelectEquipmentVo;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.entity.DzOrder;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.vo.*;
import com.dzics.data.pub.service.DzOrderService;
import com.dzics.data.pub.service.SysCmdTcpService;
import com.dzics.data.pub.service.SysConfigService;
import com.dzics.data.redis.util.RedisUtil;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author xnb
 * @date 2022???02???09??? 18:08
 */
@Service
@Slf4j
public class EquipmentServiceImpl implements EquipmentService {
    @Autowired
    private DzEquipmentProNumDao dzEquipmentProNumMapper;
    @Autowired
    private DzicsUserService userService;
    @Autowired
    private SysConfigService dzDetectionTemplCache;
    @Autowired
    private DzEquipmentDao equipmentMapper;
    @Autowired
    private DzEquipmentDowntimeRecordDao dzEquipmentDowntimeRecordMapper;
    @Autowired
    private DzProductionLineDao dzProductionLineMapper;
    @Autowired
    private DzLineShiftDayDao dzLineShiftDayMapper;
    @Autowired
    private DzLineShiftDayService lineShiftDayService;
    @Autowired
    private DzOrderService dzOrderService;
    @Autowired
    private SysCmdTcpService cmdTcpService;
    @Autowired
    private DzEquipmentDao dzEquipmentMapper;
    @Autowired
    private DzProductDao productDao;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzLineShiftDayDao lineShiftDayDao;

    @Override
    public Result<List<EquipmentDataDo>> listEquipmentData(String sub, Integer robotEquipmentCode, SelectEquipmentDataVo vo) {
        vo.setEquipmentType(3);
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
    public Result<List<EquipmentDo>> list(String sub, Integer type, PageLimit pageLimit, SelectEquipmentVo data) {
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
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
        String orderNo = dzEquipmentMapper.selectById(noVo.getId()).getOrderNo();
        noVo.setOrderNo(orderNo);
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        List<GetByEquipmentNoDo> listX = dzEquipmentDowntimeRecordMapper.getByEquipmentNo(noVo);
        PageInfo<GetByEquipmentNoDo> info = new PageInfo(listX);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Override
    public Result<EquipmentDo> add(String sub, AddEquipmentVo addEquipmentVo) {
        DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(addEquipmentVo.getLineId());
        if (dzProductionLine == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_28);
        }
        DzOrder byId = dzOrderService.getById(dzProductionLine.getOrderId());
        if (byId == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_54);
        }
        //????????????
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper<>();
        wrapper.eq("equipment_no", addEquipmentVo.getEquipmentNo()).
                eq("line_id", addEquipmentVo.getLineId()).
                eq("equipment_type", addEquipmentVo.getEquipmentType());
        List<DzEquipment> equipmentNo = equipmentMapper.selectList(wrapper);
        log.warn("???????????????????????????????????????:" + equipmentNo.size());
        if (equipmentNo.size() > 0) {
            log.error("?????????????????????????????????:" + addEquipmentVo.getEquipmentNo());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_25);
        }
        //????????????
        List<DzEquipment> equipmentCode = equipmentMapper.selectList(new QueryWrapper<DzEquipment>().eq("equipment_code", addEquipmentVo.getEquipmentCode()));
        if (equipmentCode.size() > 0) {
            log.info("?????????????????????????????????:" + addEquipmentVo.getEquipmentNo());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_26);
        }
        SysUser sysUser = userService.getByUserName(sub);
        DzEquipment dzEquipment = new DzEquipment();
        BeanUtils.copyProperties(addEquipmentVo, dzEquipment);
//        dzEquipment.setOrgCode(dzProductionLine.getOrgCode());
        if (EquiTypeEnum.JCSB.getCode() == dzEquipment.getEquipmentType()) {
            //???????????????????????????????????? ????????????0???
            dzEquipment.setRunStatusValue(0);
        } else if (EquiTypeEnum.JC.getCode() == dzEquipment.getEquipmentType()) {
            //??????
            dzEquipment.setRunStatusValue(1);
        } else if (EquiTypeEnum.JQR.getCode() == dzEquipment.getEquipmentType()) {
            //?????????
            dzEquipment.setRunStatusValue(2);
        }
        dzEquipment.setDoorCode(addEquipmentVo.getDoorCode());
        dzEquipment.setIsShow(addEquipmentVo.getIsShow());
        dzEquipment.setOrgCode(dzProductionLine.getOrgCode());
        dzEquipment.setLineNo(dzProductionLine.getLineNo());
        dzEquipment.setCreateBy(sysUser.getRealname());
        dzEquipment.setOrderNo(byId.getOrderNo());
        dzEquipment.setNickName(addEquipmentVo.getNickName());
        equipmentMapper.insertClearCache(dzEquipment);
        lineShiftDayService.arrange();
        return new Result(CustomExceptionType.OK, dzEquipment);
    }

    @Override
    public Result put(String sub, PutEquipmentVo putEquipmentVo) {
        SysUser byUserName = userService.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        //?????????????????????
        DzEquipment dzEquipment = equipmentMapper.selectById(putEquipmentVo.getId());
        if (dzEquipment == null) {
            log.error("??????id?????????,id:{}", putEquipmentVo.getId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_6);
        }

        //??????????????????
        if (!dzEquipment.getLineId().equals(putEquipmentVo.getLineId())) {
            DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(putEquipmentVo.getLineId());
            if (dzProductionLine == null) {
                log.error("???????????????id?????????");
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_71);
            }
            //??????????????????????????? ???????????????????????????????????????
            QueryWrapper<DzEquipment> wrapper = new QueryWrapper<>();
            wrapper.eq("line_id", putEquipmentVo.getLineId());
            wrapper.eq("equipment_no", putEquipmentVo.getEquipmentNo());
            wrapper.eq("equipment_type", putEquipmentVo.getEquipmentType());
            List<DzEquipment> dzEquipments = equipmentMapper.selectList(wrapper);
            if (dzEquipments.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_72);
            }
            //??????????????????????????? ???????????????????????????
            QueryWrapper<DzEquipment> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("line_id", putEquipmentVo.getLineId());
            wrapper1.eq("equipment_code", putEquipmentVo.getEquipmentCode());
            List<DzEquipment> dzEquipmentList = equipmentMapper.selectList(wrapper1);
            if (dzEquipmentList.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_73);
            }
            //??????????????? ?????????????????????
            putEquipmentVo.setOrderNo(dzProductionLine.getOrderNo());
        } else {
            //??????????????? ??????????????????????????????
            putEquipmentVo.setOrderNo(dzEquipment.getOrderNo());
        }
        //????????????????????????
//        if (!putEquipmentVo.getEquipmentName().equals(dzEquipment.getEquipmentName())) {
//            List<DzEquipment> equipmentName = equipmentMapper.selectList(new QueryWrapper<DzEquipment>().eq("equipment_name", putEquipmentVo.getEquipmentName()));
//            if (equipmentName.size() > 0) {
//                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_27);
//            }
//        }
        //??????????????????????????????
        if (!putEquipmentVo.getEquipmentCode().equals(dzEquipment.getEquipmentCode())) {
            List<DzEquipment> equipmentCode = equipmentMapper.selectList(new QueryWrapper<DzEquipment>().eq("equipment_code", putEquipmentVo.getEquipmentCode()));
            if (equipmentCode.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_26);
            }
        }
        //????????????????????????
        if (!putEquipmentVo.getEquipmentNo().equals(dzEquipment.getEquipmentNo())) {
            QueryWrapper<DzEquipment> eq = new QueryWrapper<DzEquipment>()
                    .eq("order_no", putEquipmentVo.getOrderNo())
                    .eq("line_id", putEquipmentVo.getLineId())
                    .eq("equipment_type", putEquipmentVo.getEquipmentType())
                    .eq("equipment_no", putEquipmentVo.getEquipmentNo());
            List<DzEquipment> dzEquipments = equipmentMapper.selectList(eq);
            if (dzEquipments.size() > 0) {
                log.error("?????????????????????????????????????????????????????????");
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_63);
            }
        }
        //??????????????????
        if (putEquipmentVo.getEquipmentType().intValue() != dzEquipment.getEquipmentType().intValue()) {
            //????????????????????????
            QueryWrapper<DzEquipment> eq = new QueryWrapper<DzEquipment>()
                    .eq("order_no", putEquipmentVo.getOrderNo())
                    .eq("line_id", putEquipmentVo.getLineId())
                    .eq("equipment_type", putEquipmentVo.getEquipmentType())
                    .eq("equipment_no", putEquipmentVo.getEquipmentNo());
            List<DzEquipment> dzEquipments = equipmentMapper.selectList(eq);
            if (dzEquipments.size() > 0) {
                log.error("???????????????????????????????????????????????????????????????{}????????????????????????{}???????????????", putEquipmentVo.getOrderNo(), putEquipmentVo.getLineId(), putEquipmentVo.getEquipmentType(), putEquipmentVo.getEquipmentNo());
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_63);
            }
        }
        dzEquipment.setDoorCode(putEquipmentVo.getDoorCode());
        dzEquipment.setIsShow(putEquipmentVo.getIsShow());
        dzEquipment.setEquipmentName(putEquipmentVo.getEquipmentName());
        dzEquipment.setPostscript(putEquipmentVo.getPostscript());
        BeanUtils.copyProperties(putEquipmentVo, dzEquipment);
        equipmentMapper.updateByIdClearCache(dzEquipment);
        //???????????????????????????
        DzLineShiftDay dzLineShiftDay = new DzLineShiftDay();
        dzLineShiftDay.setEquipmentNo(putEquipmentVo.getEquipmentNo());
        dzLineShiftDay.setEquipmentType(putEquipmentVo.getEquipmentType());
        dzLineShiftDayMapper.update(dzLineShiftDay, new QueryWrapper<DzLineShiftDay>().eq("eq_id", putEquipmentVo.getId()));
        return new Result(CustomExceptionType.OK, Message.OK_3);
    }

    @Override
    public Result getById(String sub, String id) {
        SysUser byUserName = userService.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        EquipmentDetis dzEquipment = equipmentMapper.getById(byUserName.getUseOrgCode(), id);
        if (dzEquipment == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_6);
        }
        return new Result(CustomExceptionType.OK, dzEquipment);
    }

    @Override
    public Result<List<EquipmentListDo>> list(String sub, SelectEquipmentVo data) {
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
        data.setTableKey(tableKey);
        SysUser byUserName = userService.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        data.setEquipmentType(data.getEquipmentType());
        data.setUseOrgCode(byUserName.getUseOrgCode());
        if (data.getPage() != -1) {
            PageHelper.startPage(data.getPage(), data.getLimit());
        }
        List<EquipmentListDo> list = equipmentMapper.list(data);
        PageInfo<EquipmentListDo> info = new PageInfo<>(list);

        List<EquipmentListDo> dataList = info.getList();
        //????????????
        for (EquipmentListDo equipmentListDo : dataList) {
            String runState = equipmentListDo.getRunStatus();//????????????
            String connectState = equipmentListDo.getConnectState();//????????????
            String operatorMode = equipmentListDo.getOperatorMode();//????????????
            if (equipmentListDo.getEquipmentType().intValue() == 2) {//??????
                runState = cmdTcpService.convertTcp("B562", equipmentListDo.getB562());
                connectState = cmdTcpService.convertTcp("B561", equipmentListDo.getB561());
                operatorMode = cmdTcpService.convertTcp("B565", equipmentListDo.getB565());
            }
            if (equipmentListDo.getEquipmentType().intValue() == 3) {//?????????
                runState = cmdTcpService.convertTcp("A563", equipmentListDo.getA563());
                connectState = cmdTcpService.convertTcp("A561", equipmentListDo.getA561());
                operatorMode = cmdTcpService.convertTcp("A562", equipmentListDo.getA562());
            }
            equipmentListDo.setRunStatus(runState);
            equipmentListDo.setConnectState(connectState);
            equipmentListDo.setOperatorMode(operatorMode);
        }
        return new Result(CustomExceptionType.OK, dataList, info.getTotal());
    }

    @Transactional
    @Override
    public Result del(String sub, Long id) {
        SysUser byUserName = userService.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue()) {
            DzEquipment dzEquipment = equipmentMapper.selectById(id);
            if (dzEquipment == null) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_56);
            }
            //????????????????????????
            int delIndex = dzLineShiftDayMapper.delete(new QueryWrapper<DzLineShiftDay>().eq("eq_id", id));
            //????????????
            int i = equipmentMapper.deleteByIdClearCache(id);
            log.info("??????id:{},???????????????????????????{},????????????????????????:{}", id, i, delIndex);
            return new Result(CustomExceptionType.OK, Message.OK_2);
        } else {
            //???????????????????????????????????????
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_55);
        }
    }

    @Override
    public Result putIsShow(String sub, PutIsShowVo putIsShowVo) {
        DzEquipment dzEquipment = equipmentMapper.selectById(putIsShowVo.getId());
        dzEquipment.setIsShow(putIsShowVo.getIsShow());
        equipmentMapper.updateById(dzEquipment);
        return Result.ok(dzEquipment);
    }

    @Override
    public Result geEquipmentState(String lineId) {
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
        DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(lineId);
        if (dzProductionLine == null) {
            log.error("??????????????????????????????????????????,id:{}", lineId);
            return Result.error(CustomExceptionType.OK_NO_DATA);
        }
        DzEquipment dzEquipment = dzEquipmentMapper.selectById(dzProductionLine.getStatisticsEquimentId());
        if (dzEquipment == null) {
            log.error("??????????????????????????????????????????????????????,id:{}", dzProductionLine.getStatisticsEquimentId());
            return Result.error(CustomExceptionType.OK_NO_DATA);
        }
        String orderNo = dzProductionLine.getOrderNo();
        String lineNo = dzProductionLine.getLineNo();
        String modelNumber = (String) redisUtil.get(RedisKey.CHECK_PRODUCT + orderNo + lineNo);
        if (StringUtils.isEmpty(modelNumber)) {
            modelNumber = ProductDefault.modelNumber;
        }
        WorkNumberName productNo = productDao.getProductNo(modelNumber);
        GetEquipmentStateDo getEquipmentStateDo = new GetEquipmentStateDo();
        if (productNo != null) {
            getEquipmentStateDo.setProductName(productNo.getProductName());
            getEquipmentStateDo.setProductNo(productNo.getModelNumber());
        } else {
            getEquipmentStateDo.setProductName("????????????");
            getEquipmentStateDo.setProductNo("????????????");
        }
        getEquipmentStateDo.setLineName(dzProductionLine.getLineName());
        getEquipmentStateDo.setEquipmentName(dzEquipment.getEquipmentName());
        getEquipmentStateDo.setConnectState(dzEquipment.getConnectState());
        getEquipmentStateDo.setRunStatus(dzEquipment.getRunStatus());
        getEquipmentStateDo.setAlarmStatus(dzEquipment.getAlarmStatus());
        getEquipmentStateDo.setDownNum(dzEquipment.getDownSum());
        return Result.ok(getEquipmentStateDo);
    }

    @Override
    public Result getRobotDownExcel(String sub, EquipmentDownExcelDo selectEquipmentVo) {
        SysUser byUserName = userService.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        selectEquipmentVo.setOrgCode(byUserName.getOrgCode());
        if (selectEquipmentVo.getPage() != -1) {
            PageHelper.startPage(selectEquipmentVo.getPage(), selectEquipmentVo.getLimit());
        }
        List<RobotDownExcelVo> machineDownExcel = lineShiftDayDao.getRobotDownExcel(selectEquipmentVo.getDepartId(), selectEquipmentVo.getOrgCode(), selectEquipmentVo.getOrderNo(), selectEquipmentVo.getLineNo(), selectEquipmentVo.getEquipmentNo(), selectEquipmentVo.getField(), selectEquipmentVo.getType());
        PageInfo<RobotDownExcelVo> info = new PageInfo<>(machineDownExcel);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }
}
