package com.dzics.data.appoint.changsha.mom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.appoint.changsha.mom.model.entity.DzicsInsideLog;
import com.dzics.data.appoint.changsha.mom.model.vo.DzicsInsideVo;

import java.text.ParseException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xnb
 * @since 2022-11-12
 */
public interface DzicsInsideLogService extends IService<DzicsInsideLog> {

    /**
     * 查询后台 内部日志
     *
     * @param insideVo：实体
     * @return List<DzicsInsideLog>
     */
    List<DzicsInsideLog>getBackInsideLog(DzicsInsideVo insideVo) throws ParseException;
}
