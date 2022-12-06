package com.dzics.data.business.service;

import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.vo.PutIsShowVo;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pdm.db.model.dao.EquipmentDataDo;
import com.dzics.data.pdm.db.model.dto.EquipmentDownExcelDo;
import com.dzics.data.pdm.db.model.dto.SelectEquipmentDataVo;
import com.dzics.data.pdm.model.dto.GetByEquipmentNoVo;
import com.dzics.data.pub.model.dto.PutEquipmentVo;
import com.dzics.data.pub.model.dto.SelectEquipmentVo;
import com.dzics.data.pub.model.vo.AddEquipmentVo;
import com.dzics.data.pub.model.vo.EquipmentDo;
import com.dzics.data.pdm.model.vo.RobotDownExcelVo;
import com.dzics.data.pub.model.vo.EquipmentListDo;

import java.util.List;

public interface EquipmentService {
    /**
     * 根据设备类型和搜索条件查询设备数据
     * @param sub
     * @param robotEquipmentCode 设备类型
     * @param vo 查询条件
     * @return
     */
    Result<List<EquipmentDataDo>> listEquipmentData(String sub, Integer robotEquipmentCode, SelectEquipmentDataVo vo);

    Result<List<EquipmentDo>> list(String sub, Integer type, PageLimit pageLimit, SelectEquipmentVo data);

    Result getByEquipmentNo(String sub, GetByEquipmentNoVo getByEquipmentNoVo, PageLimit pageLimit);

    //设备添加通用方法
    Result add(String sub, AddEquipmentVo addEquipmentVo);

    Result put(String sub, PutEquipmentVo putEquipmentVo);

    Result getById(String sub, String id);

    Result<List<EquipmentListDo>> list(String sub, SelectEquipmentVo selectEquipmentVo);

    /**
     * 删除设备
     * @param sub
     * @param id
     * @return
     */
    Result del(String sub, Long id);

    /**
     * 控制设备是否显示
     * @param sub
     * @param putIsShowVo
     * @return
     */
    Result putIsShow(String sub, PutIsShowVo putIsShowVo);

    /**
     * 首页查询设备信息
     * @param lineId
     * @return
     */
    Result geEquipmentState(String lineId);

    /**
     * 机器人停机记录 Excel导出
     * */
    Result<List<RobotDownExcelVo>> getRobotDownExcel(String sub, EquipmentDownExcelDo selectEquipmentVo);
}
