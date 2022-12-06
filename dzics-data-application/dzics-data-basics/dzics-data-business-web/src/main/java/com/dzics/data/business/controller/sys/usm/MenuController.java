package com.dzics.data.business.controller.sys.usm;

import com.dzics.data.ums.model.dto.permission.AddPermission;
import com.dzics.data.ums.model.dto.permission.UpdatePermission;
import com.dzics.data.ums.model.vo.router.MenusInfo;
import com.dzics.data.ums.service.DzicsUserService;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.vo.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author ZhangChengJun
 * Date 2021/1/8.
 */
@Api(tags = {"菜单管理"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/user")
@Controller
public class MenuController {
    @Autowired
    private DzicsUserService userService;


//    @OperLog(operModul = "菜单管理", operType = OperType.QUERY, operDesc = "菜单列表", operatorType = "后台")
    @ApiOperation(value = "菜单列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/menu")
    public Result<MenusInfo> selMenuPermission(
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return userService.selMenuPermission(sub);
    }

    @OperLog(operModul = "菜单管理", operType = OperType.ADD, operDesc = "新增菜单", operatorType = "后台")
    @ApiOperation(value = "新增菜单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PostMapping(value = "/menu")
    public Result addPermission(
            @Valid @RequestBody AddPermission addPermission,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return userService.addPermission(addPermission, sub);
    }


//    @OperLog(operModul = "菜单管理", operType = OperType.QUERY, operDesc = "菜单列表", operatorType = "后台")
    @ApiOperation(value = "菜单详情", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/menu/{id}")
    public Result<MenusInfo> selMenuPermission(@PathVariable("id") String id,
                                               @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                               @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return userService.selMenuPermissionId(Long.valueOf(id),sub);
    }

    @OperLog(operModul = "菜单管理", operType = OperType.UPDATE, operDesc = "修改菜单", operatorType = "后台")
    @ApiOperation(value = "修改菜单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PutMapping(value = "/menu")
    public Result updatePermission(@Valid @RequestBody UpdatePermission updatePermission,
                                   @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return userService.updatePermission(updatePermission, sub);
    }

    @OperLog(operModul = "菜单管理", operType = OperType.DEL, operDesc = "删除菜单", operatorType = "后台")
    @ApiOperation(value = "删除菜单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @DeleteMapping(value = "/menu/{id}")
    public Result delPermission(@PathVariable("id") String id,
                                @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return userService.delPermission(Long.valueOf(id), sub);
    }
}
