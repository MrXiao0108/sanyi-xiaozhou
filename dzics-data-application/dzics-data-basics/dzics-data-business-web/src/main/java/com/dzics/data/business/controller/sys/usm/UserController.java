package com.dzics.data.business.controller.sys.usm;

import com.dzics.data.ums.model.dto.user.*;
import com.dzics.data.ums.model.vo.user.UserListRes;
import com.dzics.data.ums.service.DzicsUserService;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.model.page.PageLimit;
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
 * 用户管理前后端对接接口层
 *
 * @author ZhangChengJun
 * Date 2022/1/26.
 * @since
 */
@Api(tags = {"用户管理"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/user/account")
@Controller
public class UserController {
    @Autowired
    private DzicsUserService userService;



    @ApiOperation(value = "账号列表")
    @ApiOperationSupport(author = "NeverEnd", order = 130)
    @GetMapping(value = "/user")
    public Result<UserListRes> userLists(PageLimit pageLimit, SelUser selUser,
                                         @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result result = userService.userLists(pageLimit, selUser, sub);
        return result;
    }


    @OperLog(operModul = "用户管理", operType = OperType.ADD, operDesc = "新增账号", operatorType = "后台")
    @ApiOperation(value = "新增账号")
    @ApiOperationSupport(author = "NeverEnd", order = 130)
    @PostMapping(value = "/user")
    public Result addUser(@Valid @RequestBody RegisterVo registerVo,
                          @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result result = userService.addUser(registerVo, sub);
        return result;
    }

    @OperLog(operModul = "用户管理", operType = OperType.UPDATE, operDesc = "编辑账号", operatorType = "后台")
    @ApiOperation(value = "编辑账号")
    @ApiOperationSupport(author = "NeverEnd", order = 131)
    @PutMapping(value = "/user")
    public Result delUser(@Valid @RequestBody UpdateUser updateUser,
                          @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result result = userService.updateUser(updateUser, sub);
        return result;
    }

    @OperLog(operModul = "用户管理", operType = OperType.UPDATE, operDesc = "禁用启用账号", operatorType = "后台")
    @ApiOperation(value = "禁用启用账号")
    @ApiOperationSupport(author = "NeverEnd", order = 132)
    @PutMapping(value = "/disable/enabled/user")
    public Result disableEnabledUser(@Valid @RequestBody DisableEnabledUser disableEnabledUser,
                                     @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result result = userService.disableEnabledUser(disableEnabledUser, sub);
        return result;
    }

    @OperLog(operModul = "用户管理", operType = OperType.UPDATE, operDesc = "重置密码", operatorType = "后台")
    @ApiOperation(value = "重置密码")
    @ApiOperationSupport(author = "NeverEnd", order = 133)
    @PutMapping(value = "/reset/user")
    public Result resetUser(@Valid @RequestBody ResetUser resetUser,
                            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result result = userService.resetUser(resetUser, sub);
        return result;
    }

    @OperLog(operModul = "用户管理", operType = OperType.DEL, operDesc = "删除账号", operatorType = "后台")
    @ApiOperation(value = "删除账号")
    @ApiOperationSupport(author = "NeverEnd", order = 134)
    @DeleteMapping(value = "/user/{userId}/{usernum}")
    public Result delUser(@PathVariable("userId") String userId,
                          @PathVariable("usernum") String usernum,
                          @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result result = userService.delUser(userId, usernum, sub);
        return result;
    }


    @ApiOperation(value = "添加用户用户参数信息", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 110)
    @GetMapping(value = "/role")
    public Result getRoles(@RequestParam(value = "userId", required = false) String userId,
                           @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return userService.getRolesNotAdmin(sub, userId);
    }


}
