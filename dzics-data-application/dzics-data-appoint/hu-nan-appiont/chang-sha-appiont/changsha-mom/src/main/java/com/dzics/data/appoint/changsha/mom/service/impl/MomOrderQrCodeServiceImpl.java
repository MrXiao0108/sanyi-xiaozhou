package com.dzics.data.appoint.changsha.mom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.db.dao.MomOrderQrCodeDao;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrderQrCode;
import com.dzics.data.appoint.changsha.mom.service.MomOrderQrCodeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * mom 二维码 订单关系表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-08-10
 */
@Service
public class MomOrderQrCodeServiceImpl extends ServiceImpl<MomOrderQrCodeDao, MomOrderQrCode> implements MomOrderQrCodeService {

    @Override
    public MomOrderQrCode getQrMomOrder(String producBarcode, String orderNo, String lineNo) {
        return baseMapper.getQrMomOrder(producBarcode, orderNo, lineNo);
    }



    @Override
    public Integer bandMomOrderQrCode(String qrcode, MomOrder startOrder, String orderCode, String lineNo) {
        //绑定存储记录
        MomOrderQrCode qrCode = new MomOrderQrCode();
        qrCode.setProTaskOrderId(startOrder.getProTaskOrderId());
        qrCode.setProductCode(qrcode);
        qrCode.setMomOrdeGuid(startOrder.getWiporderno());
        qrCode.setDelFlag(false);
        qrCode.setCreateBy("SYS");
        qrCode.setOrderNo(orderCode);
        qrCode.setLineNo(lineNo);
        return baseMapper.insert(qrCode);
    }

}
