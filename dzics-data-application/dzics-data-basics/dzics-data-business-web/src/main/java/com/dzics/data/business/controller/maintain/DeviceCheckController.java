package com.dzics.data.business.controller.maintain;


import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.maintain.model.dto.DeviceCheckVo;
import com.dzics.data.maintain.model.dto.GetDeviceCheckVo;
import com.dzics.data.maintain.model.entity.DzCheckHistoryItem;
import com.dzics.data.maintain.model.vo.GetDeviceCheckDo;
import com.dzics.data.maintain.service.DzCheckHistoryService;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.dzics.data.business.service.MainTainService;
import javax.validation.Valid;
import java.util.List;

@SuppressWarnings("ALL")
@Api(tags = {"设备巡检项记录管理"}, produces = "设备巡检项记录管理相关接口")
@RestController
@RequestMapping("/api/device/check")
public class DeviceCheckController {

    @Autowired
    private MainTainService mainTainService;
    @Autowired
    private DzCheckHistoryService checkHistoryService;
    @Autowired
    private DzicsUserService  userService;

    @OperLog(operModul = "巡检项记录管理", operType = OperType.ADD, operDesc = "添加设备巡检项记录", operatorType = "后台")
    @ApiOperation(value = "添加设备巡检项记录")
    @PostMapping
    public Result add(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @RequestBody @Valid DeviceCheckVo deviceCheckVo){
        Result result=mainTainService.addDevice(sub,deviceCheckVo);
        return result;
    }

    @ApiOperation(value = "查询巡检项记录")
    @GetMapping
    public Result<List<GetDeviceCheckDo>> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                         GetDeviceCheckVo getDeviceCheckVo){
        SysUser byUserName = userService.getByUserName(sub);
        String useOrgCode = byUserName.getUseOrgCode();
        getDeviceCheckVo.setUseOrgCode(useOrgCode);
        return checkHistoryService.list(getDeviceCheckVo);
    }

    @ApiOperation(value = "查询巡检项记录详情")
    @GetMapping("/{checkHistoryId}")
    public Result<DzCheckHistoryItem> getById(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                              @PathVariable("checkHistoryId") String checkHistoryId){
        Result result =mainTainService.getById(checkHistoryId);
        return result;
    }
    @OperLog(operModul = "巡检项记录管理", operType = OperType.UPDATE, operDesc = "修改巡检项记录", operatorType = "后台")
    @ApiOperation(value = "修改巡检项记录")
    @PutMapping
    public Result<DzCheckHistoryItem> put(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                              @RequestBody List<DzCheckHistoryItem> list){
        Result result =checkHistoryService.put(sub,list);
        return result;
    }
    @OperLog(operModul = "巡检项记录管理", operType = OperType.DEL, operDesc = "删除巡检项记录", operatorType = "后台")
    @ApiOperation(value = "删除巡检项记录")
    @DeleteMapping("/{checkHistoryId}")
    public Result del(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                          @PathVariable("checkHistoryId") String checkHistoryId){
        Result result =checkHistoryService.del(sub,checkHistoryId);
        return result;
    }
}
