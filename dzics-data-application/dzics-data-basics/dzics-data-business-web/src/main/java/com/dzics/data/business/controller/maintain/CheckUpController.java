package com.dzics.data.business.controller.maintain;

import com.dzics.data.business.service.MainTainService;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.maintain.model.dto.CheckUpVo;
import com.dzics.data.maintain.model.vo.DzCheckUpItemDo;
import com.dzics.data.maintain.service.DzCheckUpItemService;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"巡检项设置管理"}, produces = "巡检项设置管理相关接口")
@RestController
@RequestMapping("/api/check/up")
public class CheckUpController {

    @Autowired
    private MainTainService mainTainService;
    @Autowired
    private DzCheckUpItemService checkUpItemService;
    @Autowired
    private DzicsUserService userService;

    @OperLog(operModul = "巡检项设置管理", operType = OperType.ADD, operDesc = "添加巡检项", operatorType = "后台")
    @ApiOperation(value = "添加巡检项")
    @PostMapping
    public Result add(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @RequestBody @Valid CheckUpVo checkUpVo) {
        Result result = mainTainService.addCheck(sub, checkUpVo);
        return result;
    }

    @ApiOperation(value = "查询巡检项列表")
    @GetMapping
    public Result<DzCheckUpItemDo> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                        PageLimit pageLimit, Integer deviceType, String checkName) {
        SysUser byUserName = userService.getByUserName(sub);
        String useOrgCode = byUserName.getUseOrgCode();
        Result result = mainTainService.listCheck(pageLimit, deviceType, checkName,useOrgCode);
        return result;
    }

    @OperLog(operModul = "巡检项设置管理", operType = OperType.DEL, operDesc = "删除巡检项", operatorType = "后台")
    @ApiOperation(value = "删除巡检项")
    @DeleteMapping("/{checkItemId}")
    public Result del(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @PathVariable("checkItemId") String checkItemId) {
        Result result = checkUpItemService.del(checkItemId);
        return result;
    }

    @OperLog(operModul = "巡检项设置管理", operType = OperType.UPDATE, operDesc = "编辑巡检项", operatorType = "后台")
    @ApiOperation(value = "编辑巡检项")
    @PutMapping
    public Result put(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @RequestBody @Valid CheckUpVo checkUpVo) {
        Result result = mainTainService.putCheck(sub, checkUpVo);
        return result;
    }
}
