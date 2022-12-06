package com.dzics.data.pub.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.db.dao.DzDeviceAlarmConfigDao;
import com.dzics.data.pub.model.dto.GetDeivceAlarmConfig;
import com.dzics.data.pub.model.entity.DzDeviceAlarmConfig;
import com.dzics.data.pub.service.DzDeviceAlarmConfigService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 设备告警配置 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-30
 */
@Service
public class DzDeviceAlarmConfigServiceImpl extends ServiceImpl<DzDeviceAlarmConfigDao, DzDeviceAlarmConfig> implements DzDeviceAlarmConfigService {

    @Autowired
    private DzDeviceAlarmConfigService alarmConfigService;

    @Override
    public List<DzDeviceAlarmConfig> listCfg(String useOrgCode, String orderId, String lineId, String deivceId, Integer alarmGrade, String equipmentNo) {
        return this.baseMapper.listCfg(useOrgCode,orderId,lineId,deivceId,alarmGrade,equipmentNo);
    }

    @Override
    public Result<List<DzDeviceAlarmConfig>> getGiveAlarmConfig(GetDeivceAlarmConfig alarmConfig, String useOrgCode) {
        if(alarmConfig.getPage() != -1){
            PageHelper.startPage(alarmConfig.getPage(), alarmConfig.getLimit());
        }
        List<DzDeviceAlarmConfig> list = alarmConfigService.listCfg(useOrgCode,alarmConfig.getOrderId(),alarmConfig.getLineId(),alarmConfig.getDeivceId(),alarmConfig.getAlarmGrade(),alarmConfig.getEquipmentNo());
        PageInfo<DzDeviceAlarmConfig> info = new PageInfo<>(list);
        Result ok = Result.OK(info.getList());
        ok.setCount(info.getTotal());
        return ok;
    }

    @Override
    public Result delGiveAlarmConfig(String alarmConfigId, String sub) {
        alarmConfigService.removeById(alarmConfigId);
        return new Result(CustomExceptionType.OK);
    }
}
