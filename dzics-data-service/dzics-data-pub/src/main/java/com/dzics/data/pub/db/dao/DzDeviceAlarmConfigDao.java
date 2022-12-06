package com.dzics.data.pub.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pub.model.entity.DzDeviceAlarmConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 设备告警配置 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-30
 */
@Mapper
public interface DzDeviceAlarmConfigDao extends BaseMapper<DzDeviceAlarmConfig> {

    List<DzDeviceAlarmConfig> listCfg(@Param("useOrgCode") String useOrgCode, @Param("orderId") String orderId, @Param("lineId") String lineId, @Param("deivceId") String deivceId, @Param("alarmGrade") Integer alarmGrade, @Param("equipmentNo") String equipmentNo);

}
