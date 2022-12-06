package com.dzics.data.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.business.service.ToolService;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.UnderlineTool;
import com.dzics.data.pub.db.dao.DzEquipmentDao;
import com.dzics.data.pub.db.dao.DzProductionLineDao;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.tool.db.dao.DzToolCompensationDataDao;
import com.dzics.data.tool.db.dao.DzToolGroupsDao;
import com.dzics.data.tool.db.dao.DzToolInfoDao;
import com.dzics.data.tool.db.model.dao.GetToolInfoDataListDo;
import com.dzics.data.tool.db.model.vo.GetToolInfoDataListVo;
import com.dzics.data.tool.model.dto.AddToolConfigureVo;
import com.dzics.data.tool.model.entity.DzToolCompensationData;
import com.dzics.data.tool.model.entity.DzToolGroups;
import com.dzics.data.tool.model.entity.DzToolInfo;
import com.dzics.data.tool.service.DzToolCompensationDataService;
import com.dzics.data.ums.service.DzicsUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Slf4j
public class ToolServiceImpl implements ToolService {
    @Autowired
    DzicsUserService sysUserServiceDao;
    @Autowired
    DzToolCompensationDataDao dzToolCompensationDataMapper;
    @Autowired
    DzEquipmentDao dzEquipmentMapper;
    @Autowired
    DzToolGroupsDao dzToolGroupsMapper;
    @Autowired
    DzProductionLineDao dzProductionLineMapper;
    @Autowired
    DzToolInfoDao dzToolInfoMapper;
    @Autowired
    DzToolCompensationDataService dzToolCompensationDataService;

    @Override
    public Result<List<DzToolCompensationData>> getToolConfigureList(String sub, PageLimit pageLimit, Integer groupNo) {
        String orgCode = sysUserServiceDao.getUserOrgCode(sub);
        if (pageLimit.getPage() != -1){
            PageHelper.startPage(pageLimit.getPage(),pageLimit.getLimit());
        }
        List<DzToolCompensationData> dzToolCompensationData = dzToolCompensationDataMapper.getToolConfigureList(pageLimit.getField(),pageLimit.getType(),orgCode,groupNo);
        PageInfo<DzToolCompensationData> info=new PageInfo<>(dzToolCompensationData);
        return new Result(CustomExceptionType.OK,info.getList(),info.getTotal());
    }

    @Override
    public Result addToolConfigure(AddToolConfigureVo addToolConfigureVo) {
        QueryWrapper<DzToolCompensationData> wrapper=new QueryWrapper<>();
        wrapper.eq("equipment_id",addToolConfigureVo.getEquipmentId());
        wrapper.eq("group_no",addToolConfigureVo.getGroupNo());
        wrapper.eq("tool_no",addToolConfigureVo.getToolNo());
        DzToolCompensationData dzToolCompensationData = dzToolCompensationDataMapper.selectOne(wrapper);
        if(dzToolCompensationData!=null){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_109);
        }
        String equipmentId = addToolConfigureVo.getEquipmentId();
        DzEquipment dzEquipment = dzEquipmentMapper.selectById(equipmentId);
        if(dzEquipment==null){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR,Message.ERR_111);
        }
        //判断设备是否绑定过相同编号的刀具
        QueryWrapper<DzToolCompensationData> toolNo = new QueryWrapper<DzToolCompensationData>()
                .eq("tool_no", addToolConfigureVo.getToolNo())
                .eq("equipment_id",addToolConfigureVo.getEquipmentId());
        List<DzToolCompensationData> dataList = dzToolCompensationDataMapper.selectList(toolNo);
        if(dataList.size()>0){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR,Message.ERR_112);
        }
        dzToolCompensationData=new DzToolCompensationData();
        dzToolCompensationData.setEquipmentId(addToolConfigureVo.getEquipmentId());
        dzToolCompensationData.setGroupNo(addToolConfigureVo.getGroupNo());
        dzToolCompensationData.setOrgCode(dzEquipment.getOrgCode());
        dzToolCompensationData.setToolNo(addToolConfigureVo.getToolNo());
        dzToolCompensationData.setOrderId(addToolConfigureVo.getOrderId());
        dzToolCompensationData.setLineId(addToolConfigureVo.getLineId());
        int insert = dzToolCompensationDataMapper.insert(dzToolCompensationData);
        return Result.ok(dzToolCompensationData);
    }

    @Override
    public Result getToolGroupsAll(String sub) {
        String orgCode = sysUserServiceDao.getUserOrgCode(sub);
        QueryWrapper<DzToolGroups> eq = new QueryWrapper<>();
        if (orgCode != null) {
            eq.eq("org_code", orgCode);
        }
        List<DzToolGroups> list = dzToolGroupsMapper.selectList(eq);
        return Result.ok(list);
    }

    @Override
    public Result<List<GetToolInfoDataListDo>> getToolInfoDataList(String sub, PageLimit pageLimit, GetToolInfoDataListVo getToolInfoDataListVo) {
        String orgCode = sysUserServiceDao.getUserOrgCode(sub);
        getToolInfoDataListVo.setOrgCode(orgCode);
        if (pageLimit.getPage() != -1) {
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        if (!StringUtils.isEmpty(getToolInfoDataListVo.getField())) {
            getToolInfoDataListVo.setField(UnderlineTool.humpToLine(getToolInfoDataListVo.getField()));
        }
        List<GetToolInfoDataListDo> dataList = dzToolCompensationDataMapper.getToolInfoDataList(getToolInfoDataListVo);
        PageInfo<GetToolInfoDataListDo> info = new PageInfo<>(dataList);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Override
    public Result addToolConfigureById(String byEquipmentId) {
        //判断设备是否绑定过刀具信息
        QueryWrapper<DzToolCompensationData> eq = new QueryWrapper<DzToolCompensationData>()
                .eq("equipment_id", byEquipmentId);
        List<DzToolCompensationData> dzToolCompensationData = dzToolCompensationDataMapper.selectList(eq);
        if(dzToolCompensationData.size()>0){
            log.error("该设备已经有绑定的刀具信息了，无法批量绑定");
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
        //判断设备是否是机床
        DzEquipment dzEquipment = dzEquipmentMapper.selectById(byEquipmentId);
        if(dzEquipment==null){
            log.error("刀具信息批量绑定，设备id不存在：{}",byEquipmentId);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
        if(dzEquipment.getEquipmentType().intValue()!= EquiTypeEnum.JC.getCode()){
            log.error("刀具信息批量绑定，要绑定的设备类型不是机床：{}",dzEquipment.getEquipmentType());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
        //创建批量绑定的对象
        List<DzToolCompensationData>dataList=new ArrayList<>();
        //查询所有刀具组
        List<DzToolGroups> dzToolGroups = dzToolGroupsMapper.selectList(new QueryWrapper<DzToolGroups>());
        List<Integer> collect = dzToolGroups.stream().map(p -> p.getGroupNo()).collect(Collectors.toList());
        //根据产线id查询订单id
        DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(dzEquipment.getLineId());
        if(dzProductionLine==null){
            log.error("刀具信息批量绑定，设备绑定的产线不存在，id：{}",dzEquipment.getLineId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
        //查询所有刀具组的刀具信息
        List<DzToolInfo> tool_groups_id = dzToolInfoMapper.selectList(new QueryWrapper<DzToolInfo>().in("tool_groups_id", collect));
        for (DzToolGroups dzToolGroup:dzToolGroups) {
            for (DzToolInfo dzToolInfo:tool_groups_id) {
                if(dzToolInfo.getToolGroupsId().equals(dzToolGroup.getToolGroupsId())){
                    DzToolCompensationData data=new DzToolCompensationData();
                    data.setEquipmentId(byEquipmentId);
                    data.setGroupNo(dzToolGroup.getGroupNo());
                    data.setToolNo(dzToolInfo.getToolNo());
                    data.setToolLife(0);
                    data.setToolLifeCounter(0);
                    data.setToolLifeCounterType(0);
                    data.setToolGeometryX(new BigDecimal("0"));
                    data.setToolGeometryY(new BigDecimal("0"));
                    data.setToolGeometryZ(new BigDecimal("0"));
                    data.setToolGeometryC(new BigDecimal("0"));
                    data.setToolGeometryRadius(new BigDecimal("0"));
                    data.setToolWearX(new BigDecimal("0"));
                    data.setToolWearY(new BigDecimal("0"));
                    data.setToolWearZ(new BigDecimal("0"));
                    data.setToolWearC(new BigDecimal("0"));
                    data.setToolWearRadius(new BigDecimal("0"));
                    data.setToolNoseDirection(0);
                    data.setOrgCode(dzEquipment.getOrgCode());
                    data.setOrderId(dzProductionLine.getOrderId());
                    data.setLineId(dzEquipment.getLineId());
                    dataList.add(data);
                }
            }
        };
        if(dataList.size()>0){
            boolean b = dzToolCompensationDataService.saveBatch(dataList);
        }
        return Result.ok();
    }

    @Override
    public Result getEquipmentByLine(String lineId) {
        QueryWrapper<DzEquipment> eq = new QueryWrapper<DzEquipment>()
                .eq("line_id", lineId)
                .eq("equipment_type", EquiTypeEnum.JC.getCode())
                .select("id","equipment_no","equipment_name");
        List<DzEquipment> dzEquipments = dzEquipmentMapper.selectList(eq);
        return Result.ok(dzEquipments);
    }
}
