package com.dzics.data.business.controller.sys.usm;

import com.dzics.data.ums.model.dto.user.PutUserPasswordVo;
import com.dzics.data.ums.model.vo.user.UserInfo;
import com.dzics.data.ums.service.DzicsUserService;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.business.auth.JwtAuthService;
import com.dzics.data.fms.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"当前用户信息"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/user/info")
public class UserInfoController {

    @Autowired
    private DzicsUserService userService;

    @Autowired
    private FileService fileService;
    @Autowired
    private JwtAuthService jwtAuthService;

    @ApiOperation(value = "查询用户信息")
    @GetMapping
    public Result<UserInfo> getInfo(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result<UserInfo> info = userService.getInfo(sub);
        return new Result(CustomExceptionType.OK, info.getData().getUser());
    }


    @OperLog(operModul = "当前用户信息", operType = OperType.UPDATE, operDesc = "更改用户密码", operatorType = "后台")
    @ApiOperation(value = "更改用户密码")
    @PutMapping("/password")
    public Result putPassword(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                              @RequestBody @Valid PutUserPasswordVo putUserInfoVo
    ) {
        Result result = userService.putPassword(sub, putUserInfoVo);
        if (result == null) {
            return jwtAuthService.signout(sub);
        }
        return result;
    }

}
