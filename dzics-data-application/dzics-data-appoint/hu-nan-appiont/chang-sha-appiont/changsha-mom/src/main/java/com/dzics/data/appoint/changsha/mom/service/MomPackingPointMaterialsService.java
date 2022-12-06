package com.dzics.data.appoint.changsha.mom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.dao.GetPointMaterialDo;
import com.dzics.data.appoint.changsha.mom.model.entity.MomPackingPointMaterials;
import com.dzics.data.appoint.changsha.mom.model.vo.PointMaterialsModel;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xnb
 * @since 2022-10-31
 */
public interface MomPackingPointMaterialsService extends IService<MomPackingPointMaterials> {

    /**
     * 查询当前料点，下料框所绑定的Mom订单信息
     * @Parme PointMaterialsModel:投料点编码
     * @return List<GetPointMaterialDo>：返回结果集
     * */
    List<GetPointMaterialDo> getMaterialsByPoint(PointMaterialsModel pointMaterialsModel);

    /**
     * 发送mom装框信息接口，数据处理中转
     * @Parme point:料点编码
     * @return void
     * */
    void handlePackingData(String point);

    /**
     * 编辑-->移框 修改料框物料详情
     * @Parme pointMaterialsModel
     * @return void
     * */
    List<GetPointMaterialDo> putMaterialsByPoint(PointMaterialsModel pointMaterialsModel);

    /**
     * 查询当前料框详情，下料框所绑定的二维码信息
     * @Parme PointMaterialsModel:投料点编码
     * @return List<GetPointMaterialDo>：返回结果集
     * */
    List<GetPointMaterialDo> getMaterialsByPointMomOrder(PointMaterialsModel pointMaterialsModel);
}
