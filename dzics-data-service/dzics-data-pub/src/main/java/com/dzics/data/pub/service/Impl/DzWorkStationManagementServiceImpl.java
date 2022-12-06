package com.dzics.data.pub.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.db.dao.DzWorkStationManagementDao;
import com.dzics.data.pub.model.dto.PutProcessShowVo;
import com.dzics.data.pub.model.dto.SelWorkStation;
import com.dzics.data.pub.model.entity.DzWorkStationManagement;
import com.dzics.data.pub.model.vo.ResWorkStation;
import com.dzics.data.pub.service.DzWorkStationManagementService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 工位表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
@Slf4j
@Service
public class DzWorkStationManagementServiceImpl extends ServiceImpl<DzWorkStationManagementDao, DzWorkStationManagement> implements DzWorkStationManagementService {

    @Autowired
    private DzWorkStationManagementDao stationManagementMapper;
    @Autowired
    private DzWorkStationManagementService dzWorkStationManagementService;

    private List<ResWorkStation> getWorkingStation(String field, String type, String stationCode, String workCode, String orderId, String lineId, String useOrgCode) {
        Long ordrIdl = null;
        if (!StringUtils.isEmpty(orderId)) {
            ordrIdl = Long.valueOf(orderId);
        }
        Long lineIdL = null;
        if (!StringUtils.isEmpty(lineId)) {
            lineIdL = Long.valueOf(lineId);
        }
        List<ResWorkStation> stations = stationManagementMapper.getWorkingStation(field, type, stationCode, workCode, ordrIdl, lineIdL, useOrgCode);
        return stations;
    }

    @Override
    public Result getLineId(String lineId) {
        if (StringUtil.isBlank(lineId)) {
            return new Result(CustomExceptionType.Parameter_Exception, Message.ERR_202);
        }
        QueryWrapper<DzWorkStationManagement> wp = new QueryWrapper<>();
        wp.eq("line_id", lineId);
        List<DzWorkStationManagement> list = list(wp);
        return Result.OK(list);
    }

    @Override
    public Result delWorkingStation(String stationId, String sub) {
        boolean b = dzWorkStationManagementService.removeById(stationId);
        return Result.ok();
    }

    @Override
    public Result<List<ResWorkStation>> getWorkingStation(SelWorkStation selWorkStation, String sub, String useOrgCode) {
        if (selWorkStation.getPage() != -1) {
            PageHelper.startPage(selWorkStation.getPage(), selWorkStation.getLimit());
        }
        List<ResWorkStation> stations = getWorkingStation(selWorkStation.getField(), selWorkStation.getType(),
                selWorkStation.getStationCode(), selWorkStation.getWorkCode(), selWorkStation.getOrderId(), selWorkStation.getLineId(), useOrgCode);
        PageInfo<ResWorkStation> info = new PageInfo<>(stations);
        return Result.ok(info.getList(), Long.valueOf(info.getTotal()));
    }

    @Override
    public Result putOnoff(PutProcessShowVo pShow) {
        boolean a = dzWorkStationManagementService.putOnoffShow(pShow);
        return Result.ok();
    }


    @Override
    public DzWorkStationManagement getWorkStationCode(String deviceCode, String orderId, String lineId) {
        QueryWrapper<DzWorkStationManagement> wp = new QueryWrapper<>();
        wp.eq("order_id", orderId);
        wp.eq("line_id", lineId);
        wp.eq("station_code", deviceCode);
        DzWorkStationManagement one = getOne(wp);
        log.info("DzWorkStationManagementServiceImpl [getWorkStationCode] one{} ", JSONObject.toJSONString(one));
        if (ObjectUtils.isEmpty(one)) {
            log.error("DzWorkStationManagementServiceImpl [getWorkStationCode] deviceCode{}, orderId{}, lineId{}", deviceCode, orderId, lineId);
        }
        return one;
    }

    @Override
    public DzWorkStationManagement getStationIdMergeCode(String mergeCode, String deviceCode, String orderId, String lineId) {
        QueryWrapper<DzWorkStationManagement> wp = new QueryWrapper<>();
        wp.eq("order_id", orderId);
        wp.eq("line_id", lineId);
        wp.ne("station_code", deviceCode);
        wp.eq("merge_code", mergeCode);
        DzWorkStationManagement one = getOne(wp);
        log.info("DzWorkStationManagementServiceImpl [getStationIdMergeCode] one{} ", JSONObject.toJSONString(one));
        if (ObjectUtils.isEmpty(one)) {
            log.error("DzWorkStationManagementServiceImpl [getWorkStationCode] orderId{}, lineId{}, deviceCode{}, mergeCode{}",
                    orderId, lineId, deviceCode, mergeCode);
        }
        return one;
    }

    @Override
    public boolean putOnoffShow(PutProcessShowVo processShowVo) {
        DzWorkStationManagement stationManagement = new DzWorkStationManagement();
        stationManagement.setStationId(processShowVo.getStationId());
        stationManagement.setOnOff(processShowVo.getOnOff());
        int i = stationManagementMapper.updateById(stationManagement);
        return i > 0 ? true : false;
    }
}
