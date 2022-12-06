package com.dzics.data.tool.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.tool.db.dao.DzToolCompensationDataDao;
import com.dzics.data.tool.db.dao.DzToolInfoDao;
import com.dzics.data.tool.model.dto.AddToolConfigureVo;
import com.dzics.data.tool.model.entity.DzToolCompensationData;
import com.dzics.data.tool.model.entity.DzToolInfo;
import com.dzics.data.tool.service.DzToolCompensationDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 刀具补偿数据表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Service
@Slf4j
public class DzToolCompensationDataServiceImpl extends ServiceImpl<DzToolCompensationDataDao, DzToolCompensationData> implements DzToolCompensationDataService {
    @Autowired
    DzToolCompensationDataDao dzToolCompensationDataMapper;
    @Autowired
    DzToolInfoDao dzToolInfoMapper;

    @Override
    public Result delToolConfigure(String id) {
        dzToolCompensationDataMapper.deleteById(id);
        return Result.ok();
    }

    @Override
    public Result getToolByEqIdAndGroupNo(Long equipmentId, Integer groupNo,Long toolGroupsId) {
        List<DzToolInfo> list=dzToolInfoMapper.getToolByEqIdAndGroupNo(equipmentId,groupNo,toolGroupsId);
        return Result.ok(list);
    }

    @Override
    @Transactional
    public Result putToolConfigure(AddToolConfigureVo addToolConfigureVo) {
        DzToolCompensationData data = dzToolCompensationDataMapper.selectById(addToolConfigureVo.getId());
        if(data==null){
            log.error("刀具配置信息不存在,id：{}", addToolConfigureVo.getId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_108);
        }
        boolean a = data.getEquipmentId().equals(addToolConfigureVo.getEquipmentId());
        boolean b =data.getGroupNo().intValue()==addToolConfigureVo.getGroupNo().intValue();
        boolean c = data.getToolNo().intValue() == addToolConfigureVo.getToolNo().intValue();
        if(a&&b&&c){
            //数据没做更改
            return Result.ok();
        }

        QueryWrapper<DzToolCompensationData> wrapper=new QueryWrapper<>();
        wrapper.eq("equipment_id",addToolConfigureVo.getEquipmentId());
        wrapper.eq("tool_no",addToolConfigureVo.getToolNo());
        DzToolCompensationData dzToolCompensationData = dzToolCompensationDataMapper.selectOne(wrapper);
        if(dzToolCompensationData!=null){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_109);
        }
        data.setEquipmentId(addToolConfigureVo.getEquipmentId());
        data.setGroupNo(addToolConfigureVo.getGroupNo());
        data.setToolNo(addToolConfigureVo.getToolNo());
        int insert = dzToolCompensationDataMapper.updateById(data);
        return Result.ok(data);
    }

    @Override
    public Result getToolConfigureById(String id) {
        DzToolCompensationData dzToolCompensationData = dzToolCompensationDataMapper.selectById(id);
        return Result.ok(dzToolCompensationData);
    }
}
