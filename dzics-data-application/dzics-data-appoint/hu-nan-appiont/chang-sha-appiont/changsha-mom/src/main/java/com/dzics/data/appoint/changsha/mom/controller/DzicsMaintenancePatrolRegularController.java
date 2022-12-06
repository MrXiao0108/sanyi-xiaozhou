package com.dzics.data.appoint.changsha.mom.controller;


import com.dzics.data.appoint.changsha.mom.model.entity.DzicsMaintenancePatrol;
import com.dzics.data.appoint.changsha.mom.model.vo.AddMainTenPatrolVo;
import com.dzics.data.appoint.changsha.mom.model.vo.EditMainTenPatrolVo;
import com.dzics.data.appoint.changsha.mom.service.DzicsMaintenancePatrolService;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

/**
 * <p>
 * 巡检维修表 前端控制器
 * </p>
 *
 * @author xnb
 * @since 2022-11-21
 */
@Slf4j
@Api(tags = {"定期巡检维修设置"})
@RestController
@RequestMapping("api/dzicsMaintenancePatrol")
public class DzicsMaintenancePatrolRegularController {

    @Autowired
    private DzicsMaintenancePatrolService patrolService;

    @ApiOperation(value = "新增巡检维修")
    @PostMapping("/add")
    public Result<List<DzicsMaintenancePatrol>> addMainTenPatrol(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                   @RequestBody @Valid AddMainTenPatrolVo addMainTenPatrolVo){
        if(addMainTenPatrolVo.getIntervalTime()==null || addMainTenPatrolVo.getIntervalTime()<=0){
            throw new CustomException(CustomExceptionType.Parameter_Exception,"时间间隔不能为空并且大于0");
        }
        return patrolService.addPatrol(addMainTenPatrolVo);
    }

    @ApiOperation(value = "编辑巡检维修")
    @PutMapping("/edit")
    public Result editMainTenPatrol(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                   @RequestBody @Valid EditMainTenPatrolVo editMainTenPatrolVo){
        return patrolService.editPatrol(editMainTenPatrolVo);
    }

    @ApiOperation(value = "查询巡检维修")
    @GetMapping("/list")
    public Result<List<DzicsMaintenancePatrol>> getMainTenPatrol(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                                 @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                                 String type, String message){
        return Result.ok(patrolService.getPatrol(type, message,"1"));
    }

    @ApiOperation(value = "删除巡检维修")
    @DeleteMapping("/remove/{id}")
    public Result delMainTenPatrol(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                    @PathVariable String id){
        return patrolService.delPatrol(id);
    }

    @ApiOperation(value = "确认处理按钮")
    @PutMapping("/headle/{id}")
    public Result putHealed(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                            @PathVariable String id) throws ParseException {
        return patrolService.putHealed(id);
    }


}

