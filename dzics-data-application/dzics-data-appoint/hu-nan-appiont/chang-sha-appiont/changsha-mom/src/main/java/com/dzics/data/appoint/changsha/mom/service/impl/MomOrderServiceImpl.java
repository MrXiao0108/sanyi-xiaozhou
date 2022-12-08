package com.dzics.data.appoint.changsha.mom.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.config.MapConfig;
import com.dzics.data.appoint.changsha.mom.db.dao.MomOrderDao;
import com.dzics.data.appoint.changsha.mom.db.dao.MomOrderPathDao;
import com.dzics.data.appoint.changsha.mom.model.constant.MomConstant;
import com.dzics.data.appoint.changsha.mom.model.dao.MomOrderDo;
import com.dzics.data.appoint.changsha.mom.model.dto.IssueOrderInformation;
import com.dzics.data.appoint.changsha.mom.model.dto.PutMomOrder;
import com.dzics.data.appoint.changsha.mom.model.dto.SearchOrderParms;
import com.dzics.data.appoint.changsha.mom.model.dto.Task;
import com.dzics.data.appoint.changsha.mom.model.dto.mom.OprSequence;
import com.dzics.data.appoint.changsha.mom.model.entity.DncProgram;
import com.dzics.data.appoint.changsha.mom.model.entity.DzicsInsideLog;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrderPath;
import com.dzics.data.appoint.changsha.mom.model.vo.ResultDto;
import com.dzics.data.appoint.changsha.mom.mq.RabbitMQService;
import com.dzics.data.appoint.changsha.mom.service.*;
import com.dzics.data.appoint.changsha.mom.udp.UDPUtil;
import com.dzics.data.appoint.changsha.mom.util.MomTaskType;
import com.dzics.data.common.base.enums.EquiTypeEnum;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.exception.enums.CustomResponseCode;
import com.dzics.data.common.base.model.constant.DzUdpType;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.model.constant.LogClientType;
import com.dzics.data.common.base.model.constant.MomProgressStatus;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.date.DateUtil;
import com.dzics.data.logms.model.entity.SysRealTimeLogs;
import com.dzics.data.pms.model.entity.DzProduct;
import com.dzics.data.pms.service.DzProductService;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.entity.DzOrder;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.entity.DzWorkStationManagement;
import com.dzics.data.pub.model.vo.OrderIdLineId;
import com.dzics.data.pub.service.DzEquipmentService;
import com.dzics.data.pub.service.DzOrderService;
import com.dzics.data.pub.service.DzProductionLineService;
import com.dzics.data.pub.service.DzWorkStationManagementService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * mom下发订单表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
@Service
@Slf4j
public class MomOrderServiceImpl extends ServiceImpl<MomOrderDao, MomOrder> implements MomOrderService {

    @Value("${dzdc.udp.client.qr.port}")
    private Integer plcPort;
    @Value(("${accq.read.cmd.queue.equipment.realTime}"))
    private String logQuery;
    @Value("${order.code}")
    private String orderNo;

    @Autowired
    private MomOrderDao momOrderDao;
    @Autowired
    private MomOrderPathService momOrderPathService;
    @Autowired
    private MomOrderPathDao momOrderPathDao;
    @Autowired
    private DzProductionLineService dzProductionLineService;
    @Autowired
    private CachingApi cachingApi;
    @Autowired
    private DncProgramService dncProgramService;
    @Autowired
    private DzProductService dzProductService;
    @Autowired
    private DzOrderService dzOrderService;
    @Autowired
    private MapConfig mapConfig;
    @Autowired
    private RabbitMQService rabbitMQService;
    @Autowired
    private MomMaterialStorageService momMaterialStorageService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DzWorkStationManagementService managementService;
    @Autowired
    private MomOrderService orderService;
    @Autowired
    private DzicsInsideLogService insideLogService;
    @Autowired
    private ManageModeService manageModeService;
    @Autowired
    private DzEquipmentService equipmentService;


    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    @Override
    public ResultDto saveMomOrder(IssueOrderInformation<Task> information, String momParams) {
        DzicsInsideLog insideLog = new DzicsInsideLog();
        insideLog.setBusinessType(MomTaskType.MOM_ORDER_TYPE);
        insideLog.setRequestContent(JSON.toJSONString(information));
        insideLog.setCreateTime(new Date());
        insideLog.setState("0");
        try {
            DzProductionLine line = cachingApi.getOrderIdAndLineId();
            if (line == null) {
                log.error("根据配置文件配置的订单号,未在查询到产线 line:{}", line);
                insideLog.setState("1");
                insideLog.setThrowMsg("根据配置文件配置的订单号,未在查询到产线,line:"+line);
                insideLogService.save(insideLog);
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR592.getChinese());
            }
            insideLog.setOrderId(line.getOrderId());
            insideLog.setOrderNo(line.getOrderNo());
            insideLog.setLineId(line.getId());
            insideLog.setLineNo(line.getLineNo());

            if (information == null) {
                insideLog.setState("1");
                insideLog.setThrowMsg("Mom订单下发报文参数异常");
                insideLogService.save(insideLog);
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
            }
            Task task = information.getTask();
            String wipOrderNo = task.getWipOrderNo();
            String taskId = information.getTaskId();
            int version = information.getVersion();
            String taskType = information.getTaskType();
            if (StringUtil.isEmpty(task.getWipOrderNo()) || StringUtil.isEmpty(task.getWipOrderType()) || StringUtil.isEmpty(task.getWipOrderType()) || StringUtil.isEmpty(task.getProductionLine()) || StringUtil.isEmpty(task.getProductNo()) || StringUtil.isEmpty(task.getFacility()) || task.getQuantity() == 0 || StringUtil.isEmpty(task.getProgressStatus())) {
                log.error("MOM下发订单的 参数校验错误:{}", JSONObject.toJSONString(information));
//            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
                ResultDto resultDto = new ResultDto();
                resultDto.setVersion(information.getVersion());
                resultDto.setTaskId(information.getTaskId());
                resultDto.setCode("1");
                resultDto.setMsg("MOM下发订单的参数校验错误");
                insideLog.setState("1");
                insideLog.setThrowMsg("Mom交互异常");
                insideLogService.save(insideLog);
                return resultDto;
            }

            MomOrder upOrder = getMomOrder(wipOrderNo, line.getOrderId(), line.getId());
            ResultDto resultDto = new ResultDto();
            resultDto.setVersion(information.getVersion());
            resultDto.setTaskId(information.getTaskId());
            resultDto.setCode("0");
            if (upOrder == null) {
//            if(!MomConstant.ORDER_STATUS_LOWER.equals(task.getProgressStatus())){
//                resultDto.setCode("1");
//                resultDto.setMsg("当前订单在系统中未创建，无法执行Mom当前下发的指定订单状态");
//                return resultDto;
//            }
//          插入订单
                MomOrder mord = createMomOrder(task, wipOrderNo, taskId, version, taskType, line, momParams);
                boolean save = save(mord);
                resultDto.setMsg("[MOM下发订单] 接收成功");
                insideLogService.save(insideLog);
                return resultDto;
            } else {
//          更新订单
                resultDto.setMsg("[MOM下发订单] 已更新");
                String upState = upOrder.getProgressStatus();
                if (MomConstant.ORDER_STATUS_CLOSE.equals(upState)) {
                    log.error("[MOM下发订单] 订单已关闭,不能更新:{}", information);
                    resultDto.setCode("1");
                    resultDto.setMsg("[MOM下发订单] 订单已关闭,不能更新");
                    insideLog.setState("1");
                    insideLog.setThrowMsg("Mom交互异常");
                    insideLogService.save(insideLog);
                    return resultDto;
//                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR86);
                }
                if (MomConstant.ORDER_STATUS_DEL.equals(upState)) {
                    log.error("[MOM下发订单] 订单已删除,不能更新:{}", information);
                    resultDto.setCode("1");
                    resultDto.setMsg("[MOM下发订单] 订单已删除,不能更新");
                    insideLog.setState("1");
                    insideLog.setThrowMsg("Mom交互异常");
                    insideLogService.save(insideLog);
                    return resultDto;
//                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR86);
                }
                log.info("[MOM下发订单] 更新订单:{}", upOrder.getWiporderno());
                MomOrder nowOrder = createMomOrder(task, wipOrderNo, taskId, version, taskType, line, momParams);
                nowOrder.setProTaskOrderId(upOrder.getProTaskOrderId());
                String nStatus = nowOrder.getProgressStatus();
                if (MomConstant.ORDER_STATUS_LOWER.equals(upState)) {
//                      当前订单状态为下发的默认状态, 可以删除订单
                    if (nStatus.equals(upState)) {
                        updateById(nowOrder);
                        return resultDto;
                    }
                    if (!MomConstant.ORDER_STATUS_START.equals(nStatus)) {
                        resultDto.setCode("1");
                        resultDto.setMsg("[MOM下发订单] 该订单状态不允许修改");
                        insideLog.setState("1");
                        insideLog.setThrowMsg("Mom交互异常");
                        insideLogService.save(insideLog);
                        return resultDto;
//                    throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR86);
                    }
                    updateById(nowOrder);
                    return resultDto;
                }
                if (MomConstant.ORDER_STATUS_START.equals(upState)) {
//               当前订单状态为进行中, 可以强制关闭订单
                    if (!MomConstant.ORDER_STATUS_CLOSE.equals(nStatus)) {
                        resultDto.setCode("1");
                        resultDto.setMsg("[MOM下发订单] 该订单状态不允许修改");
                        insideLog.setState("1");
                        insideLog.setThrowMsg("Mom交互异常");
                        insideLogService.save(insideLog);
                        return resultDto;
//                    throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR86);
                    }
//               TODO 强制关闭订单 可能需要删除物料信息,减物料数量,中断现场操作 通知到机器人
                    updateById(nowOrder);
                    log.error("强制关闭订单:{}", nowOrder.getWiporderno());
                    return resultDto;
                }
                log.error("[MOM下发订单] 当前订单状态: {} 不正确,不能更新:{}", upState, JSONObject.toJSONString(upOrder));
                resultDto.setCode("1");
                resultDto.setMsg("[MOM下发订单] 当前订单状态:"+upState+"不正确,不能更新:"+JSONObject.toJSONString(upOrder));
                insideLog.setState("1");
                insideLog.setThrowMsg("Mom交互异常");
                insideLogService.save(insideLog);
                return resultDto;
//            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR86);
            }
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
        return null;
    }


    @Override
    public List<MomOrder> getWipOrderNo(String wipOrderNo, String orderNo, String lineNo) {
        OrderIdLineId lid = dzProductionLineService.getOrderNoAndLineNo(orderNo, lineNo);
        if (lid == null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR59.getChinese());
        }
        String lineId = lid.getLineId();
        String orderId = lid.getOrderId();
        QueryWrapper<MomOrder> wp = new QueryWrapper<>();
        wp.eq("WipOrderNo", wipOrderNo);
        wp.eq("order_id", orderId);
        wp.eq("line_id", lineId);
        List<MomOrder> list = list(wp);
        return list;
    }

    /**
     * 保存中控下发的订单
     *
     * @param task
     * @param wipOrderNo
     * @param taskId
     * @param version
     * @param taskType
     * @param line
     * @return
     */
    private MomOrder createMomOrder(Task task, String wipOrderNo, String taskId, int version, String taskType, DzProductionLine line, String momParms) {
        MomOrder momOrder = new MomOrder();
        momOrder.setTasktype(taskType);
        momOrder.setTaskid(taskId);
        momOrder.setVersion(version);
        momOrder.setProductAliasProductionLine(task.getProductAlias() + task.getProductionLine());
        momOrder.setWiporderno(wipOrderNo);
        momOrder.setWipOrderType(task.getWipOrderType());
        momOrder.setProductionLine(task.getProductionLine());
        momOrder.setWorkCenter(task.getWorkCenter());
        momOrder.setWipOrderGroup(task.getWipOrderGroup());
        momOrder.setGroupCount(task.getGroupCount());
        momOrder.setProductNo(task.getProductNo());
        momOrder.setProductName(task.getProductName());
        momOrder.setProductAlias(task.getProductAlias());
        momOrder.setJsonOriginalData(momParms);

        DzProduct dzProduct = dzProductService.getOne(Wrappers.<DzProduct>lambdaQuery()
                .eq(DzProduct::getSyProductAlias, task.getProductAlias()));
        if (!ObjectUtils.isEmpty(dzProduct)) {
            momOrder.setProductId(dzProduct.getProductId());
        }

        momOrder.setFacility(task.getFacility());
        momOrder.setQuantity(task.getQuantity());
        momOrder.setScheduledStartDate(DateUtil.stringDateToformatDateYmdHms(task.getScheduledStartDate()));
        momOrder.setScheduledCompleteDate(DateUtil.stringDateToformatDateYmdHms(task.getScheduledCompleteDate()));
        momOrder.setProgressStatus("110");
        momOrder.setOrgCode("MOM");
        momOrder.setDelFlag(false);
        momOrder.setCreateBy("MOM");
        momOrder.setOrderOldState("110");
        momOrder.setOrderOperationResult(2);
        momOrder.setOrderId(line.getOrderId());
        momOrder.setLineId(line.getId());
        momOrder.setParamRsrv1(task.getParamRsrv1());
        momOrder.setParamRsrv2(task.getParamRsrv2());
        return momOrder;
    }

    /**
     * 根据 中控订单号 产线ID 以及订单ID 查询订单
     *
     * @param wipOrderNo
     * @param orderId
     * @param lineId
     * @return
     */
    @Override
    public MomOrder getMomOrder(String wipOrderNo, String orderId, String lineId) {
        QueryWrapper<MomOrder> wp = new QueryWrapper<>();
        wp.eq("order_id", orderId);
        wp.eq("line_id", lineId);
        wp.eq("order_operation_result", 2);
        wp.eq("WipOrderNo", wipOrderNo);
        return getOne(wp);
    }

    @Override
    public MomOrder getByLoading(String orderNo, String lineNo, String loading) {
        OrderIdLineId orderNoAndLineNo = dzProductionLineService.getOrderNoAndLineNo(orderNo, lineNo);
        if (orderNoAndLineNo == null) {
            return null;
        }
        String orderId = orderNoAndLineNo.getOrderId();
        String lineId = orderNoAndLineNo.getLineId();
        QueryWrapper<MomOrder> wp = new QueryWrapper<>();
        wp.eq("order_id", orderId);
        wp.eq("line_id", lineId);
        wp.eq("ProgressStatus", loading);
        List<MomOrder> list = list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() > 1) {
                log.error("订单:{},产线:{},状态: {} ,存在多条订单记录", orderId, lineId, loading);
            }
            return list.get(0);
        }
        log.error("订单:{},产线:{},状态: {} 订单不存在", orderId, lineId, loading);
        return null;
    }

    @Override
    public MomOrder getOrderOperationResult(String orderId, String lineId, String operationResultLoading) {
        QueryWrapper<MomOrder> wp = new QueryWrapper<>();
        wp.eq("order_id", orderId);
        wp.eq("line_id", lineId);
        wp.eq("order_operation_result", operationResultLoading);
        List<MomOrder> list = list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() > 1) {
                log.error("订单:{},产线:{},状态: {} ,存在多条订单执行记录", orderId, lineId, operationResultLoading);
            }
            return list.get(0);
        }
        return null;
    }

    @Override
    public Result<List<MomOrderDo>> listQuery(SearchOrderParms parms) {
        PageHelper.startPage(parms.getPage(), parms.getLimit());
        List<MomOrderDo> orderDos = momOrderDao.getOrders(parms);
        PageInfo<MomOrderDo> info = new PageInfo<>(orderDos);
        List<MomOrderDo> list = info.getList();
        if (CollectionUtils.isEmpty(list)) {
            return Result.ok();
        }
        Set<String> wipOrderNoSet = list
                .stream()
                .map(MomOrderDo::getWiporderno)
                .collect(Collectors.toSet());
//        Map<String, MomOrderPath> wipOrderNoMap = momOrderPathService.wipOrderNoMapByWipOrderNo(wipOrderNoSet);
        Map<String, String> map = momMaterialStorageService.wipOrderNoTotal(wipOrderNoSet);

        DzProductionLine lineId = cachingApi.getOrderIdAndLineId();
        for (MomOrderDo momOrderDo : list) {
            momOrderDo.setLineName(lineId.getLineName());
            momOrderDo.setLineCode(lineId.getLineCode());
            momOrderDo.setOrderNo(lineId.getOrderNo());
            String storage = map.get(momOrderDo.getWiporderno());
            momOrderDo.setStorage(storage);
        }
        return Result.ok(list, info.getTotal());
    }

    @Override
    public void changeOrder(String proTaskOrderId) {
        MomOrder one = this.getOne(Wrappers.<MomOrder>lambdaQuery().eq(MomOrder::getProTaskOrderId, proTaskOrderId));
        dncProgramService.changeProgram(one, true, null);
    }


//    @Override
//    public void orderStart(String proTaskOrderId) {
//        MomOrder momOrder = this.getOne(Wrappers.<MomOrder>lambdaQuery().eq(MomOrder::getProTaskOrderId, proTaskOrderId));
//        this.orderStart(null,momOrder, null,null);
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void orderStart(MomOrder oldOrder,MomOrder momOrder,List<String> equipmentIds,String type) {
        boolean startInit = this.orderStartInit(momOrder, equipmentIds);
        if (!startInit) {
            log.error("MomOrderServiceImpl [orderStart] 订单发送初始化异常{}", JSON.toJSONString(momOrder));
            return;
        }
        this.sendMessageForRobot(oldOrder,momOrder, DzUdpType.controlStar,type);
    }

    @Override
    public void orderClose(MomOrder momOrder,MomOrder momOrder1,String type) {
        this.sendMessageForRobot(momOrder,momOrder1, DzUdpType.controlstop,type);
    }

    @Override
    public void orderStop(MomOrder momOrder,MomOrder momOrder1,String type) {
        this.sendMessageForRobot(momOrder,momOrder1, DzUdpType.controlStarStop,type);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void orderRecover(MomOrder momOrder,MomOrder momOrder1,String type) {
        List<String>equipmentIds=new ArrayList<>();
        //查询需要恢复的Mom订单设备请求DNC记录
        List<DncProgram> dncPrograms = dncProgramService.getBaseMapper().selectList(new QueryWrapper<DncProgram>()
                .eq("state", "3")
                .eq("line_id", momOrder1.getLineId())
                .eq("pro_task_order_id", momOrder1.getProTaskOrderId()));
        for (DncProgram dncProgram : dncPrograms) {
            equipmentIds.add(equipmentService.getOne(new QueryWrapper<DzEquipment>().eq("equipment_no", dncProgram.getMachineCode())).getId());
        }
        boolean startInit = this.orderStartInit(momOrder, equipmentIds);
        if (!startInit) {
            log.error("MomOrderServiceImpl [orderStart] 订单发送初始化异常{}", JSON.toJSONString(momOrder));
            return;
        }
        this.sendMessageForRobot(momOrder,momOrder1, DzUdpType.controlStarStopStart,type);
    }

    @Override
    public boolean orderStartInit(MomOrder momOrder, List<String> equipmentIds) {
        return dncProgramService.initProgram(momOrder, equipmentIds);
    }

    /**
     * 开始订单
     *
     * @param putMomOrder
     * @return
     */
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    @Override
    public Result orderStart(PutMomOrder putMomOrder) {
        List<String>equipmentIds=new ArrayList<>();
        String runType = manageModeService.getByCode("3").getType();
        //手动必须选择一台设备
        if("1".equals(runType)){
            if(CollectionUtils.isEmpty(putMomOrder.getEquipmentIds())){
                throw new CustomException(CustomExceptionType.Parameter_Exception,"必须最少选择一台设备");
            }
            equipmentIds = putMomOrder.getEquipmentIds();
        }else{
            //精加工的自动模式也必须手动选择设备
            if(MomConstant.ORDER_DZ_1974.equals(orderNo) || MomConstant.ORDER_DZ_1975.equals(orderNo)){
                if(CollectionUtils.isEmpty(putMomOrder.getEquipmentIds())){
                    throw new CustomException(CustomExceptionType.Parameter_Exception,"必须最少选择一台设备");
                }
                equipmentIds = putMomOrder.getEquipmentIds();
            }
        }
        MomOrder byId = orderService.getById(putMomOrder.getProTaskOrderId());
        //根据订单号获取订单状态，判断状态码是否为进行中
        if (StringUtils.isEmpty(putMomOrder.getProgressStatus())) {
            log.error("订单状态为空");
            throw new CustomException(CustomExceptionType.Parameter_Exception,CustomResponseCode.ERR12.getChinese());
        }
        String type = "";
        if(MomConstant.ORDER_DZ_1974.equals(orderNo) || MomConstant.ORDER_DZ_1975.equals(orderNo)){
            type = vaildOrder(putMomOrder);
        }else{
            //判断有没有操作在进行中
            QueryWrapper<MomOrder> wp = new QueryWrapper<>();
            wp.eq("line_id", putMomOrder.getLineId());
            wp.and(wapper -> wapper.eq("ProgressStatus", MomProgressStatus.LOADING)
//                    .or().eq("ProgressStatus", MomProgressStatus.STOP)
                    .or().eq("order_operation_result", 1));
            List<MomOrder> list = orderService.list(wp);
            if (CollectionUtils.isNotEmpty(list)) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR51);
            }
        }
        MomOrder order = new MomOrder();
        try {
            BeanUtils.copyProperties(byId,order);
        }catch(Exception e){
            e.printStackTrace();
        }
        order.setProgressStatus(putMomOrder.getProgressStatus());
        order.setOrderOldState(byId.getProgressStatus());
        order.setOrderOperationResult(1);
        boolean b = this.updateById(order);
        if (b) {
            log.info("开始订单：{}", JSON.toJSONString(order));
            this.orderStart(byId,order,equipmentIds,type);
        }
        return Result.ok(b);
    }

    /**
     * 暂停订单
     *
     * @param putMomOrder
     * @return
     */
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    @Override
    public Result orderStop(PutMomOrder putMomOrder) {
        MomOrder monOrder = this.getById(putMomOrder.getProTaskOrderId());
        if (monOrder == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_33);
        }
        if (monOrder.getOrderOperationResult().intValue() == 1) {
            log.warn("订单上个操作未完成，不允许强制关闭:{}", putMomOrder);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_126);
        }
        String type = "";
        if(MomConstant.ORDER_DZ_1974.equals(orderNo) || MomConstant.ORDER_DZ_1975.equals(orderNo)){
            type = vaildOrder(putMomOrder);
        }
        //暂停订单
        MomOrder monOrder1 = new MomOrder();
        try {
            BeanUtils.copyProperties(monOrder,monOrder1);
        }catch(Exception e){
            e.printStackTrace();
        }
        monOrder1.setProTaskOrderId(putMomOrder.getProTaskOrderId());
        monOrder1.setProgressStatus(putMomOrder.getProgressStatus());
        monOrder1.setOrderOldState(monOrder.getProgressStatus());
        monOrder1.setOrderOperationResult(1);//操作进行中
        boolean b = this.updateById(monOrder1);
        if (b) {
            log.info("暂停订单:{}", JSON.toJSONString(monOrder));
            this.orderStop(monOrder,monOrder1,type);
        }
        return Result.OK(b);
    }


    /**
     * 强制关闭订单
     *
     * @param putMomOrder
     * @return
     */
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    @Override
    public Result forceClose(PutMomOrder putMomOrder) {
        //判断当前订单的操作是否完成
        MomOrder byId = orderService.getById(putMomOrder.getProTaskOrderId());
        if (byId == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_33);
        }
        if (byId.getOrderOperationResult().intValue() == 1) {
            log.warn("订单上个操作未完成，不允许强制关闭:{}", putMomOrder);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_126);
        }
        String type = "";
        if(MomConstant.ORDER_DZ_1974.equals(orderNo) || MomConstant.ORDER_DZ_1975.equals(orderNo)){
            type = vaildOrder(putMomOrder);
        }
        //强制关闭指定订单
        MomOrder monOrder = new MomOrder();
        try {
            BeanUtils.copyProperties(byId,monOrder);
        }catch(Exception e){
            e.printStackTrace();
        }
        monOrder.setProgressStatus(putMomOrder.getProgressStatus());
        monOrder.setOrderOperationResult(1);
        monOrder.setOrderOldState(byId.getProgressStatus());
        boolean b = this.updateById(monOrder);
        this.orderClose(byId,monOrder,type);
        return Result.OK(b);
    }

    /**
     * 恢复订单
     *
     * @param putMomOrder
     * @return
     */
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    @Override
    public Result orderRecover(PutMomOrder putMomOrder) {
        MomOrder monOrder = this.getById(putMomOrder.getProTaskOrderId());
        if (monOrder == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_33);
        }
        if (monOrder.getOrderOperationResult().intValue() == 1) {
            log.warn("订单上个操作未完成，不允许强制关闭:{}", putMomOrder);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_126);
        }
        //判断订单是否在暂停状态
        if (!monOrder.getProgressStatus().equals(MomProgressStatus.STOP)) {
            log.warn("订单不是暂停状态:{}", putMomOrder);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_7);
        }
        String type = "";
        if(MomConstant.ORDER_DZ_1974.equals(orderNo) || MomConstant.ORDER_DZ_1975.equals(orderNo)){
            type = vaildOrder(putMomOrder);
        }else{
            //判断有没有操作在进行中
            QueryWrapper<MomOrder> wp = new QueryWrapper<>();
            wp.eq("line_id", putMomOrder.getLineId());
            wp.and(wapper -> wapper.eq("ProgressStatus", MomProgressStatus.LOADING)
//                    .or().eq("ProgressStatus", MomProgressStatus.STOP)
                    .or().eq("order_operation_result", 1));
            List<MomOrder> list = orderService.list(wp);
            if (CollectionUtils.isNotEmpty(list)) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR51);
            }
        }
        MomOrder monOrder1 = new MomOrder();
        try {
            BeanUtils.copyProperties(monOrder,monOrder1);
        }catch(Exception e){
            e.printStackTrace();
        }
        monOrder1.setProgressStatus(putMomOrder.getProgressStatus());
        monOrder1.setOrderOperationResult(1);//操作进行中
        monOrder1.setOrderOldState(monOrder.getProgressStatus());
        boolean b = this.updateById(monOrder1);
        if (b) {
            log.info("恢复订单:{}", JSON.toJSONString(monOrder));
            this.orderRecover(monOrder,monOrder1,type);
        }
        return Result.OK(b);
    }

    /**
     * 订单作废
     *
     * @param proTaskOrderId
     * @return
     */
    @Override
    public Result orderDelete(String proTaskOrderId) {
        MomOrder byId = this.getById(proTaskOrderId);
        if (byId != null) {
            if (byId.getProgressStatus().equals(MomProgressStatus.DOWN) && byId.getOrderOperationResult().intValue() == 2) {
                //当订单已下达且已完成时才能作废
//                byId.setOrderOperationResult(2);
                byId.setProgressStatus(MomProgressStatus.DELETE);
                boolean b = this.updateById(byId);
                return Result.ok(b);
            } else if (byId.getProgressStatus().equals(MomProgressStatus.LOADING) && byId.getOrderOperationResult().intValue() == 1) {
                log.error("订单正在装载中，不允许作废:{}", byId);
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_126);
            }
        } else {
            log.error("订单不存在:{}", proTaskOrderId);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_6);
        }
        return Result.ok(false);
    }

    /**
     * 查询物料详情
     *
     * @param proTaskOrderId
     * @return
     */
    @Override
    public Result orderMaterialDetail(String proTaskOrderId) {
        return null;
    }

    @Override
    public boolean sendMessageForRobot(MomOrder oldOrder,MomOrder momOrder, String dzUdpType,String type) {
        log.info("MomOrderServiceImpl [sendMessageForRobot] 开始向产线发送通知{}", JSON.toJSONString(momOrder));
        try {
            String wipOrderNo = momOrder.getWiporderno();
            String productAliasProductionLine = momOrder.getProductAliasProductionLine();
            DzOrder order = dzOrderService.getById(momOrder.getOrderId());
            DzProductionLine line = cachingApi.getOrderIdAndLineId();
            if (order == null || line == null) {
                log.info("MomOrderServiceImpl [sendMessageForRobot] 大正订单或产线不存在 订单{}，产线{}", JSON.toJSONString(order), JSON.toJSONString(line));
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR48);
            }
            String nextOprSeqNo = "";
            String orderNo = order.getOrderNo();
            String lineNo = line.getLineNo();
            List<MomOrderPath> paths = momOrderPathDao.selectList(new QueryWrapper<MomOrderPath>().eq("mom_order_id",momOrder.getProTaskOrderId()));
            if(CollectionUtils.isNotEmpty(paths)){
                nextOprSeqNo = paths.get(0).getOprSequenceNo();
            }else{
                log.info("MomOrderServiceImpl [sendMessageForRobot] 大正订单或产线不存在 订单{}，产线{}", JSON.toJSONString(order), JSON.toJSONString(line));
                throw new CustomException(CustomExceptionType.SYSTEM_ERROR,"当前Mom订单："+wipOrderNo+"工序不存在，请检查Mom是否下发工序");
            }
            SysRealTimeLogs timeLogs = new SysRealTimeLogs();
            timeLogs.setMessageId(UUID.randomUUID().toString().replace("-", ""));
            timeLogs.setQueueName(logQuery);
            timeLogs.setClientId(LogClientType.BUS_AGV);
            timeLogs.setOrderCode(orderNo);
            timeLogs.setLineNo(lineNo);
            timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
            timeLogs.setDeviceCode(FinalCode.Device_Code);
            timeLogs.setMessageType(1);
            if (DzUdpType.controlStar.equals(dzUdpType)) {
                timeLogs.setMessage("点击订单：" + wipOrderNo + " 开始执行");
            }
            if (DzUdpType.controlStarStop.equals(dzUdpType)) {
                timeLogs.setMessage("点击订单：" + wipOrderNo + " 暂停");
            }
            if (DzUdpType.controlstop.equals(dzUdpType)) {
                timeLogs.setMessage("点击订单：" + wipOrderNo + " 强制关闭");
            }
            if (DzUdpType.controlStarStopStart.equals(dzUdpType)) {
                timeLogs.setMessage("点击订单：" + wipOrderNo + " 恢复执行");
            }
            timeLogs.setTimestampTime(new Date());
            //发送到日志队列
            boolean rab = rabbitMQService.sendRabbitmqLog(JSONObject.toJSONString(timeLogs));
            //将信号发送到 mq 触发到 UDP 发送
            Map<String, String> mapIps = mapConfig.getMaps();
            String plcIp = mapIps.get(orderNo);
            if (StringUtils.hasText(plcIp)) {
                String msx = "";
                //粗加工区分下发加工区域
                if(MomConstant.ORDER_DZ_1974.equals(orderNo) || MomConstant.ORDER_DZ_1975.equals(orderNo)){
                    msx = "Q," + DzUdpType.udpCmdControl + "," + DzUdpType.udpCmdControlInner + "," + dzUdpType + "," + productAliasProductionLine + "," + orderNo + "," + lineNo + "," + wipOrderNo + "," + nextOprSeqNo + "," + momOrder.getProductNo() + "," + momOrder.getQuantity() + "," + "123456,"+type;
                }else{
                    msx = "Q," + DzUdpType.udpCmdControl + "," + DzUdpType.udpCmdControlInner + "," + dzUdpType + "," + productAliasProductionLine + "," + orderNo + "," + lineNo + "," + wipOrderNo + "," + nextOprSeqNo + "," + momOrder.getProductNo() + "," + momOrder.getQuantity() + "," + "123456";
                }
                log.info("下发订单指令信息:{}", msx);
                UDPUtil.sendUdpServer(plcIp, plcPort, msx);
                rabbitMQService.sendMsgOrder(JSON.toJSONString(oldOrder));
            } else {
                log.error("下发订单指令发送到UDP订单: {} ,产线: {} 配置的IP不存在mapIps: {} ", lineNo, orderNo, mapIps);
            }
        } catch (Throwable throwable) {
            log.error("操作订单状态异常:{} ", throwable.getMessage(), throwable);
            throw throwable;
        }
        return false;
    }

    /**
     * 更新订单生产数量
     *
     * @param momOrder
     * @param wipOrderNo
     * @param quantity
     * @return
     */
    @Override
    public MomOrder updateQuantity(MomOrder momOrder, String wipOrderNo, String quantity) {
        boolean isUpdate = false;
        int orderOutput = 0;
        MomOrder momUpdate = new MomOrder();
        if (momOrder != null) {
            int quantitInt = momOrder.getQuantity().intValue();
            orderOutput = Integer.valueOf(quantity).intValue();
            momUpdate.setWiporderno(wipOrderNo);
            momUpdate.setOrderOutput(orderOutput);
            if (orderOutput >= quantitInt) {
                momUpdate.setOrderOldState(MomProgressStatus.SUCCESS);
                momUpdate.setProgressStatus(MomProgressStatus.SUCCESS);
                momUpdate.setRealityCompleteDate(new Date());
            }
            QueryWrapper<MomOrder> wp = new QueryWrapper<>();
            wp.eq("WipOrderNo", wipOrderNo);
            wp.eq("order_id", momOrder.getOrderId());
            wp.eq("line_id", momOrder.getLineId());
            isUpdate = update(momUpdate, wp);
        } else {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR48);
        }
        if (momUpdate.getProgressStatus() != null) {
            momOrder.setProgressStatus(momUpdate.getProgressStatus());
            momOrder.setRealityCompleteDate(momUpdate.getRealityCompleteDate());
        }
        if (!isUpdate) {
            log.warn("更新订单:{}生产数量:{}失败", momUpdate.getWiporderno(), quantity);
            return momOrder;
        } else {
            momOrder.setOrderOutput(orderOutput);
            return momOrder;
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Throwable.class)
    public MomOrder updateOrderStates(MomOrder momOrder, String value) {
        log.info("MomOrderServiceImpl [updateOrderStates] 更新订单状态 momOrder : {}, value: {}", momOrder, value);
        if (StringUtils.isEmpty(value)) {
            log.warn("MOM订单 或 控制是否成功值 为空  value : {}", value);
            return null;
        }
        String progressStatus = momOrder.getProgressStatus();
        if (DzUdpType.ok.equals(value)) {
            log.info("MomOrderServiceImpl [updateOrderStates] DzUdpType.ok.equals(value)");
            /*机器人回复 订单控制状态 执行成功 把中间状态变成 执行完成的状态 ，例如 点击开始订单，
            点击后 变为 开始中，下发指令到机器人，机器人收到指令，运行成功后，回复执行成功。
            更新之前订单状态为当前状态, 也就是需要吧中间状态变成，执行成功的状态*/
            momOrder.setOrderOperationResult(2);
            momOrder.setOrderOldState(progressStatus);//操作成功，把旧状态和当前状态同步
            if (MomProgressStatus.LOADING.equals(progressStatus)) {
                log.info("MomOrderServiceImpl [updateOrderStates] MomProgressStatus.LOADING.equals(progressStatus)");
                log.info("订单开始成功");
            }
            if (MomProgressStatus.CLOSE.equals(progressStatus) || MomProgressStatus.DELETE.equals(progressStatus)) {
                log.info("MomOrderServiceImpl [updateOrderStates] MomProgressStatus.CLOSE.equals(progressStatus)");
//                momWorkReportSortDao.deleteByProTaskId(momOrder.getProTaskOrderId());
                log.info("订单：{}，强制关闭", momOrder.getProTaskOrderId());
            }
        }else if(DzUdpType.err.equals(value)){
            log.info("MomOrderServiceImpl [updateOrderStates] MomProgressStatus.CLOSE.equals(progressStatus)");
//         机器人回复 订单控制状态 执行失败，由 中间状态，恢复回去。 例如 ： 开始中，变为 为开始，也就是需要变为上次的订单状态
            momOrder.setOrderOperationResult(2);
            momOrder.setProgressStatus(momOrder.getOrderOldState());//把订单旧状态赋值给当前状态
        }
        if (MomProgressStatus.LOADING.equals(progressStatus)) {
            momOrder.setRealityStartDate(new Date());
        }
        if (MomProgressStatus.SUCCESS.equals(progressStatus)) {
            momOrder.setRealityCompleteDate(new Date());
        }
        if (MomProgressStatus.CLOSE.equals(progressStatus)) {
            momOrder.setRealityCompleteDate(new Date());
        }
        log.warn("更新动作前订单结果 momOrder: {}", momOrder);
        boolean b = updateById(momOrder);
        log.warn("更新订单结果：{},momOrder: {}", b, momOrder);
        return momOrder;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Throwable.class)
    public Result forwardOrder(String proTaskOrderId) {
        if(MomConstant.ORDER_DZ_1974.equals(orderNo) || MomConstant.ORDER_DZ_1975.equals(orderNo) || MomConstant.ORDER_DZ_1976.equals(orderNo)){
            throw new  CustomException(CustomExceptionType.AUTHEN_TICATIIN_FAILURE,CustomResponseCode.ERR11.getChinese());
        }
        try {
            MomOrder momOrder = momOrderDao.selectById(proTaskOrderId);
            if(momOrder == null){
                log.warn("[MomOrderServiceImpl]::[forwardOrder]::Mom订单信息不存在,Mom订单ID:{}",proTaskOrderId);
                return Result.error(CustomExceptionType.SYSTEM_ERROR,CustomResponseCode.ERR52);
            }
            if(StringUtils.isEmpty(momOrder.getJsonOriginalData())){
                log.warn("[MomOrderServiceImpl]::[forwardOrder]::Mom下发订单Json数据不存在,Mom订单ID:{}",proTaskOrderId);
                return Result.error(CustomExceptionType.Parameter_Exception,CustomResponseCode.ERR33);
            }
            if("dzics".equals(momOrder.getParamRsrv2())){
                log.warn("[MomOrderServiceImpl]::[forwardOrder]::订单转发，当前订单为异岛转发订单，不可再次转发,proTaskOrderId:{}",momOrder.getProTaskOrderId());
                return Result.error(CustomExceptionType.Parameter_Exception,CustomResponseCode.ERR92);
            }

            //获取当前需要转发工序信息的json数据
            List<MomOrderPath> paths = momOrderPathService.list(new QueryWrapper<MomOrderPath>().eq("mom_order_id", proTaskOrderId));
            if(CollectionUtils.isEmpty(paths)){
                log.warn("[MomOrderServiceImpl]::[forwardOrder]::Mom下发工序信息不存在,Mom订单ID:{}",proTaskOrderId);
                return Result.error(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.valueOf(Message.ERR_7));
            }

            //重定向，设置转发到对应类型岛
            String orderCode = "";
            if(MomConstant.ORDER_DZ_1972.equals(orderNo)){
                orderCode = "DZ-19731";
            }
            if(MomConstant.ORDER_DZ_1973.equals(orderNo)){
                orderCode = "DZ-19721";
            }
//            if(MomConstant.ORDER_DZ_1974.equals(orderNo)){
//                orderCode = "DZ-19751";
//            }
//            if(MomConstant.ORDER_DZ_1975.equals(orderNo)){
//                orderCode = "DZ-19741";
//            }

            Map<String, String> maps = mapConfig.getMaps();
            String ip = maps.get(orderCode);

            String url = "http://" + ip + ":12306/forward/data";

//            订单信息转发
            IssueOrderInformation<Task> task = JSONObject.parseObject(momOrder.getJsonOriginalData(), new TypeReference<IssueOrderInformation<Task>>() {});
            task.getTask().setParamRsrv2("dzics");
            ResponseEntity<ResultDto> r1 = restTemplate.postForEntity(url, JSON.toJSONString(task), ResultDto.class);
            if(!"0".equals(r1.getBody().getCode())){
                throw new CustomException(CustomExceptionType.SYSTEM_ERROR);
            }
            log.info("[MomOrderServiceImpl]::[forwardOrder]::订单信息转发,WipOrderNo:{},请求响应状态为:{}",task.getTask().getWipOrderNo(),r1.getBody().getCode());
            momOrder.setParamRsrv2("dzics");
            momOrderDao.updateById(momOrder);

//            工序信息转发
            for (MomOrderPath momOrderPath : paths) {
                IssueOrderInformation<OprSequence> task1 = JSONObject.parseObject(momOrderPath.getJsonOriginalData(), new TypeReference<IssueOrderInformation<OprSequence>>() {});
                ResponseEntity<ResultDto> r2 = restTemplate.postForEntity(url, JSON.toJSONString(task1), ResultDto.class);
                if(!"0".equals(r2.getBody().getCode())){
                    throw new CustomException(CustomExceptionType.SYSTEM_ERROR);
                }
                log.info("[MomOrderServiceImpl]::[forwardOrder]::订单工序转发,WipOrderNo:{},请求响应状态为:{}",task1.getTask().getWipOrderNo(),r2.getBody().getCode());
            }

        }catch(Throwable e){
            e.printStackTrace();
        }
        return Result.ok();
    }

    @Override
    public MomOrder getWorkingOrder(String orderId, String lineId, String workStation) {
        return baseMapper.getWorkingOrder(orderId, lineId, workStation);
    }

    public String vaildOrder(PutMomOrder putMomOrder){
        //获取当前订单下发的工序信息
        List<MomOrderPath> orderPaths = momOrderPathService.list(new QueryWrapper<MomOrderPath>().eq("mom_order_id", putMomOrder.getProTaskOrderId()).isNotNull("WorkStation"));
        if(CollectionUtils.isEmpty(orderPaths)){
            log.error("MomOrderServiceImpl [orderRecover] 恢复订单失败，订单ID：{},Mom没有下发工序报文信息",putMomOrder.getProTaskOrderId());
            throw new CustomException(CustomExceptionType.OK_NO_DATA,CustomResponseCode.ERR49.getChinese());
        }
        //代表当前订单下发包含的所有工位（只有当前这些工位可以生产作业）
        List<String> collect = orderPaths.stream().map(p -> p.getWorkStation()).collect(Collectors.toList());

        //查询当前的这些工位是否正在进行其他作业、暂停中
        if(MomProgressStatus.LOADING.equals(putMomOrder.getProgressStatus())){
            int count = momOrderPathDao.getIsRunning(putMomOrder.getLineId(),collect);
            if (count>0) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR51.getChinese());
            }
        }

        List<DzWorkStationManagement> managements = managementService.list(new QueryWrapper<DzWorkStationManagement>()
                        .eq("line_id", putMomOrder.getLineId()).isNotNull("dz_station_code"));
        if(CollectionUtils.isEmpty(managements)){
            log.error("MomOrderServiceImpl [orderRecover] 恢复订单失败，基础工位配置为null，检查是否配置");
            throw new CustomException(CustomExceptionType.OK_NO_DATA, CustomResponseCode.ERR49.getChinese());
        }

        String type = null;
        for (DzWorkStationManagement management : managements) {
            for (String s : collect) {
                if(management.getDzStationCode().equals(s)){
//                    如果含有关键字“1”或者“A“ 则代表是第一台加工设备
                    if(management.getStationName().contains("1")||management.getStationName().contains("A")){
                        type = "A";
                    }else{
                        type = "B";
                    }
                }
            }
        }
        if(type==null){
            log.error("MomOrderServiceImpl [orderRecover] Mom下发的工位信息在系统中不存在，工位编号：{}",collect);
            throw new CustomException(CustomExceptionType.OK_NO_DATA, CustomResponseCode.ERR49.getChinese());
        }
        return type;
    }


    /**
     * 更新订单抓取数量 如果是抓取完成，则开始新的订单
     * @param orderNo
     * @param lineNo
     * @param monOrderSel
     * @param sum
     */
    @Override
    public synchronized void updateOrderStateSum(String orderNo, String lineNo, MomOrder monOrderSel, Integer sum) {
        if (sum != null) {
            MomOrder momOder = orderService.upDateQuantity(monOrderSel, monOrderSel.getWiporderno(), sum.toString());
            log.info("MomOrderServiceImpl [updateOrderStateSum] 更新订单:{} 产线: {} 更新数量：{}", monOrderSel.getWiporderno(), orderNo,monOrderSel.getOrderOutput());
            //抛光需要自动切单功能
            if (momOder.getProgressStatus().equals(MomProgressStatus.SUCCESS) && MomConstant.ORDER_DZ_1976.equals(orderNo)) {
//              订单状态已完成，查看是否有可以开始 和 本次订单相同的物料订单，如果有则自动开始。
               this.startMomOrder(momOder, orderNo, lineNo);
            }
        }
    }

    /**
     * 跟新订单数量，如果生产数量大于 等于 计划生产数量 则修改订单状态已完成
     *
     * @param start：MomOrder信息
     * @param wipOrderNo：Mom订单号
     * @param output:数量
     * @return MomOrder
     */
    @Override
    public MomOrder upDateQuantity(MomOrder start, String wipOrderNo, String output) {
        boolean update = false;
        int orderOutput = 0;

        MomOrder monUpdate = new MomOrder();
        int quantity = start.getQuantity().intValue();
        orderOutput = Integer.valueOf(output).intValue();
        monUpdate.setWiporderno(wipOrderNo);
        monUpdate.setOrderOutput(orderOutput);

        if (orderOutput >= quantity) {
            monUpdate.setOrderOldState(start.getProgressStatus());
            monUpdate.setProgressStatus(MomProgressStatus.SUCCESS);
            monUpdate.setRealityCompleteDate(new Date());
        }
        QueryWrapper<MomOrder> wp = new QueryWrapper<>();
        wp.eq("WipOrderNo", wipOrderNo);
        wp.eq("order_id", start.getOrderId());
        wp.eq("line_id", start.getLineId());
        update = update(monUpdate, wp);

        if (monUpdate.getProgressStatus() != null) {
            start.setProgressStatus(monUpdate.getProgressStatus());
            start.setRealityCompleteDate(monUpdate.getRealityCompleteDate());
        }
        if (!update) {
            log.warn("MomOrderServiceImpl [upDateQuantity] 跟新订单: {} 数量: {} 失败", wipOrderNo, output);
        } else {
            start.setOrderOutput(orderOutput);
        }
        return start;
    }

    @Override
    public void startMomOrder(MomOrder upOlder, String orderNo, String lineNo) {
        try {
            log.info("[自动切单] 1.执行自动开始订单流程 开始：orderNo:{},lineNo:{}", orderNo, lineNo);
            log.info("[自动切单] 2.执行自动开始订单流程:获取工控机IP地址");
            Map<String, String> mapIps = mapConfig.getMaps();
            String plcIp = mapIps.get(orderNo + lineNo);
            if (StringUtils.isEmpty(plcIp)) {
                log.warn("[自动切单] 下发订单指令发送到UDP订单: {} ,产线: {} 配置的IP不存在mapIps: {} ", lineNo, orderNo, mapIps);
                return;
            }
            String lineId = upOlder.getLineId(); // 产线ID
            String orderId = upOlder.getOrderId();//订单ID
            //查询进行中的订单，或者订单暂停 或者 在操作进行中的订单
            log.info("[自动切单] 3.执行自动开始订单流程:检查是否存在操作中的订单");
            QueryWrapper<MomOrder> wp = new QueryWrapper<>();
            wp.eq("line_id", lineId);
            wp.and(wapper -> wapper.eq("ProgressStatus", MomProgressStatus.LOADING).or().eq("ProgressStatus", MomProgressStatus.STOP).or().eq("order_operation_result", 1));
            List<MomOrder> list = momOrderDao.selectList(wp);
            if (CollectionUtils.isNotEmpty(list)) {
                log.error("[自动切单] 产线中已存在执行中的订单,订单结束后可开始,自动校验订单自动开始执行取消  list: {}", list);
                return;
            }
//            获取可以自动开始的订单
            log.info("[自动切单] 4.执行自动开始订单流程：获取可以自动开始的订单");
            MomOrder startOrder = orderService.getOrderCallMaterialStatus(orderId, lineId, upOlder.getProductNo(), MomProgressStatus.DOWN);
            if (startOrder == null) {
                log.warn("[自动切单] 没有可以自动开始执行的订单 订单编号:{},产线编号:{},物料编码：{},订单状态:{}", orderNo, lineNo,upOlder.getProductNo(),MomProgressStatus.DOWN);
                return;
            }
            String wipOrder = startOrder.getWiporderno();
            log.info("[自动切单] 5.执行自动开始订单流程：获取相同产品物料信息：{} ,查询出可开始的Mom订单：{}",upOlder.getProductNo(),wipOrder);
            DzProduct dzProduct = dzProductService.getById(startOrder.getProductId());
            if (dzProduct == null) {
                log.error("[自动切单] 订单数据错误,根据订单中产品ID,无法获取到产品 dzProduct：{},请重新新建订单后重试：{}", dzProduct, JSONObject.toJSONString(startOrder));
                return;
            }
            MomOrder newOrder = new MomOrder();
            try {
                BeanUtils.copyProperties(startOrder,newOrder);
            }catch(Exception e){
                e.printStackTrace();
            }
            newOrder.setProgressStatus(String.valueOf(120));
            newOrder.setOrderOperationResult(1);
            orderService.updateById(newOrder);
            orderService.orderStart(startOrder,newOrder,null,null);
            log.info("[自动切单] 6.执行自动开始订单流程:通知三维下发订单开始指令");
            log.info("[自动切单] 7.执行自动开始订单流程:结束：orderNo:{},lineNo:{},wipOrderNo：{}", orderNo, lineNo, wipOrder);
        } catch (Throwable throwable) {
            log.error("[自动切单] 执行自动开始订单异常:{}", throwable.getMessage(), throwable);
        }
    }

    @Override
    public MomOrder getOrderCallMaterialStatus(String orderId, String lineId, String productNo, String down) {
        return momOrderDao.getOrderCallMaterialStatus(orderId, lineId, productNo, down);
    }

}
