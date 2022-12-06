package com.dzics.data.pub.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.pub.model.dto.PutProcessShowVo;
import com.dzics.data.pub.model.entity.DzWorkStationManagement;
import com.dzics.data.pub.model.vo.DzicsStationCode;
import com.dzics.data.pub.model.vo.GetWorkStationDo;
import com.dzics.data.pub.model.vo.ResWorkStation;
import com.dzics.data.pub.model.vo.StationModelAll;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工位表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
@Mapper
@Repository
public interface DzWorkStationManagementDao extends BaseMapper<DzWorkStationManagement> {

    List<ResWorkStation> getWorkingStation(@Param("field") String field, @Param("type") String type, @Param("stationCode") String stationCode, @Param("workCode") String workCode,
                                           @Param("orderId") Long orderId, @Param("lineId") Long lineId, @Param("useOrgCode") String useOrgCode);

    List<StationModelAll> getSortPosition(@Param("orderId") String orderId, @Param("lineId") String lineId);

    Boolean putOnoffShow(PutProcessShowVo processShowVo);

    List<GetWorkStationDo> getWorkStationByLineId(@Param("lineId") String lineId);

    /**
     * @param orderId 订单
     * @param lineId  产线
     * @param onOff   1 展示 0 不展示
     * @return 返回该产线需要展示的工位
     */
    List<Map<String, Object>> getSortPositionOnOff(@Param("orderId") Long orderId, @Param("lineId") Long lineId, @Param("onOff") int onOff);

    List<DzicsStationCode> getDzicsStationCode(@Param("lineId") String lineId);

}
