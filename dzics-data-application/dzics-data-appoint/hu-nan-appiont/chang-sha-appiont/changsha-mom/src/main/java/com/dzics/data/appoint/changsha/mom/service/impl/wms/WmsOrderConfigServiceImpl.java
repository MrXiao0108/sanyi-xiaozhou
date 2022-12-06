package com.dzics.data.appoint.changsha.mom.service.impl.wms;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.db.dao.WmsOrderConfigDao;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.GetOrderCfig;
import com.dzics.data.appoint.changsha.mom.model.entity.WmsOrderConfig;
import com.dzics.data.appoint.changsha.mom.service.wms.WmsOrderConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-07
 */
@Service
public class WmsOrderConfigServiceImpl extends ServiceImpl<WmsOrderConfigDao, WmsOrderConfig> implements WmsOrderConfigService {

    @Override
    public List<GetOrderCfig> getCfg(String field, String type) {
        return this.baseMapper.getCfg(field, type);
    }


    //获取物料号
    @Override
    public WmsOrderConfig getMaterialCode(String materialCode) {
        QueryWrapper<WmsOrderConfig> wms = new QueryWrapper<>();
        //判断order_status与数据库中的订单状态是否一致
        wms.eq("order_status", false);
        //判断material_code是否在数据库中存在
        wms.eq("material_code", materialCode);
        //将获取到的值保存到wmsorderConfig中，
        WmsOrderConfig one = getOne(wms);
        //返回
        return one;
    }

    @Override
    public WmsOrderConfig addMaterialCode(String materialCode, String orderNum, String rfid) {
        WmsOrderConfig wmsOrderConfig = new WmsOrderConfig();
        wmsOrderConfig.setRfid(rfid);
        wmsOrderConfig.setOrderNum(orderNum);
        wmsOrderConfig.setMaterialCode(materialCode);
        wmsOrderConfig.setOrgCode("WMS-000");
        wmsOrderConfig.setDelFlag(false);
        wmsOrderConfig.setCreateBy("UI");
        this.save(wmsOrderConfig);
        return wmsOrderConfig;
    }

    @Override
    public void updateOrderNum(String orderNum) {
        WmsOrderConfig wmsOrderConfig = new WmsOrderConfig();
        wmsOrderConfig.setOrderStatus(true);
        QueryWrapper<WmsOrderConfig> wp = new QueryWrapper<>();
        wp.eq("order_num", orderNum);
        this.update(wmsOrderConfig, wp);
    }
}
