package com.dzics.data.business.controller.pms;


import com.dzics.data.business.service.PmsService;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pms.model.dto.AddDetectorPro;
import com.dzics.data.pms.model.dto.EditProDuctTemp;
import com.dzics.data.pms.model.dto.ProDuctCheck;
import com.dzics.data.pms.model.vo.DetectionTemplateParm;
import com.dzics.data.pms.model.vo.GroupId;
import com.dzics.data.pms.service.DzDetectionTemplateService;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
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
 * 检测设置
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@Api(tags = {"产品检测配置"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/detection/item")
@Controller
public class DetectionSetController {
    @Autowired
    private PmsService busDetectorDataService;
    @Autowired
    private DzDetectionTemplateService templateService;
    @Autowired
    private DzicsUserService userService;
/*
   用于重置检测配置项默认模板 ,查询后重新同步设置，项目部署整理数据库时有用。一般情况项没用
    @ApiOperation(value = "JC", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/jc")
    public Result jc(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        List<DzDetectionTemplate> list = templateService.list();
        return Result.OK(list);
    }

    @ApiOperation(value = "/upjc", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PostMapping(value = "/upjc")
    public Result upDatejc(@RequestBody List<DzDetectionTemplate> templates,
                           @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        templateService.list();
        templateService.remove(Wrappers.emptyWrapper());
        templateService.saveBatch(templates);
        return Result.ok();
    }*/

    /**
     * 检测记录
     */
    @ApiOperation(value = "检测设置", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/all")
    public Result<DetectionTemplateParm> selDetectorItem(GroupId groupId, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busDetectorDataService.selDetectorItem(groupId, sub);
    }

    @OperLog(operModul = "产品检测配置", operType = OperType.ADD, operDesc = "新增检测", operatorType = "后台")
    @ApiOperation(value = "新增检测", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PostMapping
    public Result addDetectorItem(@RequestBody @Valid AddDetectorPro detectorPro, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        SysUser byUserName = userService.getByUserName(sub);
        return busDetectorDataService.addDetectorItem(detectorPro, sub, byUserName.getUseDepartId());
    }

    @ApiOperation(value = "配置列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping
    public Result queryProDetectorItem(ProDuctCheck proDuctCheck, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result listResult = busDetectorDataService.queryProDetectorItem(proDuctCheck, sub);
        return listResult;
    }


    @OperLog(operModul = "产品检测配置", operType = OperType.UPDATE, operDesc = "修改检测配置", operatorType = "后台")
    @ApiOperation(value = "修改检测配置", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PutMapping
    public Result editProDetectorItem(@RequestBody @Valid EditProDuctTemp editProDuctTemp, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busDetectorDataService.editProDetectorItem(editProDuctTemp, sub);
    }


    @OperLog(operModul = "产品检测配置", operType = OperType.UPDATE, operDesc = "修改检测配置", operatorType = "后台")
    @ApiOperation(value = "对比值修改", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PutMapping("/contrast")
    public Result dbProDetectorItem(@RequestBody @Valid EditProDuctTemp editProDuctTemp, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busDetectorDataService.dbProDetectorItem(editProDuctTemp, sub);
    }

    @OperLog(operModul = "产品检测配置", operType = OperType.DEL, operDesc = "删除检测配置", operatorType = "后台")
    @ApiOperation(value = "删除检测配置", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @DeleteMapping("/{groupId}")
    public Result delProDetectorItem(@PathVariable("groupId") String groupId, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busDetectorDataService.delProDetectorItem(groupId, sub);
    }
}
