package com.dzics.data.appoint.changsha.mom.controller;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dzics.data.appoint.changsha.mom.model.dao.GetWorkingDetailsDo;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.appoint.changsha.mom.model.vo.GetWorkingDetailsVo;
import com.dzics.data.appoint.changsha.mom.mq.RabbitMQService;
import com.dzics.data.appoint.changsha.mom.service.CachingApi;
import com.dzics.data.appoint.changsha.mom.service.MomOrderService;
import com.dzics.data.appoint.changsha.mom.util.RedisUniqueID;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.write.CustomCellWriteHandler;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.entity.DzProductionLine;
import com.dzics.data.pub.model.entity.DzWorkStationManagement;
import com.dzics.data.pub.model.vo.StationModelAll;
import com.dzics.data.pub.service.DzWorkStationManagementService;
import com.dzics.data.pub.service.kanban.TaktTimeService;
import com.dzics.data.wrp.model.entity.DzWorkingFlow;
import com.dzics.data.wrp.service.DzWorkingFlowService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Auther:zzz
 * @Date:2022/7/13 - 07 - 13 - 16:45
 * @Description:com.dzics.data.appoint.changsha.mom.controller.productiontask
 */
@Slf4j
@Api(tags = {"生产任务报工"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/upWork")
@Controller
public class UpWorkController {

    @Autowired
    private DzWorkingFlowService dzWorkingFlowService;
    @Autowired
    private CachingApi cachingApi;
    @Autowired
    private DzWorkStationManagementService dzWorkStationManagementService;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private RabbitMQService rabbitMQService;
    @Autowired
    private RedisUniqueID redisUniqueID;
    @Value("${order.code}")
    private String orderCode;

    @ApiOperation(value = "生产任务报工详情列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/query")
    public Result<List<GetWorkingDetailsDo>> getWorkingDetails(GetWorkingDetailsVo getWorkingDetailsVo) {
        PageHelper.offsetPage(getWorkingDetailsVo.getPage(), getWorkingDetailsVo.getLimit());
        LambdaQueryWrapper<DzWorkingFlow> query = Wrappers.<DzWorkingFlow>lambdaQuery();
        if (StringUtils.hasText(getWorkingDetailsVo.getLineId())) {
            query.eq(DzWorkingFlow::getLineId, getWorkingDetailsVo.getLineId());
        }
        if (StringUtils.hasText(getWorkingDetailsVo.getStationId())) {
            query.eq(DzWorkingFlow::getStationId, getWorkingDetailsVo.getStationId());
        }
        if (StringUtils.hasText(getWorkingDetailsVo.getQrCode())) {
            query.eq(DzWorkingFlow::getQrCode, getWorkingDetailsVo.getQrCode());
        }
        if (StringUtils.hasText(getWorkingDetailsVo.getWorkpieceCode())) {
            query.eq(DzWorkingFlow::getWorkpieceCode, getWorkingDetailsVo.getWorkpieceCode());
        }
        if (StringUtils.hasText(getWorkingDetailsVo.getStartTime())) {
            query.ge(DzWorkingFlow::getStartTime, getWorkingDetailsVo.getStartTime());
        }
        if (StringUtils.hasText(getWorkingDetailsVo.getEndTime())) {
            query.le(DzWorkingFlow::getCompleteTime, getWorkingDetailsVo.getEndTime());
        }
        List<DzWorkingFlow> list = dzWorkingFlowService.list(query);
        PageInfo<DzWorkingFlow> info = new PageInfo<>(list);
        DzProductionLine productionLine = cachingApi.getOrderIdAndLineId();
        List<GetWorkingDetailsDo> resultList = new ArrayList<>();
        list.forEach(m -> {
            GetWorkingDetailsDo getWorkingDetailsDo = new GetWorkingDetailsDo();
            getWorkingDetailsDo.setLineName(productionLine.getLineName());
            getWorkingDetailsDo.setQrCode(m.getQrCode());
            getWorkingDetailsDo.setWorkpieceCode(m.getWorkpieceCode());
            getWorkingDetailsDo.setStartTime(m.getStartTime());
            getWorkingDetailsDo.setCompleteTime(m.getCompleteTime());
            getWorkingDetailsDo.setWorkDate(String.valueOf(m.getWorkDate()));
            getWorkingDetailsDo.setLineId(m.getLineId());
            getWorkingDetailsDo.setOrderId(m.getOrderId());
            getWorkingDetailsDo.setStationId(m.getStationId());
            //工位名称
            DzWorkStationManagement dzWorkStationManagement = dzWorkStationManagementService.getById(m.getStationId());
            getWorkingDetailsDo.setStationName(dzWorkStationManagement.getStationName());
            //节拍
            Date startTime = m.getStartTime();
            Date completeTime = m.getCompleteTime();
            if (startTime != null && completeTime != null) {
                long stLong = startTime.getTime();
                long comTime = completeTime.getTime();
                long dev = (comTime - stLong) / 1000;
                getWorkingDetailsDo.setTaktTime(getGapTime(Long.valueOf(dev).intValue()));
            }
            resultList.add(getWorkingDetailsDo);
        });
        return Result.ok(resultList, info.getTotal());
    }

    public static String getGapTime(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        String timeString = minutes + " 分 " + seconds + " 秒";
        if (time < 0) {
            timeString = timeString + " 异常";
        }
        return timeString;
    }

    @ApiOperation(value = "生产任务报工详情列表导出", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/upWork")
    public void getWorkingDetails(GetWorkingDetailsVo getWorkingDetailsVo, HttpServletResponse response) throws IOException {
        String fileNameBase = "生产任务报工详情";
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            getWorkingDetailsVo.setPage(-1);
            Result<List<GetWorkingDetailsDo>> listResult = getWorkingDetails(getWorkingDetailsVo);
            List<GetWorkingDetailsDo> data = listResult.getData();
            EasyExcel.write(response.getOutputStream(), GetWorkingDetailsDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        } catch (Throwable throwable) {
            log.error("导出{}异常：{}", fileNameBase, throwable.getMessage(), throwable);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }

    @ApiOperation(value = "新增报工", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/add")
    public Result<String> add(@RequestBody Map<String, String> m) {
        String deviceCode = m.get("deviceCode");
        String type = m.get("type");
        String qrCode = m.get("qrCode");

        DzProductionLine orderIdAndLineId = cachingApi.getOrderIdAndLineId();
        Map<String, String> map = new HashMap<>();
        map.put("MessageId", redisUniqueID.getUUID());
        map.put("QueueName", "dzics-dev-gather-v1-product-position");
        map.put("ClientId", "DZROBOT");
        map.put("OrderCode", orderCode);
        map.put("DeviceType", "6");
        map.put("LineNo", orderIdAndLineId.getLineNo());
        map.put("DeviceCode", deviceCode);
        map.put("Message", "A815|[" + type + "," + qrCode + "]");
        map.put("Timestamp", LocalDateTimeUtil.formatNormal(LocalDateTime.now()));
        rabbitMQService.sendMsg("dzics-dev-gather-v1-exchange-product-position",
                "dzics-dev-gather-v1-routing-product-position",
                JSONUtil.toJsonStr(map));
        return Result.ok();
    }

    @ApiOperation(value = "二维码", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/qrCode")
    public Result<String> qrCode() {
        MomOrder one = momOrderService.getOne(Wrappers.<MomOrder>lambdaQuery()
                .eq(MomOrder::getProgressStatus, 120));
        if (ObjectUtils.isEmpty(one)) {
            return new Result<>(400, "进行中的唯一订单异常");
        }
        String qrCode = one.getProductNo() + "-" + IdWorker.getIdStr();
        return Result.ok(qrCode);
    }
}
