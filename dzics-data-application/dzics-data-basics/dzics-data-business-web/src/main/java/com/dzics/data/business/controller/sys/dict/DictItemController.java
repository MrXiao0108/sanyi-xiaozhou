package com.dzics.data.business.controller.sys.dict;



import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.DictItemVo;
import com.dzics.data.pub.model.entity.SysDictItem;
import com.dzics.data.pub.service.SysDictItemService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"字典数据Item值相关"}, produces = "字典数据Item值相关接口")
@RequestMapping("/dict/item")
@RestController
public class DictItemController {
    @Autowired
    SysDictItemService dictItemService;

    @OperLog(operModul = "字典item值相关", operType = OperType.ADD, operDesc = "新增字典item值", operatorType = "后台")
    @ApiOperation(value = "新增字典item值")
    @ApiOperationSupport(author = "jq", order = 1)
    @PostMapping
    public Result<SysDictItem> addDictItem(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                           @RequestBody @Valid DictItemVo dictItemVo){
        return  dictItemService.addDictItem(sub,dictItemVo);
    }

    @OperLog(operModul = "字典item值相关", operType = OperType.DEL, operDesc = "删除字典item值", operatorType = "后台")
    @ApiOperation(value = "删除字典item值")
    @ApiOperationSupport(author = "jq", order = 2)
    @DeleteMapping(value = "/{id}")
    public Result delDictItem(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                              @PathVariable("id")@ApiParam(value = "字典item值id(必填)",required = true) String id){
        return  dictItemService.delDictItem(sub,id);
    }

    @OperLog(operModul = "字典item值相关", operType = OperType.UPDATE, operDesc = "修改字典item值", operatorType = "后台")
    @ApiOperation(value = "修改字典item值")
    @ApiOperationSupport(author = "jq", order = 3)
    @PutMapping
    public Result<SysDictItem> updDictItem(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                           @RequestBody @Valid DictItemVo dictItemVo){
        return  dictItemService.updateDictItem(sub,dictItemVo);
    }

    @ApiOperation(value = "根据dict_code查询字典列表")
    @ApiOperationSupport(author = "jq", order = 4)
    @GetMapping(value = "/getItemListByCode")
    public Result<SysDictItem> getItemListByCode(@RequestParam("dictCode")@ApiParam(value = "字典item值的dictId(必填)",required = true) String dictCode){
        return  dictItemService.getItemListByCode(dictCode);
    }

}
