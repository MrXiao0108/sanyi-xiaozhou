package com.dzics.data.business.controller.maintain;

import com.dzics.data.business.service.MainTainService;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.vo.BaseTimeLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.maintain.model.dto.*;
import com.dzics.data.maintain.model.vo.MaintainDevice;
import com.dzics.data.maintain.model.vo.MaintainRecord;
import com.dzics.data.maintain.model.vo.MaintainRecordDetails;
import com.dzics.data.maintain.service.DzMaintainDeviceService;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/9/29.
 * @since 设备保养相关api
 */

@SuppressWarnings("ALL")
@Api(tags = {"保养记录"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/maintain/record")
@Controller
public class DeviceMaintainController {
    @Autowired
    private DzMaintainDeviceService busMaintainDeviceService;
    @Autowired
    private MainTainService mainTainService;
    @Autowired
    private DzicsUserService userService;

    @ApiOperation(value = "保养设备列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 135)
    @GetMapping("/device")
    public Result<List<MaintainDevice>> getMaintainList(BaseTimeLimit pageLimit, MaintainDeviceParms parmsReq,
                                                        @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        SysUser byUserName = userService.getByUserName(sub);
        String useOrgCode = byUserName.getUseOrgCode();
        return busMaintainDeviceService.getMaintainList(sub, pageLimit, parmsReq,useOrgCode);
    }


    @OperLog(operModul = "保养记录", operType = OperType.ADD, operDesc = "新增保养设备", operatorType = "后台")
    @ApiOperation(value = "新增保养设备", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 135)
    @PostMapping("/device")
    public Result addMaintainDevice(@RequestBody @Valid AddMaintainDevice parmsReq,
                                    @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return mainTainService.addMaintainDevice(sub, parmsReq);
    }

    @OperLog(operModul = "保养记录", operType = OperType.UPDATE, operDesc = "编辑保养设备", operatorType = "后台")
    @ApiOperation(value = "编辑保养设备", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 135)
    @PutMapping("/device")
    public Result updateMaintainDevice(@RequestBody @Valid AddMaintainDevice parmsReq,
                                       @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return mainTainService.updateMaintainDevice(sub, parmsReq);
    }

    @ApiOperation(value = "查看保养记录", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 135)
    @GetMapping
    public Result<MaintainRecord> getMaintainRecord(BaseTimeLimit pageLimit, MaintainRecordParms parmsReq,
                                                    @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busMaintainDeviceService.getMaintainRecord(sub, pageLimit, parmsReq);
    }

    @ApiOperation(value = "查看保养记录详情", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 135)
    @GetMapping("/details")
    public Result<MaintainRecordDetails> getMaintainRecordDetails(MaintainDetailsParms parmsReq,
                                                                  @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busMaintainDeviceService.getMaintainRecordDetails(sub, parmsReq);
    }

    @OperLog(operModul = "保养记录", operType = OperType.ADD, operDesc = "新增保养记录", operatorType = "后台")
    @ApiOperation(value = "新增保养记录", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 135)
    @PostMapping
    public Result addMaintainRecord(@RequestBody @Valid AddMaintainRecord parmsReq,
                                    @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return mainTainService.addMaintainRecord(sub, parmsReq);
    }


    @OperLog(operModul = "保养记录", operType = OperType.DEL, operDesc = "删除", operatorType = "后台")
    @ApiOperation(value = "删除", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 137)
    @DeleteMapping("/{maintainId}")
    public Result delMaintainDevice(@PathVariable("maintainId") @ApiParam(name = "保养设备ID", required = true) String maintainId,
                                    @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busMaintainDeviceService.delMaintainDevice(sub, maintainId);
    }

}
