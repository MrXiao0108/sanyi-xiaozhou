package com.dzics.data.appoint.changsha.mom.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.data.appoint.changsha.mom.db.dao.DncProgramDao;
import com.dzics.data.appoint.changsha.mom.enums.DNCProgramEnum;
import com.dzics.data.appoint.changsha.mom.model.dto.dnc.DNCDto;
import com.dzics.data.appoint.changsha.mom.model.entity.DncProgram;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrderPath;
import com.dzics.data.appoint.changsha.mom.model.vo.DNCProgramVo;
import com.dzics.data.appoint.changsha.mom.model.vo.DncLogVo;
import com.dzics.data.appoint.changsha.mom.model.vo.ResultVo;
import com.dzics.data.appoint.changsha.mom.service.CachingApi;
import com.dzics.data.appoint.changsha.mom.service.DncProgramService;
import com.dzics.data.appoint.changsha.mom.service.MomOrderPathService;
import com.dzics.data.appoint.changsha.mom.service.MomOrderService;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.service.DzEquipmentService;
import com.dzics.data.pub.service.DzProductionLineService;
import com.dzics.data.redis.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * dnc 换型信息 服务实现类
 * </p>
 *
 * @author van
 * @since 2022-06-28
 */
@Slf4j
@Service
public class DncProgramServiceImpl extends ServiceImpl<DncProgramDao, DncProgram> implements DncProgramService {

    @Value("${dnc.secret-key}")
    private String DNCSecretKey;
    @Autowired
    private DncProgramDao dncProgramDao;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisUtil<String> redisUtil;
    @Autowired
    private DzProductionLineService dzProductionLineService;
    @Autowired
    private MomOrderPathService momOrderPathService;
    @Autowired
    private DzEquipmentService dzEquipmentService;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private CachingApi cachingApi;

    @Override
    public DncProgram currentProgram() {
        return dncProgramDao.successLately();
    }

    @Override
    public boolean changeProgram(MomOrder momOrder, boolean isRepeat, List<DzEquipment>list) {
        boolean obj = true;
        try {
            //获取Mom订单工序信息
            MomOrderPath momOrderPath = momOrderPathService.getproTaskOrderId(momOrder.getProTaskOrderId());
            if (ObjectUtils.isEmpty(momOrderPath)) {
                return false;
            }
            //封装设备的DNC信息
            List<DncProgram> dncProgramList = this.wrapper(momOrder, momOrderPath, list);
            //循环集合 请求DNC
            for (DncProgram dncProgram : dncProgramList) {
                obj = this.sendProgram(dncProgram);
                //中途一旦出现失败，终止订单下发程序
                if(!obj){
                    return false;
                }
            }
        }catch(Throwable e){
            obj = false;
            e.printStackTrace();
        }finally {
            return obj;
        }
    }

    @Override
    public Result<String> cancel(String id) {
        DncProgram dncProgram = this.getById(id);
        if (ObjectUtils.isEmpty(dncProgram)) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_6);
        }
        dncProgram.setState(DNCProgramEnum.MANUAL.val());
        this.updateById(dncProgram);
        return Result.ok();
    }

    @Override
    public DncProgram currentProgram(String dzOrder, String equipmentNo) {
        DzEquipment one = dzEquipmentService.getOne(Wrappers.<DzEquipment>lambdaQuery()
                .eq(DzEquipment::getOrderNo, dzOrder)
                .eq(DzEquipment::getEquipmentNo, equipmentNo));
        DncProgram dncProgram = this.getOne(Wrappers.<DncProgram>lambdaQuery()
                .eq(DncProgram::getMachineCode, one.getEquipmentCode())
                .in(DncProgram::getState, DNCProgramEnum.CHANGE_SUCCESS.val(), DNCProgramEnum.MANUAL.val()));
        return dncProgram;
    }

    @Override
    public DncProgram dncProgramHandel(Map<String, String> map) {
        String orderNo = "DZ-" + map.get("orderNo");
        String equipmentNo = map.get("equipmentNo");
        return this.currentProgram(orderNo, equipmentNo);
    }

    @Override
    public boolean initProgram(MomOrder momOrder, List<String> equipmentIds) {
        //定义最终需要DNC请求的设备集合
        List<DzEquipment>equipments=new ArrayList<>();
        //获取设备表数据
        LambdaQueryWrapper<DzEquipment> eq = Wrappers.<DzEquipment>lambdaQuery()
                .eq(DzEquipment::getLineId, momOrder.getLineId());
        if(!CollectionUtils.isEmpty(equipmentIds)){
            eq.in(DzEquipment::getId, equipmentIds);
        }
        List<DzEquipment> list = dzEquipmentService.list(eq);
        //获取设备DNC最新一条的请求记录，对比与当前在做订单产品是否一致,是跳过，否添加
        for (DzEquipment equipment : list) {
            DncProgram oldProgram = dncProgramDao.getOneNewDate(momOrder.getLineId(), equipment.getEquipmentNo());
            if(!oldProgram.getMaterialCode().equals(momOrder.getProductNo()) || ObjectUtils.isEmpty(oldProgram)){
                equipments.add(equipment);
            }
        }
        return this.changeProgram(momOrder, false, equipments);
    }

    @Override
    public Result<String> manualIntervention(DNCProgramVo dncProgramVo) {
        DncProgram dncProgram = this.getById(dncProgramVo.getId());
        if (ObjectUtils.isEmpty(dncProgram)) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_6);
        }
        dncProgram.setRunProgramname(dncProgramVo.getRunProgramname());
        dncProgram.setState(DNCProgramEnum.MANUAL.val());
        this.updateById(dncProgram);
        return Result.ok(Message.OK_5);
    }

    @Override
    public Result<List<DNCProgramVo>> page(DNCProgramVo dncProgramVo) {
        PageHelper.offsetPage(dncProgramVo.getPage(), dncProgramVo.getLimit());
        LambdaQueryWrapper<DncProgram> query = Wrappers.<DncProgram>lambdaQuery();
        if (StringUtils.hasText(dncProgramVo.getWipOrderNo())) {
            query.eq(DncProgram::getWipOrderNo, dncProgramVo.getWipOrderNo());
        }
        if (StringUtils.hasText(dncProgramVo.getMaterialCode())) {
            query.eq(DncProgram::getMaterialCode, dncProgramVo.getMaterialCode());
        }
        if (StringUtils.hasText(dncProgramVo.getMachineCode())) {
            query.eq(DncProgram::getMachineCode, dncProgramVo.getMachineCode());
        }
        if (StringUtils.hasText(dncProgramVo.getState())) {
            query.eq(DncProgram::getState, dncProgramVo.getState());
        }
        List<DncProgram> list = this.list(query);
        if (CollectionUtils.isEmpty(list)) {
            return Result.ok(new ArrayList<>(), 0L);
        }

        List<DzProductionLine> lineList = dzProductionLineService.list();
        Map<String, String> lineMap = lineList
                .stream()
                .collect(Collectors.toMap(DzProductionLine::getId, DzProductionLine::getLineName));

        List<DNCProgramVo> voList = new ArrayList<>();
        list.forEach(d -> {
            DNCProgramVo vo = new DNCProgramVo();
            vo.setId(d.getId());
            vo.setLineId(d.getLineId());
            vo.setLineName(lineMap.get(d.getLineId()));
            vo.setWipOrderNo(d.getWipOrderNo());
            vo.setMaterialCode(d.getMaterialCode());
            vo.setRoutingCode(d.getRoutingCode());
            vo.setMachineCode(d.getMachineCode());
            vo.setWorkingProcedure(d.getWorkingProcedure());
            vo.setSequencenumber(d.getSequencenumber());
            vo.setRunProgramname(d.getRunProgramname());
            vo.setState(d.getState());
            vo.setDncResponse(d.getDncResponse());
            vo.setFeedbackDetail(d.getFeedbackDetail());
            voList.add(vo);
        });
        return Result.ok(voList, (long) list.size());
    }

    @Override
    public Result<String> repeat(String id) {
        DncProgram dncProgram = this.getById(id);
        if (ObjectUtils.isEmpty(dncProgram)) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_6);
        }
        log.info("DncProgramServiceImpl [repeat] 重发程序dncProgram：" + JSON.toJSONString(dncProgram));
        dncProgram.setState(DNCProgramEnum.REQUEST_SUCCESS.val());
        boolean b = this.sendProgram(dncProgram);
        if(b){
            return Result.ok();
        }
        return new Result(CustomExceptionType.SYSTEM_ERROR,"DNC程序请求失败");
    }

    @Override
    public void saveProgram(DncProgram dncProgram) {
        String key = "dnc:sava-program:" + dncProgram.getMachineCode();
        if (redisUtil.hasKey(key)) {
            return;
        }
        redisUtil.set(key, " ", 60);
        DncProgram one = this.getOne(Wrappers.<DncProgram>lambdaQuery()
                .eq(DncProgram::getMachineCode, dncProgram.getMachineCode()));
        if (!ObjectUtils.isEmpty(one)) {
            this.removeById(one.getId());
        }
        log.info(" add ---> " + JSON.toJSONString(dncProgram));
        this.save(dncProgram);
        /*else {
            one.setProTaskOrderId(dncProgram.getProTaskOrderId());
            one.setWipOrderNo(dncProgram.getWipOrderNo());
            one.setTaskNumber(dncProgram.getTaskNumber());
            one.setMaterialCode(dncProgram.getMaterialCode());
            one.setRoutingCode(dncProgram.getRoutingCode());
            one.setSequencenumber(dncProgram.getSequencenumber());
            one.setWorkingProcedure(dncProgram.getWorkingProcedure());
            one.setWorkCenter(dncProgram.getWorkCenter());
            one.setMachineCode(dncProgram.getMachineCode());
            one.setProgramname(dncProgram.getProgramname());
            one.setTokenstr(dncProgram.getTokenstr());
            one.setState(dncProgram.getState());
            one.setDncRequest(dncProgram.getDncRequest());
            one.setDncResponse(dncProgram.getDncResponse());
            log.info(" upd ---> " + JSON.toJSONString(dncProgram));
            this.updateById(one);
        }*/
        redisUtil.del(key);
    }

    @Override
    public boolean sendProgram(DncProgram dncProgram) {
        DNCDto.DownloadProgram downloadProgram = DNCDto.downloadProgram(dncProgram);
        Integer code = 200;
        try {
            log.info("DncProgramServiceImpl [changeProgram] 调用DNC下载程序接口 请求参数{}", JSON.toJSONString(downloadProgram));
            dncProgram.setDncRequest(JSON.toJSONString(downloadProgram));
            ResponseEntity<ResultVo> result = restTemplate.postForEntity("http://10.0.91.65:9009/downloadprogram", downloadProgram, ResultVo.class);
            code = Integer.valueOf(result.getBody().getCode());
            log.info("DncProgramServiceImpl [changeProgram] 调用DNC下载程序接口 响应结果{}", JSON.toJSONString(result));
            dncProgram.setDncResponse(JSON.toJSONString(result));
        }
        catch(ResourceAccessException resourceAccessException){
            log.error("DncProgramServiceImpl [changeProgram] 调用DNC下载程序接口 连接超时：" + resourceAccessException.getMessage());
            sendProgram(dncProgram);
        }
        catch (Exception e) {
            log.error("DncProgramServiceImpl [changeProgram] 调用DNC下载程序接口异常：" + e.getMessage());
            dncProgram.setDncResponse("请求异常" + e.getMessage());
            dncProgram.setState(DNCProgramEnum.REQUEST_FAIL.val());
            code = -1;
        }
        this.saveProgram(dncProgram);
        if(code!=200){
            return false;
        }
        return true;
    }

    @Override
    public List<DncProgram> getDncLog(DncLogVo dncLogVo) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(StringUtils.isEmpty(dncLogVo.getStartTime()) && StringUtils.isEmpty(dncLogVo.getEndTime())){
            dncLogVo.setEndTime(simpleDateFormat.format(new Date()));
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(simpleDateFormat.parse(dncLogVo.getEndTime()));
            calendar.add(Calendar.DATE,-7);
            dncLogVo.setStartTime(simpleDateFormat.format(calendar.getTime()));
        }
        if(dncLogVo.getPage()!=-1){
            PageHelper.startPage(dncLogVo.getPage(),dncLogVo.getLimit());
        }
        List<DncProgram> dncLog = dncProgramDao.getDncLog(dncLogVo.getEquipmentCode(), dncLogVo.getProgramName(), dncLogVo.getDncResponse(), dncLogVo.getState(), dncLogVo.getStartTime(), dncLogVo.getEndTime(),dncLogVo.getField(),dncLogVo.getType());
        PageInfo pageInfo = new PageInfo<>(dncLog);
        return pageInfo.getList();
    }

    /**
     * 装配换型实体
     *
     * @param momOrder:      MOM订单实体
     * @param momOrderPath:  MOM订单工序组件
     * @param equipmentList: 设备列表
     * @return 换型实体
     * @author van
     * @date 2022/6/28
     */
    private List<DncProgram> wrapper(MomOrder momOrder, MomOrderPath momOrderPath, List<DzEquipment> equipmentList) {
        List<DncProgram> list = new ArrayList<>();
        equipmentList.forEach(equipment -> {
            DncProgram dncProgram = new DncProgram();
            dncProgram.setLineId(momOrder.getLineId());
            dncProgram.setProTaskOrderId(momOrder.getProTaskOrderId());
            dncProgram.setWipOrderNo(momOrder.getWiporderno());
            dncProgram.setTaskNumber(momOrder.getTaskid());
            dncProgram.setMaterialCode(momOrder.getProductNo());
            dncProgram.setRoutingCode(momOrder.getParamRsrv1());
            dncProgram.setSequencenumber(momOrderPath.getSequenceNo());
            dncProgram.setWorkingProcedure(momOrderPath.getOprSequenceNo());
            dncProgram.setWorkCenter(momOrder.getWorkCenter());
            dncProgram.setMachineCode(equipment.getEquipmentCode());
            dncProgram.setTokenstr(DNCSecretKey);
            dncProgram.setDncResponse(null);
            dncProgram.setProgramname(null);
            dncProgram.setState(DNCProgramEnum.REQUEST_SUCCESS.val());
            list.add(dncProgram);
        });
        return list;
    }
}
