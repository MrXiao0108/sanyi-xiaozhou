package com.dzics.data.appoint.changsha.mom.service.impl.wms;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.appoint.changsha.mom.db.dao.MomOrderDao;
import com.dzics.data.appoint.changsha.mom.enums.InterfaceType;
import com.dzics.data.appoint.changsha.mom.model.constant.MomConstant;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.CallFrame;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.DzLocation;
import com.dzics.data.appoint.changsha.mom.model.dto.wms.DzOrderCompleted;
import com.dzics.data.appoint.changsha.mom.model.entity.WmsCallframeHistory;
import com.dzics.data.appoint.changsha.mom.model.entity.WmsOrderConfig;
import com.dzics.data.appoint.changsha.mom.model.vo.wms.CallFrameResp;
import com.dzics.data.appoint.changsha.mom.model.vo.wms.LocationResp;
import com.dzics.data.appoint.changsha.mom.model.vo.wms.WmsRespone;
import com.dzics.data.appoint.changsha.mom.service.wms.DzWmsService;
import com.dzics.data.appoint.changsha.mom.service.wms.WmsCallframeHistoryService;
import com.dzics.data.appoint.changsha.mom.service.wms.WmsOrderConfigService;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.SnowflakeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/12/6.
 * @since
 */
@Service
@Slf4j
public class DzWmsServiceImpl implements DzWmsService {
    @Autowired
    private LocationRequestAbstaract locationRequestAbstaract;
    @Autowired
    private PutCompletedAbstaract putCompletedAbstaract;
    @Autowired
    private WmsCallframeHistoryService wmsCallframeHistoryService;
    @Autowired
    private WmsOrderConfigService wmsOrderConfigService;
    @Autowired
    public SnowflakeUtil snowflakeUtil;
    @Autowired
    private MomOrderDao momOrderDao;

    /**
     * 呼叫料框
     *
     * @param dzCallFrame RFIDrfid信息  orderNum订单号   materialCode物料号
     * @return
     */
    @Override
    public synchronized Result callFrame(CallFrame dzCallFrame) {
        //获取物料号
        String materialCode = dzCallFrame.getMaterialCode();
        WmsOrderConfig wmsOrderConfig = wmsOrderConfigService.getMaterialCode(materialCode);
        if (wmsOrderConfig == null) {
            String st = MomConstant.ORDER_STATUS_START;
            String orderNum = momOrderDao.selProductNo(materialCode, st);
            if (StringUtils.isEmpty(orderNum)) {
                orderNum = "WMS-" + snowflakeUtil.nextId();
                log.warn("根据物料号:{},状态:{} 查询MOM订单不存在,自动生成订单号:{}", materialCode, st , orderNum);
            }
            //赋值RFID信息
            String rfid = "WMS-" + snowflakeUtil.nextId();
            //存放请求参数到实体类wmsorderconfigService中
            wmsOrderConfigService.addMaterialCode(materialCode, orderNum, rfid);
            //写入RFID信息,写入订单号
            dzCallFrame.setRfid(rfid);
            dzCallFrame.setOrderNum(orderNum);
        } else {
            //不为空则直接写入RFId信息、订单号
            dzCallFrame.setRfid(wmsOrderConfig.getRfid());
            dzCallFrame.setOrderNum(wmsOrderConfig.getOrderNum());
        }
        //调用呼叫料框接口
        WmsCallframeHistory history = new WmsCallframeHistory();
        //叫料接口类型
        history.setInterfaceType(InterfaceType.CallFrame.toString());
        //叫料接口写入参数
        history.setRfid(dzCallFrame.getRfid());
        history.setOrdernum(dzCallFrame.getOrderNum());
        history.setMaterialcode(materialCode);

        try {

            history.setSendingTime(new Date());
            CallFrameResp callFrameResp = locationRequestAbstaract.callFrame(dzCallFrame);
            history.setResponseTime(new Date());
            history.setResStatus(callFrameResp.getStatus());
            history.setResMessage(callFrameResp.getMessage());
            history.setResStation(callFrameResp.getStation());
            Result<Object> ok = Result.ok();
            if (callFrameResp.getStatus()) {
                ok.setMsg(callFrameResp.getMessage());
                ok.setData(callFrameResp);
                return ok;
            }
            ok.setMsg(callFrameResp.getMessage());
            ok.setCode(CustomExceptionType.SYSTEM_ERROR.getCode());
            return ok;
        } catch (Throwable throwable) {
            history.setResMessage(throwable.getMessage());
            log.error("请求WMS错误 dzCallFrame :{},信息: {} ", dzCallFrame, throwable.getMessage(), throwable);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        } finally {
            wmsCallframeHistoryService.save(history);
        }
    }

    /**
     * @param dzLocation
     * @return
     */
    @Override
    public synchronized Result locationRequest(DzLocation dzLocation) {
        String materialCode = dzLocation.getMaterialCode();
        WmsOrderConfig wmsOrderConfig = wmsOrderConfigService.getMaterialCode(materialCode);
        if (wmsOrderConfig == null) {
            log.error("机械手放货位置申请 根据物料号没有获取到 订单配置项，无法获取 RFID ; wmsOrderConfig: {}", wmsOrderConfig);
            Result ok = Result.ok();
            ok.setMsg(CustomResponseCode.ERR0.getChinese());
            ok.setCode(CustomExceptionType.SYSTEM_ERROR.getCode());
            return ok;
        }
        dzLocation.setRfid(wmsOrderConfig.getRfid());
        WmsCallframeHistory history = new WmsCallframeHistory();
        history.setInterfaceType(InterfaceType.LocationRequest.toString());
        history.setRfid(dzLocation.getRfid());
        history.setResStation(dzLocation.getStation());
        history.setMaterialcode(materialCode);
        try {
            history.setSendingTime(new Date());
            LocationResp locationResp = locationRequestAbstaract.rfidMaterialCodeStation(dzLocation);
            history.setResponseTime(new Date());
            history.setResStatus(locationResp.getStatus());
            history.setResMessage(locationResp.getMessage());
            history.setResAllowed(locationResp.getAllowed());
            Result ok = Result.ok();
            if (locationResp.getStatus()) {
                ok.setMsg(locationResp.getMessage());
                ok.setData(locationResp);
                return ok;
            }
            ok.setMsg(locationResp.getMessage());
            ok.setCode(CustomExceptionType.SYSTEM_ERROR.getCode());
            return ok;
        } catch (Throwable throwable) {
            history.setResMessage(throwable.getMessage());
            log.error("请求WMS错误 dzLocation :{},信息: {} ", dzLocation, throwable.getMessage(), throwable);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        } finally {
            wmsCallframeHistoryService.save(history);
        }
    }

    /**
     * @param dzOrderCompleted
     * @return
     */
    @Override
    public Result orderCompleted(DzOrderCompleted dzOrderCompleted) {
        String orderNum = dzOrderCompleted.getOrderNum();
        WmsCallframeHistory history = new WmsCallframeHistory();
        history.setInterfaceType(InterfaceType.OrderCompleted.toString());
        history.setOrdernum(orderNum);
        try {
            history.setSendingTime(new Date());
            WmsRespone wmsRespone = locationRequestAbstaract.orderCompleted(dzOrderCompleted);
            history.setResponseTime(new Date());
            history.setResStatus(wmsRespone.getStatus());
            history.setResMessage(wmsRespone.getMessage());
            Result ok = Result.ok();
            if (wmsRespone.getStatus()) {
                ok.setMsg(wmsRespone.getMessage());
//                设置配置订单完成
                wmsOrderConfigService.updateOrderNum(orderNum);
                return ok;
            }
            ok.setMsg(wmsRespone.getMessage());
            ok.setCode(CustomExceptionType.SYSTEM_ERROR.getCode());
            return ok;
        } catch (Throwable throwable) {
            history.setResMessage(throwable.getMessage());
            log.error("请求WMS错误 dzOrderCompleted :{},信息: {} ", dzOrderCompleted, throwable.getMessage(), throwable);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        } finally {
            wmsCallframeHistoryService.save(history);
        }
    }

    @Override
    public synchronized Result<WmsRespone> putCompleted(DzLocation putCompleted) {
        String materialCode = putCompleted.getMaterialCode();
        WmsOrderConfig wmsOrderConfig = wmsOrderConfigService.getMaterialCode(materialCode);
        if (wmsOrderConfig == null) {
            log.error("放料完成 根据物料号没有获取到 订单配置项，无法获取 RFID ; wmsOrderConfig: {}", wmsOrderConfig);
            Result ok = Result.ok();
            ok.setMsg(CustomResponseCode.ERR0.getChinese());
            ok.setCode(CustomExceptionType.SYSTEM_ERROR.getCode());
            return ok;
        }
        putCompleted.setRfid(wmsOrderConfig.getRfid());
        WmsCallframeHistory history = new WmsCallframeHistory();
        history.setInterfaceType(InterfaceType.PutCompleted.toString());
        history.setRfid(putCompleted.getRfid());
        history.setMaterialcode(putCompleted.getMaterialCode());
        history.setResStation(putCompleted.getStation());
        try {
            history.setSendingTime(new Date());
            WmsRespone wmsRespone = putCompletedAbstaract.rfidMaterialCodeStation(putCompleted);
            history.setResponseTime(new Date());
            history.setResStatus(wmsRespone.getStatus());
            history.setResMessage(wmsRespone.getMessage());
            Result ok = Result.ok();
            if (wmsRespone.getStatus()) {
                ok.setMsg(wmsRespone.getMessage());
                ok.setData(wmsRespone);
                return ok;
            }
            ok.setMsg(wmsRespone.getMessage());
            ok.setCode(CustomExceptionType.SYSTEM_ERROR.getCode());
            return ok;
        } catch (Throwable throwable) {
            history.setResMessage(throwable.getMessage());
            log.error("请求WMS错误 putCompleted :{},信息: {} ", putCompleted, throwable.getMessage(), throwable);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        } finally {
            wmsCallframeHistoryService.save(history);
        }
    }

    @Override
    public Result historical(PageLimitBase pageLimitBase) {
        PageHelper.startPage(pageLimitBase.getPage(), pageLimitBase.getLimit());
        QueryWrapper<WmsCallframeHistory> wp = new QueryWrapper<>();
        wp.orderByDesc("sending_time");
        List<WmsCallframeHistory> list = wmsCallframeHistoryService.list(wp);
        PageInfo<WmsCallframeHistory> wmsCallframeHistoryPageInfo = new PageInfo<>(list);
        return Result.ok(wmsCallframeHistoryPageInfo.getList(), wmsCallframeHistoryPageInfo.getTotal());
    }
}
