package com.dzics.data.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.business.service.MainTainService;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.maintain.model.dto.*;
import com.dzics.data.maintain.model.entity.*;
import com.dzics.data.maintain.model.vo.CheckTypeDo;
import com.dzics.data.maintain.model.vo.DzCheckUpItemDo;
import com.dzics.data.maintain.model.vo.MaintainRecordDetails;
import com.dzics.data.maintain.service.*;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.entity.SysDictItem;
import com.dzics.data.pub.service.DzEquipmentService;
import com.dzics.data.pub.service.SysDictItemService;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MainTainServiceImpl implements MainTainService {
    @Autowired
    private SysDictItemService sysDictItemService;
    @Autowired
    private DzicsUserService sysUserServiceDao;
    @Autowired
    private DzCheckUpItemService dzCheckUpItemService;
    @Autowired
    private DzCheckUpItemTypeService dzCheckUpItemTypeService;
    @Autowired
    private DzCheckHistoryService dzCheckHistoryService;
    @Autowired
    private DzCheckHistoryItemService dzCheckHistoryItemService;
    @Autowired
    private DzEquipmentService dzEquipmentService;
    @Autowired
    private DzMaintainDeviceService dzMaintainDevice;
    @Autowired
    private DzMaintainDeviceHistoryService deviceHistoryService;
    @Autowired
    private DzMaintainDeviceHistoryDetailsService deviceHistoryDetailsService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Result addCheck(String sub, CheckUpVo checkUpVo) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzCheckUpItem dzCheckUpItem = new DzCheckUpItem();
        dzCheckUpItem.setDeviceType(checkUpVo.getDeviceType());
        dzCheckUpItem.setCheckName(checkUpVo.getCheckName());
        dzCheckUpItem.setOrgCode(byUserName.getUseOrgCode());
        dzCheckUpItem.setCreateBy(byUserName.getUsername());
        boolean save = dzCheckUpItemService.save(dzCheckUpItem);
        if (save) {
            List<DzCheckUpItemType> list = new ArrayList<>();
            for (CheckTypeVo checkTypeVo : checkUpVo.getCheckTypeList()) {
                DzCheckUpItemType dzCheckUpItemType = new DzCheckUpItemType();
                dzCheckUpItemType.setDeviceType(checkUpVo.getDeviceType());
                dzCheckUpItemType.setCheckItemId(dzCheckUpItem.getCheckItemId());
                dzCheckUpItemType.setDictItemId(checkTypeVo.getDictItemId());
                dzCheckUpItemType.setDictCode(checkTypeVo.getDictCode());
                dzCheckUpItemType.setChecked(checkTypeVo.getChecked());
                dzCheckUpItemType.setOrgCode(byUserName.getUseOrgCode());
                dzCheckUpItemType.setCreateBy(byUserName.getUsername());
                list.add(dzCheckUpItemType);
            }
            if (list.size() > 0) {
                dzCheckUpItemTypeService.saveBatch(list);
                return Result.ok();
            }
        }
        return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
    }

    @Override
    public Result listCheck(PageLimit pageLimit, Integer deviceType, String checkName, String useOrgCode) {
        if (pageLimit.getPage() != -1) {
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        QueryWrapper<DzCheckUpItem> wrapper = new QueryWrapper();
        wrapper.eq("org_code",useOrgCode);
        if (deviceType != null) {
            wrapper.eq("device_type", deviceType);
        }
        if (!StringUtils.isEmpty(checkName)) {
            wrapper.likeRight("check_name", checkName);
        }
        List<DzCheckUpItem> data = dzCheckUpItemService.list(wrapper);
        PageInfo<DzCheckUpItem> info = new PageInfo<>(data);
        List<DzCheckUpItem> list = info.getList();
        List<DzCheckUpItemDo> dzCheckUpItemDos = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return Result.ok(new ArrayList<>(), info.getTotal());
        }

        //查询检测项类型
        List<String> collect = list.stream().map(p -> p.getCheckItemId()).collect(Collectors.toList());
        List<DzCheckUpItemType> check_item_id = dzCheckUpItemTypeService.list(new QueryWrapper<DzCheckUpItemType>().in("check_item_id", collect));
        //字典表翻译检测项类型
        List<String> collect1 = check_item_id.stream().map(p -> p.getDictItemId()).collect(Collectors.toList());
        List<SysDictItem> dictItems = sysDictItemService.list(new QueryWrapper<SysDictItem>().in("id", collect1));

        List<CheckTypeDo> checkTypeDos = new ArrayList<>();
        for (DzCheckUpItemType dzCheckUpItemType : check_item_id) {
            CheckTypeDo checkTypeDo = new CheckTypeDo();
            checkTypeDo.setCheckItemId(dzCheckUpItemType.getCheckItemId());
            checkTypeDo.setDictCode(dzCheckUpItemType.getDictCode());
            checkTypeDo.setChecked(dzCheckUpItemType.getChecked());
            checkTypeDo.setCheckTypeId(dzCheckUpItemType.getCheckTypeId());
            for (SysDictItem sysDictItem : dictItems) {
                if (sysDictItem.getId().equals(dzCheckUpItemType.getDictItemId())) {
                    checkTypeDo.setItemText(sysDictItem.getItemText());
                    break;
                }
            }
            checkTypeDos.add(checkTypeDo);
        }
        for (DzCheckUpItem dzCheckUpItem : list) {
            DzCheckUpItemDo dzCheckUpItemDo = new DzCheckUpItemDo();
            BeanUtils.copyProperties(dzCheckUpItem, dzCheckUpItemDo);
            List<CheckTypeDo> checkTypeList = new ArrayList<>();
            for (CheckTypeDo checkTypeDo : checkTypeDos) {
                if (checkTypeDo.getCheckItemId().equals(dzCheckUpItem.getCheckItemId())) {
                    checkTypeList.add(checkTypeDo);
                }
            }
            dzCheckUpItemDo.setCheckTypeList(checkTypeList);
            dzCheckUpItemDos.add(dzCheckUpItemDo);
        }
        return Result.ok(dzCheckUpItemDos, info.getTotal());
    }

    @Override
    public Result putCheck(String sub, CheckUpVo checkUpVo) {
        //判断同类型下巡检名称是否存在

        QueryWrapper<DzCheckUpItem> wrapper = new QueryWrapper<DzCheckUpItem>()
                .eq("check_name", checkUpVo.getCheckName())
                .eq("device_type", checkUpVo.getDeviceType())
                .ne("check_item_id", checkUpVo.getCheckItemId());
        DzCheckUpItem one = dzCheckUpItemService.getOne(wrapper);
        if (one != null) {
            log.error("巡检想名称重复");
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_200);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);

        one = new DzCheckUpItem();
        one.setCheckItemId(checkUpVo.getCheckItemId());
        one.setDeviceType(checkUpVo.getDeviceType());
        one.setCheckName(checkUpVo.getCheckName());
        one.setUpdateBy(byUserName.getUsername());
        boolean b = dzCheckUpItemService.updateById(one);
        if (b) {
            List<DzCheckUpItemType> list = new ArrayList<>();
            for (CheckTypeVo checkTypeVo : checkUpVo.getCheckTypeList()) {
                DzCheckUpItemType dzCheckUpItemType = new DzCheckUpItemType();
                BeanUtils.copyProperties(checkTypeVo, dzCheckUpItemType);
                dzCheckUpItemType.setCreateBy("111");
                list.add(dzCheckUpItemType);
            }
            dzCheckUpItemTypeService.updateBatchById(list);
        }
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Result addDevice(String sub, DeviceCheckVo deviceCheckVo) {
        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(deviceCheckVo.getHistoryItemList())) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzCheckHistory dzCheckHistory = new DzCheckHistory();
        dzCheckHistory.setLineId(deviceCheckVo.getLineId());
        dzCheckHistory.setDeviceId(deviceCheckVo.getDeviceId());
        dzCheckHistory.setCheckType(deviceCheckVo.getCheckType());
        dzCheckHistory.setUsername(byUserName.getUsername());
        dzCheckHistory.setOrgCode(byUserName.getUseOrgCode());
        dzCheckHistory.setUpdateBy(byUserName.getUsername());
        dzCheckHistory.setCreateBy(byUserName.getUsername());
        boolean save = dzCheckHistoryService.save(dzCheckHistory);
        if (save) {
            List<DzCheckHistoryItem> list = new ArrayList<>();
            for (DeviceCheckItemVo deviceCheckItemVo : deviceCheckVo.getHistoryItemList()) {
                DzCheckHistoryItem data = new DzCheckHistoryItem();
                data.setCheckHistoryId(dzCheckHistory.getCheckHistoryId());
                data.setCheckName(deviceCheckItemVo.getCheckName());
                data.setChecked(deviceCheckItemVo.getChecked());
                data.setContentText(deviceCheckItemVo.getContentText());
                data.setOrgCode(dzCheckHistory.getOrgCode());
                list.add(data);
            }
            if (list.size() > 0) {
                dzCheckHistoryItemService.saveBatch(list);
                return Result.ok();
            }
        }
        return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
    }

    @Override
    public Result getById(String checkHistoryId) {
        List<DzCheckHistoryItem> check_history_item_id = dzCheckHistoryItemService.list(new QueryWrapper<DzCheckHistoryItem>().eq("check_history_id", checkHistoryId));
        DzCheckHistory byId = dzCheckHistoryService.getById(checkHistoryId);
        DzEquipment equipment = dzEquipmentService.getById(byId.getDeviceId());
        Map<String, Object> map = new HashMap<>();
        map.put("historyItemList", check_history_item_id);
        map.put("checkType", byId.getCheckType());
        map.put("deviceId", equipment.getId());
        map.put("lineId", equipment.getLineId());
        map.put("equipmentType", equipment.getEquipmentType());

        return Result.ok(map);
    }

    @Override
    public Result addMaintainDevice(String sub, AddMaintainDevice parmsReq) {
        DzMaintainDevice dzM = dzMaintainDevice.getByDeviceId(parmsReq.getDeviceId());
        if (dzM != null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR352);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzMaintainDevice maintainDevice = new DzMaintainDevice();
        maintainDevice.setLineId(parmsReq.getLineId());
        maintainDevice.setDeviceId(parmsReq.getDeviceId());
        maintainDevice.setDateOfProduction(parmsReq.getDateOfProduction());
        maintainDevice.setMaintainDateBefore(parmsReq.getMaintainDateBefore());
        maintainDevice.setMaintainDateAfter(parmsReq.getMaintainDateAfter());
        maintainDevice.setFrequency(parmsReq.getFrequency());
        maintainDevice.setMultiple(parmsReq.getMultiple());
        maintainDevice.setUnit(parmsReq.getUnit());
        maintainDevice.setOrgCode(byUserName.getUseOrgCode());
        maintainDevice.setDelFlag(false);
        maintainDevice.setCreateBy(byUserName.getRealname());
        dzMaintainDevice.save(maintainDevice);
        return Result.ok();
    }

    @Override
    public Result updateMaintainDevice(String sub, AddMaintainDevice parmsReq) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzMaintainDevice maintainDevice = new DzMaintainDevice();
        maintainDevice.setMaintainId(parmsReq.getMaintainId());
        maintainDevice.setDeviceId(parmsReq.getDeviceId());
        maintainDevice.setDateOfProduction(parmsReq.getDateOfProduction());
        maintainDevice.setMaintainDateBefore(parmsReq.getMaintainDateBefore());
        maintainDevice.setMaintainDateAfter(parmsReq.getMaintainDateAfter());
        maintainDevice.setMultiple(parmsReq.getMultiple());
        maintainDevice.setFrequency(parmsReq.getFrequency());
        maintainDevice.setUnit(parmsReq.getUnit());
        maintainDevice.setUpdateBy(byUserName.getRealname());
        dzMaintainDevice.updateById(maintainDevice);
        return Result.ok();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result addMaintainRecord(String sub, AddMaintainRecord parmsReq) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        Date date = new Date();
        LocalDate now = LocalDate.now();
        String username = byUserName.getUsername();
        String realname = byUserName.getRealname();
        String useOrgCode = byUserName.getUseOrgCode();
        String maintainId = parmsReq.getMaintainId();
        DzMaintainDevice byId = dzMaintainDevice.getById(maintainId);
        String unit = byId.getUnit();//单位 年 月 日
        Integer frequency = byId.getFrequency();// 单位内执行次数
        Integer multiple = byId.getMultiple();
        Integer dayNum = 0;
        if ("年".equals(unit)) {
            dayNum = 365 * multiple / frequency;
        }
        if ("月".equals(unit)) {
            dayNum = 30 * multiple / frequency;
        }
        if ("周".equals(unit)) {
            dayNum = 7 * multiple / frequency;
        }
        DzMaintainDevice maintainDevice = new DzMaintainDevice();
        maintainDevice.setMaintainId(maintainId);
        maintainDevice.setMaintainDateBefore(now);
        maintainDevice.setMaintainDateAfter(now.plusDays(Long.valueOf(dayNum)));
        dzMaintainDevice.updateById(maintainDevice);
        DzMaintainDeviceHistory deviceHistory = new DzMaintainDeviceHistory();
        deviceHistory.setMaintainId(maintainId);
        deviceHistory.setMaintainDate(date);
        deviceHistory.setUsername(username);
        deviceHistory.setOrgCode(useOrgCode);
        deviceHistory.setDelFlag(false);
        deviceHistory.setCreateBy(realname);
        deviceHistoryService.save(deviceHistory);
        String maintainHistoryId = deviceHistory.getMaintainHistoryId();
        List<MaintainRecordDetails> recordDetails = parmsReq.getRecordDetails();
        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(recordDetails)) {
            List<DzMaintainDeviceHistoryDetails> historyDetails = new ArrayList<>();
            for (MaintainRecordDetails recordDetail : recordDetails) {
                DzMaintainDeviceHistoryDetails deviceHistoryDetails = new DzMaintainDeviceHistoryDetails();
                deviceHistoryDetails.setMaintainHistoryId(maintainHistoryId);
                deviceHistoryDetails.setMaintainItem(recordDetail.getMaintainItem());
                deviceHistoryDetails.setMaintainContent(recordDetail.getMaintainContent());
                deviceHistoryDetails.setOrgCode(useOrgCode);
                deviceHistoryDetails.setDelFlag(false);
                deviceHistoryDetails.setCreateBy(realname);
                historyDetails.add(deviceHistoryDetails);
            }
            deviceHistoryDetailsService.saveBatch(historyDetails);
        }
        return Result.ok();
    }

    @Override
    public Result list(PageLimit pageLimit, Integer deviceType, String checkName) {
        if (pageLimit.getPage() != -1) {
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        QueryWrapper<DzCheckUpItem> wrapper = new QueryWrapper();
        if (deviceType != null) {
            wrapper.eq("device_type", deviceType);
        }
        if (!StringUtils.isEmpty(checkName)) {
            wrapper.likeRight("check_name", checkName);
        }
        List<DzCheckUpItem> data = dzCheckUpItemService.list(wrapper);
        PageInfo<DzCheckUpItem> info = new PageInfo<>(data);
        List<DzCheckUpItem> list = info.getList();
        List<DzCheckUpItemDo> dzCheckUpItemDos = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return Result.ok(new ArrayList<>(), info.getTotal());
        }

        //查询检测项类型
        List<String> collect = list.stream().map(p -> p.getCheckItemId()).collect(Collectors.toList());
        List<DzCheckUpItemType> check_item_id = dzCheckUpItemTypeService.list(new QueryWrapper<DzCheckUpItemType>().in("check_item_id", collect));
        //字典表翻译检测项类型
        List<String> collect1 = check_item_id.stream().map(p -> p.getDictItemId()).collect(Collectors.toList());
        List<SysDictItem> dictItems = sysDictItemService.list(new QueryWrapper<SysDictItem>().in("id", collect1));

        List<CheckTypeDo> checkTypeDos = new ArrayList<>();
        for (DzCheckUpItemType dzCheckUpItemType : check_item_id) {
            CheckTypeDo checkTypeDo = new CheckTypeDo();
            checkTypeDo.setCheckItemId(dzCheckUpItemType.getCheckItemId());
            checkTypeDo.setDictCode(dzCheckUpItemType.getDictCode());
            checkTypeDo.setChecked(dzCheckUpItemType.getChecked());
            checkTypeDo.setCheckTypeId(dzCheckUpItemType.getCheckTypeId());
            for (SysDictItem sysDictItem : dictItems) {
                if (sysDictItem.getId().equals( dzCheckUpItemType.getDictItemId())) {
                    checkTypeDo.setItemText(sysDictItem.getItemText());
                    break;
                }
            }
            checkTypeDos.add(checkTypeDo);
        }
        for (DzCheckUpItem dzCheckUpItem : list) {
            DzCheckUpItemDo dzCheckUpItemDo = new DzCheckUpItemDo();
            BeanUtils.copyProperties(dzCheckUpItem, dzCheckUpItemDo);
            List<CheckTypeDo> checkTypeList = new ArrayList<>();
            for (CheckTypeDo checkTypeDo : checkTypeDos) {
                if (checkTypeDo.getCheckItemId().equals(dzCheckUpItem.getCheckItemId())) {
                    checkTypeList.add(checkTypeDo);
                }
            }
            dzCheckUpItemDo.setCheckTypeList(checkTypeList);
            dzCheckUpItemDos.add(dzCheckUpItemDo);
        }
        return Result.ok(dzCheckUpItemDos, info.getTotal());
    }

}
