package com.dzics.data.business.controller.logger;

import com.dzics.data.business.service.LogService;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.logms.model.entity.DzEquipmentStateLog;
import com.dzics.data.logms.model.vo.SelectEquipmentStateVo;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api(tags = {"设备运行状态管理"}, produces = "设备运行状态管理相关接口")
@RestController
@RequestMapping("/equipmentState/log")
public class EquipmentStateController {

    @Autowired
    LogService logService;


    @ApiOperation(value = "设备运行状态日志查询")
    @ApiOperationSupport(author = "jq", order = 2)
    @GetMapping
    public Result<List<DzEquipmentStateLog>> list(PageLimit pageLimit, @Valid SelectEquipmentStateVo selectEquipmentStateVo,
                                                  @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub){
        return logService.list(sub,pageLimit,selectEquipmentStateVo);
    }

}
