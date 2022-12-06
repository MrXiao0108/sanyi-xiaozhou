package com.dzics.data.business.controller.alarm;


import com.dzics.data.business.service.AlarmService;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.AddDeviceAlarmConfig;
import com.dzics.data.pub.model.dto.GetDeivceAlarmConfig;
import com.dzics.data.pub.model.entity.DzDeviceAlarmConfig;
import com.dzics.data.pub.service.DzDeviceAlarmConfigService;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 告警字典配置接口
 *
 * @author ZhangChengJun
 * Date 2021/6/21.
 * @since
 */
@Api(tags = {"设备告警配置"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/alarm/config/device")
public class DeviceAlarmConfigController {

    @Autowired
    private DzDeviceAlarmConfigService deviceAlarmConfig;
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private DzicsUserService userService;

    @OperLog(operModul = "设备告警配置", operType = OperType.ADD, operDesc = "新增", operatorType = "后台")
    @ApiOperation(value = "新增", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PostMapping
    public Result addGiveAlarmConfig(
            @Valid @RequestBody AddDeviceAlarmConfig alarmConfig,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return alarmService.addGiveAlarmConfig(alarmConfig, sub);
    }

    @OperLog(operModul = "设备告警配置", operType = OperType.UPDATE, operDesc = "编辑", operatorType = "后台")
    @ApiOperation(value = "编辑", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PutMapping
    public Result putGiveAlarmConfig(
            @Valid @RequestBody AddDeviceAlarmConfig alarmConfig,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return alarmService.putGiveAlarmConfig(alarmConfig, sub);
    }

    @ApiOperation(value = "查询", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping
    public Result<List<DzDeviceAlarmConfig>> getGiveAlarmConfig(
            GetDeivceAlarmConfig alarmConfig,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        SysUser byUserName = userService.getByUserName(sub);
        String useOrgCode = byUserName.getUseOrgCode();
        return deviceAlarmConfig.getGiveAlarmConfig(alarmConfig, useOrgCode);
    }



    @OperLog(operModul = "设备告警配置", operType = OperType.DEL, operDesc = "删除", operatorType = "后台")
    @ApiOperation(value = "删除", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @DeleteMapping("/{alarmConfigId}")
    public Result delGiveAlarmConfig(
            @PathVariable("alarmConfigId") @ApiParam(value = "唯一标识", required = true) String alarmConfigId,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return deviceAlarmConfig.delGiveAlarmConfig(alarmConfigId, sub);
    }


}
