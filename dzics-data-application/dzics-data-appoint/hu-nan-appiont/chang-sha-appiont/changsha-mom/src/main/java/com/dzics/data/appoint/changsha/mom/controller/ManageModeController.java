package com.dzics.data.appoint.changsha.mom.controller;

import com.dzics.data.appoint.changsha.mom.enums.OperatingModeEnum;
import com.dzics.data.appoint.changsha.mom.model.entity.ManageMode;
import com.dzics.data.appoint.changsha.mom.service.ManageModeService;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.vo.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author LiuDongFei
 * @date 2022年09月13日 15:30
 */
@Api(tags = {"管理模式"})
@RestController
@Slf4j
@RequestMapping("/api/mom/mode")
public class ManageModeController {
    @Autowired
    private ManageModeService manageModeService;

    @OperLog(operModul = "管理模式", operType = OperType.UPDATE, operDesc = "切换手动", operatorType = "后台")
    @ApiOperation("切换手动按钮")
    @ApiOperationSupport(author = "Ubiead", order = 111)
    @PostMapping("/manualMode/{type}")
    public Result<String> manualMode(@PathVariable("type") String type) {
        log.info("ManageModeController [orderMode] 操作模式状态{}", type);
        manageModeService.manualOrder(OperatingModeEnum.NUMBER.val(), type);
        return Result.ok();
    }

    @ApiOperation("模式查询")
    @GetMapping("/mode")
    public Result<ManageMode> mode(String code) {
        return Result.ok(manageModeService.getByCode(code));
    }

    @ApiOperation("模式变更")
    @PostMapping("upd")
    public Result<ManageMode> upd(@RequestBody ManageMode manageMode) {
        log.info("ManageModeController [upd] code{}, type{}", manageMode.getCode(),manageMode.getType());
        manageModeService.manualOrder(manageMode.getCode(),manageMode.getType());
        return Result.ok();
    }
}

