package com.dzics.data.appoint.changsha.mom.controller;

import com.dzics.data.appoint.changsha.mom.model.entity.DzicsMaintenancePatrol;
import com.dzics.data.appoint.changsha.mom.model.vo.AddMainTenPatrolVo;
import com.dzics.data.appoint.changsha.mom.model.vo.EditMainTenPatrolVo;
import com.dzics.data.appoint.changsha.mom.service.DzicsMaintenancePatrolService;
import com.dzics.data.common.base.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author xnb
 * @date 2022/11/28 0028 11:41
 */
@Slf4j
@Api(tags = {"主动巡检维修设置"})
@RestController
@RequestMapping("api/dzicsMaintenancePatrol/active")
public class DzicsMaintenancePatrolActiveController {

    @Autowired
    private DzicsMaintenancePatrolService patrolService;

    @ApiOperation(value = "查询维修巡检列表")
    @GetMapping("/list")
    public Result<List<DzicsMaintenancePatrol>> getActiveList(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                String type, String message){
        List<DzicsMaintenancePatrol> patrol = patrolService.getPatrol(type, message,"2");
        return Result.OK(patrol);
    }

    @ApiOperation(value = "编辑巡检维修")
    @PutMapping("/edit")
    public Result editMainTenPatrol(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                    @RequestBody @Valid EditMainTenPatrolVo editMainTenPatrolVo){
        return patrolService.editPatrol(editMainTenPatrolVo);
    }

    @ApiOperation(value = "删除巡检维修")
    @DeleteMapping("/remove/{id}")
    public Result delMainTenPatrol(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                   @PathVariable String id){
        return patrolService.delPatrol(id);
    }

    @ApiOperation(value = "新增巡检维修")
    @PostMapping("/add")
    public Result addMainTenPatrol(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                   @RequestBody @Valid AddMainTenPatrolVo addMainTenPatrolVo){
        return patrolService.addPatrol(addMainTenPatrolVo);
    }

}
