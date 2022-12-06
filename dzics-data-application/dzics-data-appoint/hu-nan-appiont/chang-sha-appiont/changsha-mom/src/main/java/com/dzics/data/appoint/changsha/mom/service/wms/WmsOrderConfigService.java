package com.dzics.data.appoint.changsha.mom.service.wms;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.GetOrderCfig;
import com.dzics.data.appoint.changsha.mom.model.entity.WmsOrderConfig;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-07
 */
public interface WmsOrderConfigService extends IService<WmsOrderConfig> {

    List<GetOrderCfig> getCfg(String field, String type);

    WmsOrderConfig getMaterialCode(String materialCode);


    WmsOrderConfig addMaterialCode(String materialCode, String orderNum, String rfid);

    void updateOrderNum(String orderNum);
}
