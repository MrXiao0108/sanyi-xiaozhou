package com.dzics.data.maintain.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.maintain.model.entity.DzMaintainDevice;
import com.dzics.data.maintain.model.vo.MaintainDevice;
import com.dzics.data.maintain.model.vo.MaintainRecord;
import com.dzics.data.maintain.model.vo.MaintainRecordDetails;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 保养设备配置 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
public interface DzMaintainDeviceDao extends BaseMapper<DzMaintainDevice> {

    List<MaintainDevice> getMaintainList(@Param("lineId") String lineId, @Param("equipmentNo") String equipmentNo,
                                         @Param("states") String states, @Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime,
                                         @Param("field") String field, @Param("type") String type, @Param("useOrgCode") String useOrgCode);

    List<MaintainDevice> getMaintainListWait(@Param("lineId") String lineId, @Param("equipmentNo") String equipmentNo, @Param("states") String states,
                                             @Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime, @Param("field") String field,
                                             @Param("type") String type, @Param("now") LocalDate now);


    List<MaintainDevice> getMaintainListOver(@Param("lineId") String lineId, @Param("equipmentNo") String equipmentNo, @Param("states") String states,
                                             @Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime, @Param("field") String field,
                                             @Param("type") String type, @Param("now") LocalDate now);

    List<MaintainRecord> getMaintainRecord(@Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime, @Param("field") String field,
                                           @Param("type") String type, @Param("createBy") String createBy, @Param("maintainId") String maintainId);

    List<MaintainRecordDetails> getMaintainRecordDetails(@Param("maintainHistoryId") String maintainHistoryId);
}
