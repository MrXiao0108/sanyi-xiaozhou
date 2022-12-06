package com.dzics.data.appoint.changsha.mom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.dao.MomMaterialPointDo;
import com.dzics.data.appoint.changsha.mom.model.dto.MomUpPoint;
import com.dzics.data.appoint.changsha.mom.model.dto.SearchAGVParms;
import com.dzics.data.appoint.changsha.mom.model.entity.MomMaterialPoint;
import com.dzics.data.appoint.changsha.mom.model.vo.AddMaterialVo;
import com.dzics.data.appoint.changsha.mom.model.vo.UpdateMaterialVo;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.vo.DzicsStationCode;

import java.util.List;

/**
 * <p>
 * 料点编码 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-11-02
 */
public interface MomMaterialPointService extends IService<MomMaterialPoint> {

    /**
     * 查询小车对应上聊点
     *
     * @param basketType:
     * @param orderCode:
     * @param lineNo:
     * @return 查询结果
     * @author van
     * @date 2022/7/7
     */
    MomUpPoint getStationCode(String basketType, String orderCode, String lineNo);

    String getNextPoint(String orderCode, String lineNo,String basketType);

    String getNextPoint(String orderId, String id);

    String getNextPoint();

    /**
     * 查询AGV投料信息
     *
     * @param parms
     * @return
     */
    Result listQuery(SearchAGVParms parms);

    /**
     * 新增投料信息
     *
     * @param addMaterialVo
     * @return
     */
    Result addData(AddMaterialVo addMaterialVo);

    /**
     * 修改编辑投料信息
     * @param updateMaterialVo
     * @return
     */
    Result alterData(UpdateMaterialVo updateMaterialVo);

    /**
     * 删除投料信息
     *
     * @param materialPointId
     * @return
     */
    Result delData(String materialPointId);

    /**
     * 查询 Dz工位编号
     * @param lineId
     * @return
     */
    Result getDzStationCode(String lineId);
}
