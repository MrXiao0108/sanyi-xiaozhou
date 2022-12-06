package com.dzics.data.business.controller.equipment;

import com.dzics.data.business.service.EquipmentService;
import com.dzics.data.common.base.annotation.OperLog;
import com.dzics.data.common.base.enums.OperType;
import com.dzics.data.common.base.model.vo.PutIsShowVo;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.PutEquipmentVo;
import com.dzics.data.pub.model.dto.SelectEquipmentVo;
import com.dzics.data.pub.model.vo.AddEquipmentVo;
import com.dzics.data.pub.model.vo.EquipmentDetis;
import com.dzics.data.pub.model.vo.EquipmentListDo;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = {"设备管理"}, produces = "设备管理相关接口")
@RestController
@RequestMapping("/equipment")
public class AddEquipmentController {

    @Autowired
    EquipmentService businessEquipmentService;


    @OperLog(operModul = "设备管理", operType = OperType.ADD, operDesc = "添加设备", operatorType = "后台")
    @ApiOperation(value = "添加设备")
    @ApiOperationSupport(author = "jq", order = 1)
    @PostMapping
    public Result add(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @RequestBody @Valid AddEquipmentVo addEquipmentVo){
        return  businessEquipmentService.add(sub,addEquipmentVo);
    }

    @ApiOperation(value = "设备列表")
    @ApiOperationSupport(author = "jq", order = 2)
    @GetMapping
    public Result<List<EquipmentListDo>> list(SelectEquipmentVo selectEquipmentVo,
                                              @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub
    ){
        return  businessEquipmentService.list(sub,selectEquipmentVo);
    }
    @ApiOperation(value = "根据id查询设备")
    @ApiOperationSupport(author = "jq", order = 2)
    @GetMapping("/{id}")
    public Result<EquipmentDetis> getById(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                          @PathVariable("id")String id
    ){
        return  businessEquipmentService.getById(sub,id);
    }

    @OperLog(operModul = "设备管理相关", operType = OperType.UPDATE, operDesc = "修改设备", operatorType = "后台")
    @ApiOperation(value = "修改设备")
    @ApiOperationSupport(author = "jq", order = 3)
    @PutMapping
    public Result put(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @RequestBody @Valid PutEquipmentVo putEquipmentVo
    ){
        return  businessEquipmentService.put(sub,putEquipmentVo);
    }

    @OperLog(operModul = "设备管理相关", operType = OperType.DEL, operDesc = "删除设备", operatorType = "后台")
    @ApiOperation(value = "删除设备")
    @ApiOperationSupport(author = "jq", order = 4)
    @DeleteMapping("/{id}")
    public Result del(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @PathVariable(value = "id",required = true)@ApiParam(value = "设备id")Long id
    ){
        return  businessEquipmentService.del(sub,id);
    }


    @OperLog(operModul = "设备管理相关", operType = OperType.DEL, operDesc = "控制设备看板是否显示", operatorType = "后台")
    @ApiOperation(value = "控制设备看板是否显示")
    @ApiOperationSupport(author = "jq", order = 4)
    @PutMapping("/putIsShow")
    public Result putIsShow(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                            @RequestBody @Valid PutIsShowVo putIsShowVo
                            ){
        return  businessEquipmentService.putIsShow(sub,putIsShowVo);
    }
}
