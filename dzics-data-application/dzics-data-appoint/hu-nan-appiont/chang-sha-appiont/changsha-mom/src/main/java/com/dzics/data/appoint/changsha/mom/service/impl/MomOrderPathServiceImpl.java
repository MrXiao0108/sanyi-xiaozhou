package com.dzics.data.appoint.changsha.mom.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.config.param.MOMReqSys;
import com.dzics.data.appoint.changsha.mom.db.dao.MomMaterialPointDao;
import com.dzics.data.appoint.changsha.mom.db.dao.MomMaterialWarehouseDao;
import com.dzics.data.appoint.changsha.mom.db.dao.MomOrderPathDao;
import com.dzics.data.appoint.changsha.mom.db.dao.MomWaitCallMaterialDao;
import com.dzics.data.appoint.changsha.mom.exception.CustomMomException;
import com.dzics.data.appoint.changsha.mom.model.dto.IssueOrderInformation;
import com.dzics.data.appoint.changsha.mom.model.dto.mom.OprSequence;
import com.dzics.data.appoint.changsha.mom.model.entity.DzicsInsideLog;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrderPath;
import com.dzics.data.appoint.changsha.mom.model.entity.MomWaitCallMaterial;
import com.dzics.data.appoint.changsha.mom.model.vo.ResultDto;
import com.dzics.data.appoint.changsha.mom.service.CachingApi;
import com.dzics.data.appoint.changsha.mom.service.DzicsInsideLogService;
import com.dzics.data.appoint.changsha.mom.service.MomOrderPathService;
import com.dzics.data.appoint.changsha.mom.service.MomOrderService;
import com.dzics.data.appoint.changsha.mom.util.MomTaskType;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.pub.model.entity.DzProductionLine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单工序组工序组 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
@Service
@Slf4j
public class MomOrderPathServiceImpl extends ServiceImpl<MomOrderPathDao, MomOrderPath> implements MomOrderPathService {
    @Autowired
    private CachingApi cachingApi;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private MomWaitCallMaterialDao waitCallMaterialDao;
    @Autowired
    private MomMaterialPointDao pointDao;
    @Autowired
    private MomMaterialWarehouseDao warehouseDao;
    @Autowired
    private MOMReqSys momReqSys;
    @Value("${order.code}")
    private String orderCode;
    @Autowired
    private DzicsInsideLogService insideLogService;

    /**
     * 获取工序信息
     *
     * @param proTaskOrderId
     * @return
     */
    @Override
    public MomOrderPath getproTaskOrderId(String proTaskOrderId) {
        QueryWrapper<MomOrderPath> wp = new QueryWrapper<>();
        wp.eq("mom_order_id", proTaskOrderId);
        List<MomOrderPath> list = list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        } else {
            log.warn("根据MOM订单ID 获取的工序信息，工序不存在 proTaskOrderId : {}", proTaskOrderId);
            return null;
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public ResultDto saveOrderOprSequence(IssueOrderInformation<OprSequence> task,String msg) {
        DzicsInsideLog insideLog = new DzicsInsideLog();
        insideLog.setBusinessType(MomTaskType.MOM_ORDER_OPR_SEQUENCE);
        insideLog.setRequestContent(JSON.toJSONString(task));
        insideLog.setCreateTime(new Date());
        insideLog.setState("0");
        try {
            ResultDto resultDto = new ResultDto();
            resultDto.setVersion(task.getVersion());
            resultDto.setTaskId(task.getTaskId());
            resultDto.setCode("0");
            resultDto.setMsg("[MOM下发工序] 接收成功");

            DzProductionLine line = cachingApi.getOrderIdAndLineId();
            String orderId = line.getOrderId();
            String lineId = line.getId();
            String lineNo = line.getLineNo();
            String orderNo = line.getOrderNo();
            OprSequence sequence = task.getTask();
            String wipOrderNo = sequence.getWipOrderNo();
            String oprSequenceNo = sequence.getOprSequenceNo();
            String taskId = task.getTaskId();

            insideLog.setOrderId(orderId);
            insideLog.setOrderNo(orderNo);
            insideLog.setLineId(lineId);
            insideLog.setLineNo(lineNo);

            int version = task.getVersion();
            //        获取当前岛的所有工位
            List<String> stationCode = pointDao.getOrderNoLineNo(orderNo, lineNo);
            if (CollectionUtils.isEmpty(stationCode)) {
                insideLog.setState("1");
                insideLog.setThrowMsg("[MOM下发工序] 中控没有设置工位信息");
                insideLogService.save(insideLog);
                throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, "中控没有设置工位信息", version, taskId);
            }

            MomOrder momOrder = momOrderService.getMomOrder(wipOrderNo, orderId, lineId);
            if (momOrder == null) {
                resultDto.setCode("1");
                resultDto.setMsg("[MOM下发工序] 订单不存在");
                insideLog.setThrowMsg("Mom交互异常");
                insideLog.setState("1");
                insideLogService.save(insideLog);
                return resultDto;
//            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR52);
            }
            MomOrderPath path = getWipOrderOperSeqNo(wipOrderNo, oprSequenceNo);
            if (path != null) {
                resultDto.setCode("1");
                resultDto.setMsg("[MOM下发工序] 工序编号已存在");
                insideLog.setState("1");
                insideLog.setThrowMsg("Mom交互异常");
                insideLogService.save(insideLog);
                return resultDto;
//            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR44);
            }
            MomOrderPath momOrderPath = new MomOrderPath();
            momOrderPath.setMomOrderId(momOrder.getProTaskOrderId());
            momOrderPath.setJsonOriginalData(msg);
            BeanUtils.copyProperties(sequence, momOrderPath);
//        保存工序信息
            baseMapper.insert(momOrderPath);
//        保存待叫料信息
            String materialNo = momOrder.getProductNo();
            Integer quantity = momOrder.getQuantity() != null ? momOrder.getQuantity() : 0;
            MomWaitCallMaterial component = new MomWaitCallMaterial();
            component.setOrderNo(line.getOrderNo());
            component.setLineNo(line.getLineNo());

            Map<String, String> maps = momReqSys.getMaps();
            component.setReqSys(maps.get(orderCode));
            component.setSequenceNo(momOrderPath.getSequenceNo());
            component.setFacility(sequence.getFacility());
            component.setWipOrderNo(sequence.getWipOrderNo());
            component.setProductNo(materialNo);
            component.setMaterialName(momOrder.getProductName());
            component.setMomOrderId(momOrder.getProTaskOrderId());
            component.setMaterialType("1");//1产品物料 2 组件物料
            component.setOprSequenceNo(sequence.getOprSequenceNo());//工序号
            component.setMaterialNo(materialNo);//物料编码
            component.setQuantity(quantity);//叫料总数量
            component.setSucessQuantity(0);//已叫料总数量
            component.setSurplusQuantity(quantity);//剩余叫料总数
            component.setFalgStatus(false);
            component.setFalgOrderStatus(0);
            component.setOrgCode(momOrder.getOrgCode());
            component.setDelFlag(false);
            component.setWorkStation(sequence.getWorkStation());
            waitCallMaterialDao.insert(component);
            insideLogService.save(insideLog);
            return resultDto;
        }catch(Throwable throwable){
            throwable.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, MomOrderPath> wipOrderNoMapByWipOrderNo(Collection<String> coll) {
        List<MomOrderPath> list = this.list(Wrappers.<MomOrderPath>lambdaQuery()
                .in(MomOrderPath::getWipOrderNo, coll));
        return list.stream().collect(Collectors.toMap(MomOrderPath::getWipOrderNo, m -> m));
    }

    @Override
    public MomOrderPath getNewByMomOrder(String orderId) {
        return baseMapper.getNewByMomOrder(orderId);
    }

    private MomOrderPath getWipOrderOperSeqNo(String wipOrderNo, String oprSequenceNo) {
        QueryWrapper<MomOrderPath> wp = new QueryWrapper<>();
        wp.eq("WipOrderNo", wipOrderNo);
        wp.eq("OprSequenceNo", oprSequenceNo);
        MomOrderPath one = getOne(wp);
        return one;
    }
}
