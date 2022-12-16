package com.dzics.data.business.controller.tool;


import com.dzics.data.business.service.ToolService;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.dzics.data.tool.db.model.dao.GetToolInfoDataListDo;
import com.dzics.data.tool.db.model.vo.GetToolInfoDataListVo;
import com.dzics.data.tool.model.dto.AddToolConfigureVo;
import com.dzics.data.tool.model.entity.DzToolCompensationData;
import com.dzics.data.tool.model.entity.DzToolGroups;
import com.dzics.data.tool.service.DzToolCompensationDataService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"刀具配置"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/product/check")
@Controller
public class ToolConfigureController {

    @Autowired
    private ToolService toolService;
    @Autowired
    private DzToolCompensationDataService toolCompensationDataService;


    @ApiOperation(value = "查询刀具配置列表")
    @ApiOperationSupport(author = "jq", order = 20)
    @GetMapping("/getToolConfigureList")
    public Result<DzToolCompensationData> getToolConfigureList(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                               @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                               PageLimit pageLimit,
                                                               @RequestParam(value = "groupNo", required = false) Integer groupNo) {

        Result toolConfigureList = toolService.getToolConfigureList(sub, pageLimit, groupNo);
        return toolConfigureList;
    }

    @ApiOperation(value = "新增刀具配置")
    @ApiOperationSupport(author = "jq", order = 20)
    @PostMapping("/addToolConfigure")
    public Result addToolConfigure(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                   @RequestBody @Valid AddToolConfigureVo addToolConfigureVo) {

        return toolService.addToolConfigure(addToolConfigureVo);
    }


    @ApiOperation(value = "删除刀具配置")
    @ApiOperationSupport(author = "jq", order = 20)
    @DeleteMapping("/delToolConfigure/{id}")
    public Result addToolConfigure(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                   @PathVariable("id") String id) {

        return toolCompensationDataService.delToolConfigure(id);
    }


    @ApiOperation(value = "根据组编号和设备id查询改组下未绑定的刀具")
    @ApiOperationSupport(author = "jq", order = 20)
    @GetMapping("/getToolByEqIdAndGroupNo")
    public Result getToolByEqIdAndGroupNo(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                          @RequestParam("equipmentId") Long equipmentId,
                                          @RequestParam("groupNo") Integer groupNo,
                                          @RequestParam("toolGroupsId") Long toolGroupsId) {

        return toolCompensationDataService.getToolByEqIdAndGroupNo(equipmentId, groupNo, toolGroupsId);
    }

    @ApiOperation(value = "編輯刀具配置信息")
    @ApiOperationSupport(author = "jq", order = 20)
    @PutMapping("/putToolConfigure")
    public Result putToolConfigure(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                   @RequestBody @Valid AddToolConfigureVo addToolConfigureVo
    ) {

        return toolCompensationDataService.putToolConfigure(addToolConfigureVo);
    }

    @ApiOperation(value = "根据id查询刀具配置信息")
    @ApiOperationSupport(author = "jq", order = 20)
    @GetMapping("/getToolConfigureById/{id}")
    public Result getToolConfigureById(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                       @PathVariable("id") String id
    ) {

        return toolCompensationDataService.getToolConfigureById(id);
    }

    /**
     * 查询刀具组列表
     *
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @ApiOperation(value = "查询所有刀具组")
    @ApiOperationSupport(author = "jq", order = 10)
    @GetMapping("/getToolGroupsAll")
    public Result<DzToolGroups> getToolGroupsAll(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                 @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return toolService.getToolGroupsAll(sub);
    }

    @ApiOperation(value = "根据产线查询机床列表")
    @ApiOperationSupport(author = "jq", order = 20)
    @GetMapping("/getEquipmentByLine/{lineId}")
    public Result<DzEquipment> getEquipmentByLine(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                  @PathVariable("lineId") String lineId
    ) {

        return toolService.getEquipmentByLine(lineId);
    }

    /**
     * 刀具信息数据
     *
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @ApiOperation(value = "查询刀具信息数据")
    @ApiOperationSupport(author = "jq", order = 10)
    @GetMapping("/getToolInfoDataList")
    public Result<GetToolInfoDataListDo> getToolInfoDataList(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                             @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                             PageLimit pageLimit, GetToolInfoDataListVo getToolInfoDataListVo) {
        Result toolInfoDataList = toolService.getToolInfoDataList(sub, pageLimit, getToolInfoDataListVo);
        return toolInfoDataList;
    }

    @OperLog(operModul = "刀具配置", operType = OperType.ADD, operDesc = "新增刀具配置(根据设备id批量配置)", operatorType = "后台")
    @ApiOperation(value = "新增刀具配置(根据设备id批量配置)")
    @ApiOperationSupport(author = "jq", order = 20)
    @GetMapping("/addToolConfigure/{byEquipmentId}")
    public Result addToolConfigureById(@PathVariable("byEquipmentId") String byEquipmentId) {
        return toolService.addToolConfigureById(byEquipmentId);
    }
}
