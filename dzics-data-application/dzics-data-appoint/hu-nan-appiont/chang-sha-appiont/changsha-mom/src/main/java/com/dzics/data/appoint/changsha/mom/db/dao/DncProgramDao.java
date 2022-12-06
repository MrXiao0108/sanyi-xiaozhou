package com.dzics.data.appoint.changsha.mom.db.dao;

import com.dzics.data.appoint.changsha.mom.model.entity.DncProgram;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * dnc 换型信息 Mapper 接口
 * </p>
 *
 * @author van
 * @since 2022-06-28
 */
@Mapper
public interface DncProgramDao extends BaseMapper<DncProgram> {

    DncProgram successLately();

    List<DncProgram>getDncLog(@Param("equipmentCode")String equipmentCode,@Param("programName")String programName,
                              @Param("dncResponse")String dncResponse,@Param("state")String state,
                              @Param("beginTime")String beginTime,@Param("endTime")String endTime,@Param("filed")String filed,@Param("type")String type);

    /**
     * 查询当前设备最新一条DNC请求记录
     * @parme lineId
     * @parme equipmentNo
     * @return DncProgram
     * */
    DncProgram getOneNewDate(@Param("lineId")String lineId,@Param("equipmentNo")String equipmentNo);
}
