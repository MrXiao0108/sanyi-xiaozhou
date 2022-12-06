package com.dzics.data.appoint.changsha.mom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.dto.CallMaterial;
import com.dzics.data.appoint.changsha.mom.model.dto.Task;
import com.dzics.data.appoint.changsha.mom.model.entity.MomWaitCallMaterial;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.pub.model.entity.DzProductionLine;

import java.util.List;

/**
 * <p>
 * 等待叫料的订单 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-10
 */
public interface MomWaitCallMaterialService extends IService<MomWaitCallMaterial> {

    List<CallMaterial> getWorkStation(String proTaskId, String stationCode);

    /**
     * 根据订单号 和工位 查询物料信息
     * @param wiporderno  MOM订单号
     * @param stationId 工位编号
     * @param orderCode
     * @param lineNo
     * @return
     */
    List<MomWaitCallMaterial> getWorkStationOrderId(String wiporderno, String stationId, String orderCode, String lineNo);

    String getOprSequenceNo(String workStation, String proTaskOrderId);

    CallMaterial getCallMaterial(String lineNo, String orderCode, List<CallMaterial> materials);

    boolean removeProTaskId(String proTaskOrderId);

    /**
     * 保存订单待叫料信息
     *
     * @param line
     * @param momOrder
     */
    /*void saveTaskOrderMaterial(DzProductionLine line, Task task, MomOrder momOrder);*/
}
