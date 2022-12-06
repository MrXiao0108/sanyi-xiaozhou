package com.dzics.data.wrp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.wrp.db.dao.DzWorkingFlowDao;
import com.dzics.data.wrp.model.entity.DzWorkingFlow;
import com.dzics.data.wrp.service.DzWorkingFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 工件制作流程记录 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-19
 */
@Service
@Slf4j
public class DzWorkingFlowServiceImpl extends ServiceImpl<DzWorkingFlowDao, DzWorkingFlow> implements DzWorkingFlowService {

    @Override
    public DzWorkingFlow getQrCodeStationCode(String stationId, String qrCode, String orderId, String lineId) {
        QueryWrapper<DzWorkingFlow> wp = new QueryWrapper<>();
        wp.eq("station_id", stationId);
        wp.eq("qr_code", qrCode);
        wp.eq("line_id", lineId);
        wp.eq("order_id",orderId);
        wp.select("process_flow_id", "line_id", "order_id", "working_procedure_id", "station_id", "pro_task_id", "qr_code", "start_time", "complete_time");
        DzWorkingFlow list = getOne(wp);
        return list;
    }

    @Override
    public void updateByIdQrcode(DzWorkingFlow dzWorkingFlow) {
        QueryWrapper<DzWorkingFlow> wp = new QueryWrapper<>();
        wp.eq("process_flow_id",dzWorkingFlow.getProcessFlowId());
        wp.eq("qr_code",dzWorkingFlow.getQrCode());
        update(dzWorkingFlow,wp);
    }

}
