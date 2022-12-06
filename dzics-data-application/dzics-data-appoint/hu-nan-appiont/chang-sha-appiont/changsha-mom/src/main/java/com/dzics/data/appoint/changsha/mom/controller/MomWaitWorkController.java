package com.dzics.data.appoint.changsha.mom.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dzics.data.appoint.changsha.mom.db.dao.MomWaitWorkReportDao;
import com.dzics.data.appoint.changsha.mom.model.constant.MomConstant;
import com.dzics.data.appoint.changsha.mom.model.entity.MomWaitWorkReport;
import com.dzics.data.appoint.changsha.mom.model.vo.GetWorkingDetailsVo;
import com.dzics.data.appoint.changsha.mom.service.MomWaitWorkReportService;
import com.dzics.data.appoint.changsha.mom.service.impl.WorkReportingCjServiceImpl;
import com.dzics.data.appoint.changsha.mom.service.impl.WorkReportingJjServiceImpl;
import com.dzics.data.appoint.changsha.mom.service.impl.WorkReportingPgServiceImpl;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.dto.position.SendPosition;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.entity.DzWorkStationManagement;
import com.dzics.data.pub.service.DzWorkStationManagementService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @Auther:zzz
 * @Date:2022/7/14 - 07 - 14 - 9:42
 * @Description:com.dzics.data.appoint.changsha.mom.controller
 */
@Slf4j
@Api(tags = {"报工处理"})
@RestController
@RequestMapping("/api/report")
@Controller
public class MomWaitWorkController {
    @Autowired
    private MomWaitWorkReportDao reportDao;
    @Autowired
    private WorkReportingCjServiceImpl cjService;
    @Autowired
    private WorkReportingJjServiceImpl jjService;
    @Autowired
    private WorkReportingPgServiceImpl pgService;
    @Autowired
    private MomWaitWorkReportService momWaitWorkReportService;

    @ApiOperation(value = "MOM报工响应列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/query")
    public Result<List<MomWaitWorkReport>> getWorkingDetails(GetWorkingDetailsVo getWorkingDetailsVo) {
        PageHelper.offsetPage(getWorkingDetailsVo.getPage(), getWorkingDetailsVo.getLimit());
        LambdaQueryWrapper<MomWaitWorkReport> query = Wrappers.<MomWaitWorkReport>lambdaQuery();
        if (StringUtils.hasText(getWorkingDetailsVo.getLineId())) {
            query.eq(MomWaitWorkReport::getLineId, getWorkingDetailsVo.getLineId());
        }
        List<MomWaitWorkReport> list = momWaitWorkReportService.list(query);
        PageInfo<MomWaitWorkReport> info = new PageInfo<>(list);
        return Result.ok(list, info.getTotal());
    }

    @ApiOperation(value = "重发")
    @PutMapping("/repeat")
    public Result<String> repeat(@RequestBody MomWaitWorkReport momWaitWorkReport) {
        if (!StringUtils.hasLength(momWaitWorkReport.getId())) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_5);
        }
        try {
            MomWaitWorkReport report = momWaitWorkReportService.getById(momWaitWorkReport.getId());
            SendPosition sendPosition = new SendPosition();
            BeanUtils.copyProperties(report, sendPosition);
            String orderNo = report.getOrderNo();
            Boolean s = null;
            sendPosition.setProductionTime(new Date());
//            粗加工订单号
            if (MomConstant.ORDER_DZ_1972.equals(orderNo) || MomConstant.ORDER_DZ_1973.equals(orderNo)) {
                s = cjService.sendWorkReportingData(sendPosition, report.getId());
            }
//            精加工订单号
            if (MomConstant.ORDER_DZ_1974.equals(orderNo) || MomConstant.ORDER_DZ_1975.equals(orderNo)) {
                s = jjService.sendWorkReportingData(sendPosition, report.getId());
            }
//            抛光订单号
            if (MomConstant.ORDER_DZ_1976.equals(orderNo)) {
                s = pgService.sendWorkReportingData(sendPosition, report.getId());
            }

            if (Boolean.TRUE.equals(s)) {
                reportDao.deleteById(report.getId());
            }
        } catch (Throwable throwable) {
            log.error("报工失败", throwable);
        }
        return Result.ok();
    }

    @ApiOperation(value = "根据id查询表格信息")
    @GetMapping("/{id}")
    public Result<MomWaitWorkReport> getById(@PathVariable String id) {
        MomWaitWorkReport momWaitWorkReport = momWaitWorkReportService.getById(id);
        return Result.ok(momWaitWorkReport);
    }

    @ApiOperation(value = "编辑")
    @PutMapping
    public Result<String> upDataQrCode(@RequestBody MomWaitWorkReport momWaitWorkReport) {
        momWaitWorkReportService.updateById(momWaitWorkReport);
        return Result.ok("修改qr_code成功");
    }
}
