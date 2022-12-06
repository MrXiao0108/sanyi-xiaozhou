package com.dzics.data.appoint.changsha.mom.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrderQrCode;

/**
 * <p>
 * mom 二维码 订单关系表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-08-10
 */
public interface MomOrderQrCodeDao extends BaseMapper<MomOrderQrCode> {

    default MomOrderQrCode getQrMomOrder(String productBarcode, String orderNo, String lineNo) {
        QueryWrapper<MomOrderQrCode> wp = new QueryWrapper<>();
        wp.eq("order_no", orderNo);
        wp.eq("line_no", lineNo);
        wp.eq("product_code", productBarcode);
        return selectOne(wp);
    }
}
