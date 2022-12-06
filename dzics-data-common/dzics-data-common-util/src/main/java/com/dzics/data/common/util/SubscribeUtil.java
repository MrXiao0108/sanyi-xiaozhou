package com.dzics.data.common.util;


import com.dzics.data.common.base.dto.GetOrderNoLineNo;

/**
 * @Classname Subscribe
 * @Description 描述
 * @Date 2022/2/16 8:53
 * @Created by NeverEnd
 */
public  class SubscribeUtil {
    public static boolean checkOutParms(GetOrderNoLineNo orderNoLineNo) {
        if (orderNoLineNo != null) {
            if (orderNoLineNo.getOrderNo() != null && orderNoLineNo.getLineNo() != null) {
                return true;
            }
        }
        return false;
    }
}
