package com.dzics.data.business.controller.tool;

import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.tool.model.dto.AddDzToolGroupVo;
import com.dzics.data.tool.model.dto.AddToolInfoVo;
import com.dzics.data.tool.model.dto.PutToolGroupsVo;
import com.dzics.data.tool.model.dto.PutToolInfoByIdVo;
import com.dzics.data.tool.model.entity.DzToolGroups;
import com.dzics.data.tool.model.entity.DzToolInfo;
import com.dzics.data.tool.service.DzToolGroupsService;
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


@Api(tags = {"刀具组信息"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/product/check")
@Controller
public class ToolGroupsInfoController {
    @Autowired
    private DzToolGroupsService businessToolGroupsService;

    @Autowired
    private DzicsUserService userService;

    /**
     * 查询刀具组列表
     *
     * @param tokenHdaer
     * @param sub
     * @param groupNo
     * @return
     */
    @ApiOperation(value = "查询刀具组")
    @ApiOperationSupport(author = "jq", order = 10)
    @GetMapping
    public Result<DzToolGroups> getToolGroupsList(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                  PageLimit pageLimit,
                                                  @RequestParam(value = "groupNo", required = false) String groupNo) {
        SysUser byUserName = userService.getByUserName(sub);
        String useOrgCode = byUserName.getUseOrgCode();
        Result toolGroupsList = businessToolGroupsService.getToolGroupsList(sub, pageLimit, groupNo,useOrgCode);
        return toolGroupsList;
    }

    /**
     * 新增刀具组
     *
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @OperLog(operModul = "新增刀具组", operType = OperType.ADD, operDesc = "新增刀具组", operatorType = "后台")
    @ApiOperation(value = "新增刀具组")
    @ApiOperationSupport(author = "jq", order = 11)
    @PostMapping
    public Result add(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @RequestBody @Valid AddDzToolGroupVo addDzToolGroupVo) {
        SysUser user = userService.getByUserName(sub);
        String orgCode = user.getUseOrgCode();
        return businessToolGroupsService.addToolGroups(addDzToolGroupVo, orgCode);
    }

    /**
     * 新增刀具组
     *
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @OperLog(operModul = "删除刀具组", operType = OperType.DEL, operDesc = "删除刀具组", operatorType = "后台")
    @ApiOperation(value = "删除刀具组")
    @ApiOperationSupport(author = "jq", order = 12)
    @DeleteMapping(value = "/{toolGroupsId}")
    public Result delToolGroups(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                @PathVariable("toolGroupsId") String toolGroupsId
    ) {
        return businessToolGroupsService.delToolGroups(toolGroupsId);
    }

    /**
     * 编辑刀具组编号
     *
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @OperLog(operModul = "修改刀具组编号", operType = OperType.UPDATE, operDesc = "修改刀具组编号", operatorType = "后台")
    @ApiOperation(value = "修改刀具组编号")
    @ApiOperationSupport(author = "jq", order = 13)
    @PutMapping
    public Result putToolGroups(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                @RequestBody @Valid PutToolGroupsVo putToolGroupsVo
    ) {
        return businessToolGroupsService.putToolGroups(putToolGroupsVo);
    }

    /**
     * 根据刀具组id查询刀具列表
     *
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @ApiOperation(value = "根据刀具组id查询刀具列表")
    @ApiOperationSupport(author = "jq", order = 14)
    @GetMapping("/toolGroupsId")
    public Result<DzToolInfo> getToolInfoList(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                              @RequestParam("toolGroupsId") Long toolGroupsId, PageLimit pageLimit
    ) {
        return businessToolGroupsService.getToolInfoList(toolGroupsId, pageLimit);
    }

    /**
     * 删除刀具
     *
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @OperLog(operModul = "删除刀具", operType = OperType.DEL, operDesc = "删除刀具", operatorType = "后台")
    @ApiOperation(value = "删除刀具")
    @ApiOperationSupport(author = "jq", order = 15)
    @DeleteMapping("/delToolInfo/{id}")
    public Result delToolInfo(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                              @PathVariable("id") @ApiParam("刀具id") Long id
    ) {
        return businessToolGroupsService.delToolInfo(id);
    }

    /**
     * 编辑刀具
     *
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @OperLog(operModul = "编辑刀具", operType = OperType.UPDATE, operDesc = "编辑刀具", operatorType = "后台")
    @ApiOperation(value = "编辑刀具")
    @ApiOperationSupport(author = "jq", order = 16)
    @PutMapping("/putToolInfo")
    public Result putToolInfo(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                              @RequestBody @Valid PutToolInfoByIdVo putToolInfoByIdVo
    ) {
        return businessToolGroupsService.putToolInfo(putToolInfoByIdVo.getId(), putToolInfoByIdVo.getToolNo());
    }

    /**
     * 新增刀具
     *
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @OperLog(operModul = "新增刀具", operType = OperType.ADD, operDesc = "新增刀具", operatorType = "后台")
    @ApiOperation(value = "新增刀具")
    @ApiOperationSupport(author = "jq", order = 17)
    @PostMapping("/addToolInfo")
    public Result addToolInfo(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                              @RequestBody @Valid AddToolInfoVo addToolInfoVo
    ) {
        return businessToolGroupsService.addToolInfo(addToolInfoVo.getToolGroupId(), addToolInfoVo.getToolNo());
    }
}
