package com.dzics.data.wrp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.wrp.model.entity.DzWorkingFlow;

/**
 * <p>
 * 工件制作流程记录 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-19
 */
public interface DzWorkingFlowService extends IService<DzWorkingFlow> {
    DzWorkingFlow getQrCodeStationCode(String stationId, String qrCode, String orderId, String lineId);

    void updateByIdQrcode(DzWorkingFlow dzWorkingFlow);
}
