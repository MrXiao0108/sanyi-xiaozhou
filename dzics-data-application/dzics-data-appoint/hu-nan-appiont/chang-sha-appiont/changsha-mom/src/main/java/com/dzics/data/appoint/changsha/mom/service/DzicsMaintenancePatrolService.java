package com.dzics.data.appoint.changsha.mom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.entity.DzicsMaintenancePatrol;
import com.dzics.data.appoint.changsha.mom.model.vo.AddMainTenPatrolVo;
import com.dzics.data.appoint.changsha.mom.model.vo.EditMainTenPatrolVo;
import com.dzics.data.common.base.vo.Result;

import java.text.ParseException;
import java.util.List;

/**
 * <p>
 * 巡检维修表 服务类
 * </p>
 *
 * @author xnb
 * @since 2022-11-21
 */
public interface DzicsMaintenancePatrolService extends IService<DzicsMaintenancePatrol> {

    /**
     * 新增巡检维修
     *
     * @param addMainTenPatrolVo
     * @return Result
     */
    Result addPatrol(AddMainTenPatrolVo addMainTenPatrolVo);


    /**
     * 新增巡检维修
     *
     * @param editMainTenPatrolVo
     * @return Result
     */
    Result editPatrol(EditMainTenPatrolVo editMainTenPatrolVo);

    /**
     * 删除巡检维修
     *
     * @param Id
     * @return Result
     */
    Result delPatrol(String Id);

    /**
     * 查询巡检维修
     *
     * @param type
     * @param message
     * @return Result
     */
    List<DzicsMaintenancePatrol> getPatrol(String type, String message,String modelType);

    /**
     * 确认处理按钮
     *
     * @param id
     * @return Result
     */
    Result putHealed(String id) throws ParseException;

}
