package com.dzics.data.business.controller.sys.usm;

import com.dzics.data.business.service.BusUserDepartService;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.ums.model.dto.depart.AddDepart;
import com.dzics.data.ums.model.dto.depart.DisableEnabledDepart;
import com.dzics.data.ums.model.dto.depart.SelDepart;
import com.dzics.data.ums.model.dto.depart.UpdateDepart;
import com.dzics.data.ums.model.vo.depart.ResDepart;
import com.dzics.data.ums.service.SysDepartService;
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

@Api(tags = {"站点管理"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/user")
@Controller
public class DepartController {

    @Autowired
    private SysDepartService departService;
    @Autowired
    private BusUserDepartService busUserDepartService;

    @OperLog(operModul = "站点管理", operType = OperType.ADD, operDesc = "新增", operatorType = "后台")
    @ApiOperation(value = "新增", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PostMapping(value = "/depart")
    public Result addDepart(
            @Valid @RequestBody AddDepart addDepart,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return departService.addDepart(addDepart, sub);
    }

    @OperLog(operModul = "站点管理", operType = OperType.ADD, operDesc = "更新", operatorType = "后台")
    @ApiOperation(value = "更新", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PutMapping(value = "/depart")
    public Result updateDepart(
            @Valid @RequestBody UpdateDepart updateDepart,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return departService.updateDepart(updateDepart, sub);
    }

    //    @OperLog(operModul = "站点管理", operType = OperType.QUERY, operDesc = "新增编辑取参", operatorType = "后台")
    @ApiOperation(value = "新增编辑取参", notes = "添加站点无参会返回权限树形结构, 编辑站点时或返回站点信息,树形结构,已选择结果id", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 110)
    @GetMapping(value = "/depart/msg")
    public Result getDepartMsg(@RequestParam(value = "departId", required = false) Long departId,
                               @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                               @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return departService.getDepartMsg(sub, departId);
    }


    //    @OperLog(operModul = "站点管理", operType = OperType.QUERY, operDesc = "详情", operatorType = "后台")
    @ApiOperation(value = "详情", notes = "详情", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 110)
    @GetMapping(value = "/depart/{departId}")
    public Result<ResDepart> getDepartDetails(@PathVariable("departId") Long departId,
                                              @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return departService.getDepartDetails(sub, departId);
    }


    //    @OperLog(operModul = "站点管理", operType = OperType.QUERY, operDesc = "列表", operatorType = "后台")
    @ApiOperation(value = "列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/depart")
    public Result<List<ResDepart>> queryDepart(
            PageLimit pageLimit, SelDepart selDepart,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return departService.queryDepart(pageLimit, selDepart, sub);
    }


    @OperLog(operModul = "站点管理", operType = OperType.DEL, operDesc = "删除", operatorType = "后台")
    @ApiOperation(value = "删除", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @DeleteMapping(value = "/depart/{departId}")
    public Result<ResDepart> delDepart(
            @PathVariable("departId") @ApiParam(value = "站点id", required = true) String departId,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busUserDepartService.delDepart(departId, sub);
    }


    @OperLog(operModul = "站点管理", operType = OperType.UPDATE, operDesc = "禁用启用", operatorType = "后台")
    @ApiOperation(value = "禁用启用", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PutMapping(value = "/disable/enabled/depart")
    public Result disableEnabledRole(
            @Valid @RequestBody DisableEnabledDepart enabledDepart,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return departService.disableEnabledRole(enabledDepart, sub);
    }
}
