package com.dzics.data.appoint.changsha.mom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrderQrCode;

/**
 * <p>
 * mom 二维码 订单关系表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-08-10
 */
public interface MomOrderQrCodeService extends IService<MomOrderQrCode> {

    MomOrderQrCode getQrMomOrder(String producBarcode, String orderNo, String lineNo);


    /**
     * 二维码和订单绑定
     * @param qrcode
     * @param startOrder
     * @param orderCode
     * @param lineNo
     * @return 返回该订单数量
     */
    Integer bandMomOrderQrCode(String qrcode, MomOrder startOrder, String orderCode, String lineNo);
}
