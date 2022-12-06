package com.dzics.data.kanbanrouting.controller;


import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.boardms.model.dto.menu.AddPermission;
import com.dzics.data.boardms.model.dto.menu.SelKbRouting;
import com.dzics.data.boardms.model.dto.menu.UpdatePermission;
import com.dzics.data.boardms.model.entity.SysKanbanRouting;
import com.dzics.data.boardms.model.vo.MenusInfo;
import com.dzics.data.boardms.service.KanbanService;
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
 * 看板路由管理
 *
 * @author ZhangChengJun
 * Date 2021/4/28.
 * @since
 */
@Api(tags = {"看板路由"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/route/kanban")
@Controller
public class KanBanController {
    @Autowired
    private KanbanService kanbanService;

    @ApiOperation(value = "采单列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/menu")
    public Result<MenusInfo> selMenuPermission(
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return kanbanService.selMenuPermission(sub);
    }


    @OperLog(operModul = "看板路由", operType = OperType.ADD, operDesc = "新增采单", operatorType = "后台")
    @ApiOperation(value = "新增采单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PostMapping(value = "/menu")
    public Result addPermission(
            @Valid @RequestBody AddPermission addPermission,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return kanbanService.addPermission(addPermission, sub);
    }


    @ApiOperation(value = "采单详情", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/menu/{id}")
    public Result<MenusInfo> selMenuPermission(@PathVariable("id") Long id,
                                               @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                               @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return kanbanService.selMenuPermissionId(id, sub);
    }

    @OperLog(operModul = "采单管理", operType = OperType.UPDATE, operDesc = "修改采单", operatorType = "后台")
    @ApiOperation(value = "修改采单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PutMapping(value = "/menu")
    public Result updatePermission(@Valid @RequestBody UpdatePermission updatePermission,
                                   @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return kanbanService.updatePermission(updatePermission, sub);
    }

    @OperLog(operModul = "采单管理", operType = OperType.DEL, operDesc = "删除采单", operatorType = "后台")
    @ApiOperation(value = "删除采单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @DeleteMapping(value = "/menu/{id}")
    public Result delPermission(@PathVariable("id") Long id,
                                @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return kanbanService.delPermission(id, sub);
    }

    @ApiOperation(value = "子路由列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/menu/routing/details")
    public Result<SysKanbanRouting> selRoutingDetails(SelKbRouting kbRouting,
                                                      @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return kanbanService.selRoutingDetails(kbRouting, sub);
    }

    @ApiOperation(value = "路由", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/menu/routing")
    public Result<SysKanbanRouting> selMenuRouting( SelKbRouting kbRouting,
                                                       @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return kanbanService.selMenuRouting(kbRouting, sub);
    }


    @ApiOperation(value = "path获取订单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/menu/routing/path")
    public Result<SysKanbanRouting> selRoutingDetailsOrder(SelKbRouting kbRouting,
                                                      @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return kanbanService.selRoutingDetailsOrder(kbRouting, sub);
    }

}
